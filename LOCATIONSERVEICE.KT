package com.prithis.app

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class LocationForegroundService : Service() {

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var handlerThread: HandlerThread
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile private var trackId         = ""
    @Volatile private var authToken       = ""
    @Volatile private var lastLat         = Double.NaN
    @Volatile private var lastLng         = Double.NaN
    @Volatile private var lastSpeed       = 0f
    @Volatile private var lastAcc         = 999f
    @Volatile private var lastHeading     = 0f
    @Volatile private var lastAltitude    = 0.0
    @Volatile private var lastAltAccuracy = 0f
    @Volatile private var lastSpeedAcc    = 0f
    @Volatile private var lastHeadingAcc  = 0f
    @Volatile private var lastProvider    = "gps"

    private val pointBuffer  = mutableListOf<JSONObject>()
    private var lastBatchMs  = 0L
    private val BATCH_MS     = 150L
    private val MAX_BUF_SIZE = 3

    private val PUSH_MS      = 150L
    private val GPS_INTERVAL = 200L
    private val GPS_MIN      = 100L

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            android.util.Log.d("LocationService", "Stop service action received")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val resolvedTrackId = prefs.getString(PREF_TRACK_ID, null) ?: run {
            android.util.Log.e("LocationService", "TrackId is null in prefs, stopping service")
            stopSelf()
            return START_NOT_STICKY
        }
        authToken = prefs.getString(PREF_AUTH_TOKEN, null) ?: run {
            android.util.Log.e("LocationService", "AuthToken is null in prefs, stopping service")
            stopSelf()
            return START_NOT_STICKY
        }

        // CRITICAL: startForeground() MUST be called every time onStartCommand fires,
        // even on repeated calls from the keep-alive monitor. Android will ANR/kill
        // the service if startForeground() is not called within 5 seconds.
        val stopPi = PendingIntent.getService(
            this, 0,
            Intent(this, LocationForegroundService::class.java).apply { action = ACTION_STOP_SERVICE },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Diving — Sharing Location")
            .setContentText("Your live location is being shared")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPi)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this, NOTIF_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIF_ID, notification)
        }

        // If GPS loop is already running, just refresh the notification and return.
        if (trackId.isNotBlank()) {
            android.util.Log.d("LocationService", "Service already running (trackId=$trackId), notification refreshed")
            return START_STICKY
        }

        // First real start — initialise everything.
        trackId = resolvedTrackId
        android.util.Log.d("LocationService", "Service first start with trackId: $trackId")

        startLocationUpdates()
        startPushLoop()
        return START_STICKY
    }

    @android.annotation.SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        handlerThread = HandlerThread("DivingGpsThread").also { it.start() }

        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, GPS_INTERVAL)
            .setMinUpdateIntervalMillis(GPS_MIN)
            .setWaitForAccurateLocation(false)
            .setMaxUpdateDelayMillis(GPS_INTERVAL)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(r: LocationResult) {
                val best = r.locations
                    .filter { it.accuracy > 0 && it.accuracy <= 60f }
                    .minByOrNull { it.accuracy }
                    ?: r.locations.filter { it.accuracy > 0 }.minByOrNull { it.accuracy }
                    ?: return

                lastLat = best.latitude
                lastLng = best.longitude
                lastAcc = best.accuracy
                lastSpeed = if (best.hasSpeed() && best.speed >= 0) best.speed else 0f
                lastHeading = if (best.hasBearing()) best.bearing else 0f
                lastAltitude = if (best.hasAltitude()) best.altitude else lastAltitude
                lastProvider = best.provider ?: "gps"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    lastAltAccuracy = if (best.hasVerticalAccuracy()) best.verticalAccuracyMeters else lastAltAccuracy
                    lastSpeedAcc    = if (best.hasSpeedAccuracy()) best.speedAccuracyMetersPerSecond else lastSpeedAcc
                    lastHeadingAcc  = if (best.hasBearingAccuracy()) best.bearingAccuracyDegrees else lastHeadingAcc
                }
            }
        }

        fusedClient.requestLocationUpdates(req, locationCallback, handlerThread.looper)
    }

    private fun startPushLoop() {
        serviceScope.launch {
            delay(800L)
            android.util.Log.d("LocationService", "Push loop started")
            while (isActive) {
                if (trackId.isBlank()) { android.util.Log.e("LocationService", "TrackId blank, stopping loop"); break }
                val id = trackId
                if (!lastLat.isNaN() && !lastLng.isNaN()) {
                    try {
                        val pointObj = JSONObject().apply {
                            put("accuracy",        lastAcc.toDouble())
                            put("speed",           lastSpeed.toDouble())
                            put("heading",         lastHeading.toDouble())
                            put("altitude",        lastAltitude)
                            put("altAccuracy",     lastAltAccuracy.toDouble())
                            put("speedAccuracy",   lastSpeedAcc.toDouble())
                            put("headingAccuracy", lastHeadingAcc.toDouble())
                            put("provider",        lastProvider)
                            put("timestamp",       System.currentTimeMillis())
                        }

                        // Add encrypted coords with fallback
                        val encrypted = EncryptionUtil.encryptCoords(lastLat, lastLng)
                        if (encrypted.isNullOrBlank()) {
                            // fallback: send real coords
                            pointObj.put("lat", lastLat)
                            pointObj.put("lng", lastLng)
                        } else {
                            pointObj.put("encryptedCoords", encrypted)
                            // don't put lat/lng at all — server decrypts from encryptedCoords
                        }
                        synchronized(pointBuffer) { pointBuffer.add(pointObj) }

                        val nowMs         = System.currentTimeMillis()
                        val size          = synchronized(pointBuffer) { pointBuffer.size }
                        val timeSinceSend = nowMs - lastBatchMs

                        if (size >= MAX_BUF_SIZE || timeSinceSend >= BATCH_MS) {
                            val toSend = synchronized(pointBuffer) {
                                val copy = pointBuffer.toList(); pointBuffer.clear(); copy
                            }
                            if (toSend.isNotEmpty()) {
                                val (code, _) = if (toSend.size == 1) {
                                    val body = JSONObject().apply {
                                        put("trackId", id)
                                        val keys = toSend[0].keys()
                                        while (keys.hasNext()) { val k = keys.next() as String; put(k, toSend[0].opt(k)) }
                                    }
                                    httpPost("/api/location/update", body, authToken)
                                } else {
                                    val arr = JSONArray().also { a -> toSend.forEach { a.put(it) } }
                                    httpPost("/api/location/batch", JSONObject().apply { put("trackId", id); put("points", arr) }, authToken)
                                }
                                if (code in 200..299) {
                                    lastBatchMs = System.currentTimeMillis()
                                } else {
                                    // Re-queue failed points at the front
                                    synchronized(pointBuffer) {
                                        pointBuffer.addAll(0, toSend.takeLast(MAX_BUF_SIZE))
                                    }
                                    android.util.Log.e("LocationService", "Failed to send points: HTTP $code - re-queued ${toSend.size} points")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("LocationService", "Push loop error: ${e.message}", e)
                    }
                }
                delay(PUSH_MS)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID, "Diving Background Tracking", NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Shows while location is being shared in background" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(chan)
        }
    }

    override fun onDestroy() {
        android.util.Log.d("LocationService", "Service onDestroy called")
        super.onDestroy()
        // Reset trackId so if the service is restarted (START_STICKY), it re-initialises cleanly
        trackId = ""
        serviceScope.cancel()
        // Quit thread first to prevent callback firing during shutdown
        if (::handlerThread.isInitialized)    handlerThread.quitSafely()
        if (::locationCallback.isInitialized) fusedClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
