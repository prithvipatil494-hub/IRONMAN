package com.prithis.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.prithis.app.R
import com.google.android.gms.location.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolygonAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.geojson.Point as MapboxPoint
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.text.BasicTextField
import android.view.Gravity
import com.mapbox.maps.plugin.compass.compass

// ─── Config ───────────────────────────────────────────────────────────────────
const val GOOGLE_WEB_CLIENT_ID =
    "820558273332-3jmo1on8p0r33m76hoskl8v2v22gq1ng.apps.googleusercontent.com"
const val BACKEND = 
const val MAPBOX_TOKEN =
   
const val MAX_FRIENDS = 4

// ─── SharedPreferences keys ───────────────────────────────────────────────────
const val PREFS_NAME = "DivingPrefs"
const val PREF_TRACK_ID = "trackId"
const val PREF_UID = "uid"
const val PREF_DISPLAY_NAME = "displayName"
const val PREF_SAVED_FRIENDS = "savedFriends"
const val PREF_MAP_STYLE = "mapStyle"
const val PREF_PROFILE_PIC = "profilePic"
const val PREF_AUTH_TOKEN = "authToken"
const val PREF_TOKEN_ISSUED_AT = "tokenIssuedAt"
const val PREF_CACHED_EMAIL = "cachedEmail"

// ─── Foreground service constants ─────────────────────────────────────────────
const val CHANNEL_ID = "diving_bg_channel"
const val NOTIF_ID = 1001
const val ACTION_STOP_SERVICE = "com.prithis.app.STOP_SERVICE"
const val CHAT_CHANNEL_ID = "diving_chat_messages"
const val SOS_CHANNEL_ID = "diving_sos"
const val PREF_SOS_LAST_MS = "sosLastPressedMs"
const val SOS_COOLDOWN_MS = 4 * 60 * 60 * 1000L   // 4 hours
const val SOS_VIDEO_SECS = 20                     // auto-stop after 20 s

// ─── Timing ───────────────────────────────────────────────────────────────────
const val PUSH_INTERVAL = 150L
const val POLL_INTERVAL = 150L
private const val GPS_INTERVAL_MS = 100L
private const val GPS_MIN_DELAY_MS = 50L
private const val IS_RECENT_MS_CLIENT = 10_000L  // match server's 10s window

// ─── Dark / Black Theme Palette ─────────────────────────────────────────────
val DarkBg = Color(0xFF0E0F11)
val DarkSurface = Color(0xFF161718)
val DarkCard = Color(0xFF1E1F22)
val DarkCardAlt = Color(0xFF27282C)
val DarkBorder = Color(0xFF09090B)
val DarkBorderLight = Color(0xFF2C2D31)

val NeonGreen = Color(0xFF57F287)
val EmeraldGreen = Color(0xFF5865F2)
val EmeraldDeep = Color(0xFF4752C4)
val EmeraldMid = Color(0xFF7983F5)
val EmeraldPale = Color(0xFFB5BAF6)
val EmeraldGlow = Color(0xFF5865F2).copy(alpha = 0.15f)

val TextOnDark = Color(0xFFDBDEE1)
val TextOnDarkMuted = Color(0xFF80848E)
val TextOnDarkDim = Color(0xFF4E5058)

val GreenOnline = Color(0xFF57F287) // Bright green for better visibility
val RedRecord = Color(0xFFF23F43)
val AmberWarning = Color(0xFFF0B232)

val FriendColors =
    listOf(Color(0xFF00D4FF), Color(0xFF00C8FF), Color(0xFFFFD700), Color(0xFFFF6B9D))
val FriendColorHex = listOf("#00D4FF", "#00C8FF", "#FFD700", "#FF6B9D")
val FriendColorsMap = FriendColors.zip(FriendColorHex).toMap()

// Cached brushes for navigation — same as ChatScreen
private val BrushEmerald = Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen))
private val BrushDarkCard = Brush.linearGradient(listOf(DarkCard, DarkCardAlt))

val SkyPrimary = EmeraldGreen
val SkyLight = EmeraldMid
val SkyPale = EmeraldPale
val SkyDeep = EmeraldDeep
val SheetBg = DarkBg
val SheetCard = DarkCard
val SheetBorder = DarkBorder
val TextPrimary = TextOnDark
val TextSecondary = Color(0xFF8FA4B8)
val TextMuted = TextOnDarkMuted

// ─── Thresholds ───────────────────────────────────────────────────────────────
private const val ACC_GOOD = 30f
private const val ACC_ACCEPTABLE = 100f
private const val MIN_DIST = 1.0
private const val FORCE_MS = 2_000L
private const val MAX_SPEED_JUMP_MS = 55.0
private const val ACCURACY_WINDOW = 5

// ─── Map Style Enum ───────────────────────────────────────────────────────────
enum class MapTheme(val label: String, val icon: String) {
    DARK("Dark", "🌙"),
    LIGHT("Street", "🗺️"),
    SATELLITE("Satellite", "🛰️")
}

// Local coordinate class — replaces com.mapbox.geojson.Point
data class Point(private val lat: Double, private val lng: Double) {
    companion object {
        fun fromLngLat(longitude: Double, latitude: Double) = Point(lat = latitude, lng = longitude)
    }

    fun latitude() = lat
    fun longitude() = lng
}

fun Point.toMapboxPoint() = MapboxPoint.fromLngLat(longitude(), latitude())

// Simple user data class to replace FirebaseUser
data class AppUser(
    val uid: String,
    val email: String,
    val displayName: String = "",
    val trackId: String = ""
)

// ─── HTTP helpers ─────────────────────────────────────────────────────────────
fun httpGet(path: String, token: String? = null): JSONObject? = try {
    val conn = URL("$BACKEND$path").openConnection() as HttpURLConnection
    conn.connectTimeout = 5000; conn.readTimeout = 5000; conn.requestMethod = "GET"
    token?.let { conn.setRequestProperty("Authorization", "Bearer $it") }
    if (conn.responseCode == 200) JSONObject(conn.inputStream.bufferedReader().readText()) else null
} catch (_: Exception) {
    null
}

private fun httpGetArray(path: String, token: String? = null): JSONArray? = try {
    val conn = URL("$BACKEND$path").openConnection() as HttpURLConnection
    conn.connectTimeout = 5000; conn.readTimeout = 5000; conn.requestMethod = "GET"
    token?.let { conn.setRequestProperty("Authorization", "Bearer $it") }
    if (conn.responseCode == 200) JSONArray(conn.inputStream.bufferedReader().readText()) else null
} catch (_: Exception) {
    null
}

// Fast bulk location fetch for all tracked friends in one request
private fun httpGetBulkArray(path: String, token: String? = null): JSONArray? = try {
    val conn = URL("$BACKEND$path").openConnection() as HttpURLConnection
    conn.connectTimeout = 3000; conn.readTimeout = 3000; conn.requestMethod = "GET"
    token?.let { conn.setRequestProperty("Authorization", "Bearer $it") }
    if (conn.responseCode == 200) JSONArray(conn.inputStream.bufferedReader().readText()) else null
} catch (_: Exception) {
    null
}

fun httpPost(path: String, body: JSONObject, token: String? = null): Pair<Int, JSONObject?> = try {
    val conn = URL("$BACKEND$path").openConnection() as HttpURLConnection
    conn.connectTimeout = 8000; conn.readTimeout = 8000
    conn.requestMethod = "POST"; conn.doOutput = true
    conn.setRequestProperty("Content-Type", "application/json")
    token?.let { conn.setRequestProperty("Authorization", "Bearer $it") }
    OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }
    val code = conn.responseCode
    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
    code to JSONObject(stream.bufferedReader().readText())
} catch (_: Exception) {
    -1 to null  // -1 = network failure
}

fun httpPatch(path: String, body: JSONObject, token: String? = null): JSONObject? = try {
    val conn = URL("$BACKEND$path").openConnection() as HttpURLConnection
    conn.connectTimeout = 8000; conn.readTimeout = 8000
    conn.requestMethod = "PATCH"; conn.doOutput = true
    conn.setRequestProperty("Content-Type", "application/json")
    token?.let { conn.setRequestProperty("Authorization", "Bearer $it") }
    OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }
    if (conn.responseCode in 200..299)
        JSONObject(conn.inputStream.bufferedReader().readText()) else null
} catch (_: Exception) {
    null
}

fun httpDelete(path: String, token: String? = null): Boolean = try {
    val conn = URL("$BACKEND$path").openConnection() as HttpURLConnection
    conn.connectTimeout = 5000; conn.readTimeout = 5000; conn.requestMethod = "DELETE"
    token?.let { conn.setRequestProperty("Authorization", "Bearer $it") }
    conn.responseCode in 200..299
} catch (_: Exception) {
    false
}

fun ensureChatNotificationChannel(ctx: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(
        CHAT_CHANNEL_ID, "Chat Messages", NotificationManager.IMPORTANCE_DEFAULT
    ).apply { description = "Notifications for new chat messages" }
    manager.createNotificationChannel(channel)
}

fun showChatNotification(
    ctx: Context,
    friendId: String,
    friendName: String,
    message: String,
    notificationId: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) return
    val openIntent = Intent(ctx, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(EXTRA_NOTIF_TYPE, NOTIF_TYPE_CHAT)
        putExtra(EXTRA_NOTIF_FRIEND_ID, friendId)
        putExtra(EXTRA_NOTIF_FRIEND_NAME, friendName)
    }
    val pendingIntent = PendingIntent.getActivity(
        ctx, notificationId, openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val notification = NotificationCompat.Builder(ctx, CHAT_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_email)
        .setContentTitle(friendName.ifBlank { "New message" })
        .setContentText(message.ifBlank { "Sent a new message" })
        .setStyle(
            NotificationCompat.BigTextStyle().bigText(message.ifBlank { "Sent a new message" })
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()
    NotificationManagerCompat.from(ctx).notify(notificationId, notification)
}

fun accuracyLabel(acc: Float): Pair<String, Color> = when {
    acc < 20f -> "Excellent" to GreenOnline
    acc < 50f -> "Good" to EmeraldGreen
    acc < 100f -> "Fair" to AmberWarning
    else -> "Poor" to RedRecord
}

fun distM(a: Point, b: Point): Double {
    val r = 6_371_000.0
    val dLat = Math.toRadians(b.latitude() - a.latitude())
    val dLon = Math.toRadians(b.longitude() - a.longitude())
    val x = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(a.latitude())) * cos(Math.toRadians(b.latitude())) *
            sin(dLon / 2).pow(2)
    return r * 2 * asin(sqrt(x))
}

// ─── Smooth map centering ───────────────────────────────────────────────────────
fun smoothCenterMap(map: com.mapbox.maps.MapboxMap, target: Point, currentZoom: Double) {
    val current = map.cameraState.center
    val distance = distM(
        Point.fromLngLat(current.longitude(), current.latitude()),
        target
    )

    // Only fly if moved > 50m
    if (distance > 50) {
        map.flyTo(
            CameraOptions.Builder()
                .center(target.toMapboxPoint())
                .zoom(currentZoom)
                .build(),
            com.mapbox.maps.plugin.animation.MapAnimationOptions.mapAnimationOptions {
                duration(800) // 800ms ease animation
                interpolator(android.view.animation.DecelerateInterpolator(1.5f)) // Smooth deceleration
            }
        )
    }
}

// ─── Accuracy circle geometry ─────────────────────────────────────────────────
// Returns a single outer ring (list of MapboxPoint) forming a ~64-sided polygon
// centred at [centre] with radius [radiusM] metres.
fun accuracyCirclePoints(centre: Point, radiusM: Double, steps: Int = 64): List<List<MapboxPoint>> {
    val latRad = Math.toRadians(centre.latitude())
    val ring = (0..steps).map { i ->
        val angle = 2.0 * Math.PI * i / steps
        val dLat = radiusM * cos(angle) / 111_320.0
        val dLng = radiusM * sin(angle) / (111_320.0 * cos(latRad))
        MapboxPoint.fromLngLat(centre.longitude() + dLng, centre.latitude() + dLat)
    }
    return listOf(ring)
}

// ─── Socket.IO Manager ────────────────────────────────────────────────────────
object SocketManager {
    private var socket: io.socket.client.Socket? = null
    private var isConnected = false

    fun connect(serverUrl: String, myTrackId: String,
                onFriendLocation: (String, JSONObject) -> Unit,
                onConnected: () -> Unit,
                onDisconnected: () -> Unit) {
        try {
            val opts = io.socket.client.IO.Options().apply {
                transports = arrayOf("websocket")
                reconnection = true
                reconnectionAttempts = Int.MAX_VALUE
                reconnectionDelay = 1000
                timeout = 10000
            }
            socket = io.socket.client.IO.socket(serverUrl, opts)

            socket?.on(io.socket.client.Socket.EVENT_CONNECT) {
                isConnected = true
                onConnected()
                // Join personal room
                socket?.emit("user:join", myTrackId)
            }

            socket?.on(io.socket.client.Socket.EVENT_DISCONNECT) {
                isConnected = false
                onDisconnected()
            }

            // Real-time friend location updates (server pushes these)
            socket?.on("friend:location") { args ->
                try {
                    val data = args[0] as org.json.JSONObject
                    val trackId = data.optString("trackId")
                    if (trackId.isNotBlank()) {
                        onFriendLocation(trackId, data)
                    }
                } catch (_: Exception) {}
            }

            // New server-side interpolation: 10Hz location updates with isReal/isInterpolated flags
            socket?.on("location:updated") { args ->
                try {
                    val data = args[0] as org.json.JSONObject
                    val trackId = data.optString("trackId")
                    if (trackId.isNotBlank()) {
                        onFriendLocation(trackId, data)
                    }
                } catch (_: Exception) {}
            }

            socket?.connect()
        } catch (e: Exception) {
            android.util.Log.e("Socket", "Connection failed: ${e.message}")
        }
    }

    fun watchFriend(myTrackId: String, friendTrackId: String) {
        socket?.emit("watch:friend", JSONObject().apply {
            put("myTrackId", myTrackId)
            put("friendTrackId", friendTrackId)
        })
    }

    fun unwatchFriend(myTrackId: String, friendTrackId: String) {
        socket?.emit("unwatch:friend", JSONObject().apply {
            put("myTrackId", myTrackId)
            put("friendTrackId", friendTrackId)
        })
        // Clear interpolation state for this friend
        // Note: This would need access to the smoothRenderer if using the separate class
        // For now, the displayPositions map will naturally clear when the friend is removed
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
        isConnected = false
    }

    val connected get() = isConnected
}

// ─── Data models ──────────────────────────────────────────────────────────────
data class LocationData(
    val point: Point, val accuracy: Float,
    val speedKmh: Float = 0f, val address: String = "Fetching address…"
)

data class FriendLocation(
    val point: Point, val accuracy: Float,
    val speedKmh: Float, val isRecent: Boolean,
    val heading: Float = 0f,
    val altitude: Double = 0.0,
    val extrapolated: Boolean = false,
    val snapped: Boolean = false,
    val roadName: String = "",
    val isReal: Boolean = true,
    val isInterpolated: Boolean = false,
    val confidence: Float = 1.0f
)

fun createCircularAvatarBitmap(
    displayName: String,
    avatarBase64: String,
    colorHex: String,
    sizePx: Int = 120
): android.graphics.Bitmap {
    val pinH = (sizePx * 0.28f).toInt()   // height of the Life360-style triangle pin
    val total = sizePx + pinH
    val bmp = android.graphics.Bitmap.createBitmap(
        sizePx,
        total,
        android.graphics.Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bmp)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f

    val decoded = if (avatarBase64.isNotBlank()) {
        try {
            val bytes = android.util.Base64.decode(avatarBase64, android.util.Base64.DEFAULT)
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        }
    } else null

    // 1. Drop shadow
    paint.color = android.graphics.Color.argb(70, 0, 0, 0)
    paint.style = android.graphics.Paint.Style.FILL
    canvas.drawCircle(cx + 3f, cy + 3f, cx - 2f, paint)

    // 2. White outer ring
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(cx, cy, cx - 2f, paint)

    // 3. Colored accent ring
    paint.color = android.graphics.Color.parseColor(colorHex)
    canvas.drawCircle(cx, cy, cx - 8f, paint)

    // 4. Inner content — photo or initial
    val innerR = cx - 14f
    if (decoded != null) {
        val scaled = android.graphics.Bitmap.createScaledBitmap(decoded, sizePx, sizePx, true)
        paint.shader = android.graphics.BitmapShader(
            scaled,
            android.graphics.Shader.TileMode.CLAMP,
            android.graphics.Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, innerR, paint)
        paint.shader = null
    } else {
        val base = android.graphics.Color.parseColor(colorHex)
        val r = (android.graphics.Color.red(base) * 0.65f).toInt()
        val g = (android.graphics.Color.green(base) * 0.65f).toInt()
        val b = (android.graphics.Color.blue(base) * 0.65f).toInt()
        paint.color = android.graphics.Color.rgb(r, g, b)
        canvas.drawCircle(cx, cy, innerR, paint)
        paint.color = android.graphics.Color.WHITE
        paint.textSize = sizePx * 0.36f
        paint.textAlign = android.graphics.Paint.Align.CENTER
        paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        val initial = displayName.firstOrNull()?.uppercase() ?: "?"
        canvas.drawText(initial, cx, cy - (paint.descent() + paint.ascent()) / 2f, paint)
    }

    // 5. Triangle pin — white outer shape
    paint.style = android.graphics.Paint.Style.FILL
    paint.color = android.graphics.Color.WHITE
    val pinW = cx * 0.42f
    val pathOuter = android.graphics.Path()
    pathOuter.moveTo(cx - pinW, sizePx - 5f)
    pathOuter.lineTo(cx + pinW, sizePx - 5f)
    pathOuter.lineTo(cx, total.toFloat() - 2f)
    pathOuter.close()
    canvas.drawPath(pathOuter, paint)

    // 6. Triangle pin — colored inner fill
    paint.color = android.graphics.Color.parseColor(colorHex)
    val pinWI = pinW * 0.62f
    val pathInner = android.graphics.Path()
    pathInner.moveTo(cx - pinWI, sizePx.toFloat() + 1f)
    pathInner.lineTo(cx + pinWI, sizePx.toFloat() + 1f)
    pathInner.lineTo(cx, total.toFloat() - 5f)
    pathInner.close()
    canvas.drawPath(pathInner, paint)

    return bmp
}

data class SavedFriend(
    val trackId: String, val displayName: String, val email: String,
    val avatarBase64: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("trackId", trackId)
        put("displayName", displayName)
        put("email", email)
        put("avatarBase64", avatarBase64)
    }

    companion object {
        fun fromJson(obj: JSONObject) = SavedFriend(
            trackId = obj.getString("trackId"),
            displayName = obj.optString("displayName", ""),
            email = obj.optString("email", ""),
            avatarBase64 = obj.optString("avatarBase64", "")
        )
    }
}

// ─── Image helpers ────────────────────────────────────────────────────────────
fun uriToBase64(ctx: Context, uri: Uri, maxPx: Int = 256): String? = try {
    val stream = ctx.contentResolver.openInputStream(uri) ?: return null
    val orig = BitmapFactory.decodeStream(stream)
    stream.close()
    val side = minOf(orig.width, orig.height)
    val x = (orig.width - side) / 2
    val y = (orig.height - side) / 2
    val cropped = Bitmap.createBitmap(orig, x, y, side, side)
    val scaled = Bitmap.createScaledBitmap(cropped, maxPx, maxPx, true)
    val out = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, 75, out)
    Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
} catch (_: Exception) {
    null
}

fun base64ToBitmap(b64: String?): Bitmap? {
    if (b64.isNullOrBlank()) return null
    return try {
        val bytes = Base64.decode(b64, Base64.NO_WRAP)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}

fun makeCircularAvatarBitmap(src: Bitmap, ringColorHex: String, sizePx: Int = 120): Bitmap {
    val result = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = android.graphics.Color.parseColor(ringColorHex)
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, paint)
    val inner = sizePx / 2f - 5f
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    val scaled = Bitmap.createScaledBitmap(src, sizePx - 10, sizePx - 10, true)
    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val tempBmp = Bitmap.createBitmap(sizePx - 10, sizePx - 10, Bitmap.Config.ARGB_8888)
    val tempCanvas = Canvas(tempBmp)
    val clipPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    clipPaint.color = android.graphics.Color.BLACK
    tempCanvas.drawCircle(inner, inner, inner, clipPaint)
    clipPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    tempCanvas.drawBitmap(scaled, 0f, 0f, clipPaint)
    canvas.drawBitmap(tempBmp, 5f, 5f, null)
    return result
}

// Arrival alert model, save/load, notifications moved to ArrivalAlerts.kt

fun ensureSosNotificationChannel(ctx: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val ch = NotificationChannel(
        SOS_CHANNEL_ID,
        "SOS Alerts",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Emergency SOS alerts from contacts"
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 400, 200, 400, 200, 400)
    }
    manager.createNotificationChannel(ch)
}

fun showSosNotification(ctx: Context, senderName: String, notifId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) return
    val pi = PendingIntent.getActivity(
        ctx, notifId,
        Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NOTIF_TYPE, NOTIF_TYPE_MAP)
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val notification = NotificationCompat.Builder(ctx, SOS_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("🚨 SOS — $senderName needs help!")
        .setContentText("$senderName has triggered an emergency SOS alert.")
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("$senderName has triggered an emergency SOS. Open the app to see their location.")
        )
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setAutoCancel(true)
        .setColor(0xFFFF4757.toInt())
        .setContentIntent(pi)
        .build()
    NotificationManagerCompat.from(ctx).notify(notifId, notification)
}

// ensureArrivalNotificationChannel, showEtaOverdueNotification, showArrivalNotification,
// createDestinationMarkerBitmap moved to ArrivalAlerts.kt

const val EXTRA_NOTIF_FRIEND_ID = "notif_friend_id"
const val EXTRA_NOTIF_FRIEND_NAME = "notif_friend_name"
const val EXTRA_NOTIF_TYPE = "notif_type"
const val NOTIF_TYPE_CHAT = "chat"
const val NOTIF_TYPE_MAP = "map"

data class NotifEvent(
    val type: String,
    val friendId: String? = null,
    val friendName: String? = null,
    val seq: Long = System.nanoTime()  // unique per tap so same-friend notifs always re-trigger
)

enum class NavTab { MAP, CONTACTS, CHAT, PROFILE }

// ─── Activity ─────────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Single Compose-observable event; unique seq ensures same-friend taps always fire
    private val notifEvent = androidx.compose.runtime.mutableStateOf<NotifEvent?>(null)

    private fun Intent.toNotifEvent(): NotifEvent? {
        val type = getStringExtra(EXTRA_NOTIF_TYPE) ?: return null
        return NotifEvent(
            type = type,
            friendId = getStringExtra(EXTRA_NOTIF_FRIEND_ID),
            friendName = getStringExtra(EXTRA_NOTIF_FRIEND_NAME)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Coil with larger cache for smooth scrolling
        val imageLoader = coil.ImageLoader.Builder(this)
            .memoryCache {
                coil.memory.MemoryCache.Builder(this)
                    .maxSizePercent(0.50) // 50% of available memory — prevents eviction on scroll
                    .build()
            }
            .diskCache {
                coil.disk.DiskCache.Builder()
                    .directory(filesDir.resolve("image_cache"))  // filesDir survives OS cache clearing
                    .maxSizeBytes(500L * 1024 * 1024) // 500 MB disk cache for more persistent image storage
                    .build()
            }
            .build()
        coil.Coil.setImageLoader(imageLoader)

        // ADD THIS LINE — enables edge-to-edge so statusBarsPadding() works correctly
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)

        // ADD THIS — tells the window to resize when keyboard opens
        window.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notifEvent.value = intent?.toNotifEvent()
        setContent {
            AppRoot(fusedLocationClient, notifEvent.value) { locationCallback = it }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notifEvent.value = intent.toNotifEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::locationCallback.isInitialized)
            fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

// ─── Local Location Cache ─────────────────────────────────────────────────────
object LocationCache {
    private const val PREF_NAME = "LocationCache"
    private const val MAX_AGE_MS = 24 * 60 * 60 * 1000L // 24 hours

    fun saveMyLocation(ctx: Context, point: Point, accuracy: Float, speedKmh: Float) {
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().apply {
            putFloat("my_lat", point.latitude().toFloat())
            putFloat("my_lng", point.longitude().toFloat())
            putFloat("my_acc", accuracy)
            putFloat("my_spd", speedKmh)
            putLong("my_ts", System.currentTimeMillis())
            apply()
        }
    }

    fun loadMyLocation(ctx: Context): LocationData? {
        val prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val ts = prefs.getLong("my_ts", 0L)
        if (ts == 0L || System.currentTimeMillis() - ts > MAX_AGE_MS) return null
        val lat = prefs.getFloat("my_lat", 0f).toDouble()
        val lng = prefs.getFloat("my_lng", 0f).toDouble()
        if (lat == 0.0 && lng == 0.0) return null
        return LocationData(
            point = Point.fromLngLat(lng, lat),
            accuracy = prefs.getFloat("my_acc", 0f),
            speedKmh = prefs.getFloat("my_spd", 0f),
            address = "Last known location"
        )
    }

    fun saveFriendLocation(ctx: Context, trackId: String, loc: FriendLocation) {
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().apply {
            putFloat("f_${trackId}_lat", loc.point.latitude().toFloat())
            putFloat("f_${trackId}_lng", loc.point.longitude().toFloat())
            putFloat("f_${trackId}_acc", loc.accuracy)
            putFloat("f_${trackId}_spd", loc.speedKmh)
            putFloat("f_${trackId}_hdg", loc.heading)
            putLong("f_${trackId}_ts", System.currentTimeMillis())
            apply()
        }
    }

    fun loadFriendLocation(ctx: Context, trackId: String): FriendLocation? {
        val prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val ts = prefs.getLong("f_${trackId}_ts", 0L)
        if (ts == 0L || System.currentTimeMillis() - ts > MAX_AGE_MS) return null
        val lat = prefs.getFloat("f_${trackId}_lat", 0f).toDouble()
        val lng = prefs.getFloat("f_${trackId}_lng", 0f).toDouble()
        if (lat == 0.0 && lng == 0.0) return null
        return FriendLocation(
            point = Point.fromLngLat(lng, lat),
            accuracy = prefs.getFloat("f_${trackId}_acc", 0f),
            speedKmh = prefs.getFloat("f_${trackId}_spd", 0f),
            isRecent = false, // cached = not live
            heading = prefs.getFloat("f_${trackId}_hdg", 0f)
        )
    }

    fun loadAllFriendLocations(ctx: Context, trackIds: List<String>): Map<String, FriendLocation> {
        return trackIds.mapNotNull { id ->
            loadFriendLocation(ctx, id)?.let { id to it }
        }.toMap()
    }
}

// ─── Root ─────────────────────────────────────────────────────────────────────
@Composable
fun AppRoot(
    fused: FusedLocationProviderClient,
    notifEvent: NotifEvent? = null,
    onCb: (LocationCallback) -> Unit
) {
    val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    var user by remember { mutableStateOf<AppUser?>(null) }
    var token by remember { mutableStateOf<String?>(prefs.getString(PREF_AUTH_TOKEN, null)) }
    var authChecked by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (token == null) {
            authChecked = true
            return@LaunchedEffect
        }

        // ── Local cache check first — skip network entirely if token is fresh ──
        val tokenIssuedAt = prefs.getLong(PREF_TOKEN_ISSUED_AT, 0L)
        val tokenAgeMs = System.currentTimeMillis() - tokenIssuedAt
        val SIX_DAYS_MS = 6 * 24 * 60 * 60 * 1000L

        val cachedUid = prefs.getString(PREF_UID, null)
        val cachedTrackId = prefs.getString(PREF_TRACK_ID, null)
        val cachedName = prefs.getString(PREF_DISPLAY_NAME, null)
        val cachedEmail = prefs.getString(PREF_CACHED_EMAIL, null)

        if (tokenAgeMs < SIX_DAYS_MS && cachedUid != null && cachedTrackId != null) {
            // Token is fresh and we have cached user data — restore immediately
            // without any network call
            user = AppUser(
                uid = cachedUid,
                email = cachedEmail ?: "",
                displayName = cachedName ?: "",
                trackId = cachedTrackId
            )
            authChecked = true
            return@LaunchedEffect
        }

        // Token is old or cache is incomplete — verify with server
        val (code, response) = withContext(Dispatchers.IO) {
            httpPost("/api/auth/verify-token", JSONObject(), token)
        }
        when {
            response?.optBoolean("success") == true -> {
                val userObj = response.optJSONObject("user")
                val restoredUser = AppUser(
                    uid = userObj?.optString("uid") ?: "",
                    email = userObj?.optString("email") ?: "",
                    displayName = userObj?.optString("displayName") ?: "",
                    trackId = userObj?.optString("trackId") ?: ""
                )
                user = restoredUser
                // Refresh the cache
                prefs.edit()
                    .putLong(PREF_TOKEN_ISSUED_AT, System.currentTimeMillis())
                    .putString(PREF_UID, restoredUser.uid)
                    .putString(PREF_TRACK_ID, restoredUser.trackId)
                    .putString(PREF_DISPLAY_NAME, restoredUser.displayName)
                    .putString(PREF_CACHED_EMAIL, restoredUser.email)
                    .apply()
            }
            code == 403 || code == 401 -> {
                // Genuinely expired — clear and force re-login
                prefs.edit()
                    .remove(PREF_AUTH_TOKEN)
                    .remove(PREF_TOKEN_ISSUED_AT)
                    .remove(PREF_UID)
                    .remove(PREF_TRACK_ID)
                    .apply()
                token = null
                user = null
            }
            // code == -1 means network failure
            // Keep token, try to restore from cache even if stale
            else -> {
                if (cachedUid != null && cachedTrackId != null) {
                    user = AppUser(
                        uid = cachedUid,
                        email = cachedEmail ?: "",
                        displayName = cachedName ?: "",
                        trackId = cachedTrackId
                    )
                }
            }
        }
        authChecked = true
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = EmeraldGreen,
            secondary = NeonGreen,
            background = DarkBg,
            surface = DarkSurface,
            onSurface = TextOnDark,
            onPrimary = Color.White,
            surfaceVariant = DarkCard,
            outline = DarkBorderLight
        ),
        typography = Typography(
            displayLarge = TextStyle(fontWeight = FontWeight.ExtraBold),
            displayMedium = TextStyle(fontWeight = FontWeight.ExtraBold),
            displaySmall = TextStyle(fontWeight = FontWeight.Bold),
            headlineLarge = TextStyle(fontWeight = FontWeight.Bold),
            headlineMedium = TextStyle(fontWeight = FontWeight.Bold),
            headlineSmall = TextStyle(fontWeight = FontWeight.Bold),
            titleLarge = TextStyle(fontWeight = FontWeight.Bold),
            titleMedium = TextStyle(fontWeight = FontWeight.SemiBold),
            titleSmall = TextStyle(fontWeight = FontWeight.SemiBold),
            bodyLarge = TextStyle(fontWeight = FontWeight.Medium),
            bodyMedium = TextStyle(fontWeight = FontWeight.Medium),
            bodySmall = TextStyle(fontWeight = FontWeight.Medium),
            labelLarge = TextStyle(fontWeight = FontWeight.SemiBold),
            labelMedium = TextStyle(fontWeight = FontWeight.SemiBold),
            labelSmall = TextStyle(fontWeight = FontWeight.Medium)
        )
    ) {
        when {
            !authChecked -> {
                // Show splash/loading instead of login screen while checking cache
                Box(
                    Modifier.fillMaxSize().background(DarkBg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Brush.linearGradient(listOf(EmeraldDeep, NeonGreen))),
                            Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(56.dp)
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Diving", fontSize = 36.sp, fontWeight = FontWeight.Black,
                            color = TextOnDark)
                        Spacer(Modifier.height(16.dp))
                        CircularProgressIndicator(color = EmeraldGreen, strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp))
                    }
                }
            }
            user == null -> AuthScreen { loggedInUser, loggedInToken ->
                user = loggedInUser
                token = loggedInToken
                prefs.edit()
                    .putString(PREF_AUTH_TOKEN, loggedInToken)
                    .putLong(PREF_TOKEN_ISSUED_AT, System.currentTimeMillis())
                    .putString(PREF_UID, loggedInUser.uid)
                    .putString(PREF_TRACK_ID, loggedInUser.trackId)
                    .putString(PREF_DISPLAY_NAME, loggedInUser.displayName)
                    .putString(PREF_CACHED_EMAIL, loggedInUser.email)
                    .apply()
            }
            else -> MainApp(fused, onCb, user!!, token, notifEvent) {
                prefs.edit()
                    .remove(PREF_AUTH_TOKEN)
                    .remove(PREF_TOKEN_ISSUED_AT)
                    .apply()
                token = null
                user = null
            }
        }
    }
}

// ─── Auth Screen ──────────────────────────────────────────────────────────────
@Composable
fun AuthScreen(onSignedIn: (AppUser, String) -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Google Sign-In launcher
    val googleLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken != null) {
                    loading = true
                    scope.launch(Dispatchers.IO) {
                        val body = JSONObject().apply { put("idToken", idToken) }
                        val (_, response) = httpPost("/api/auth/google", body)

                        withContext(Dispatchers.Main) {
                            loading = false
                            if (response?.optBoolean("success") == true) {
                                val token = response.optString("token")
                                val userObj = response.optJSONObject("user")
                                val user = AppUser(
                                    uid = userObj?.optString("uid") ?: "",
                                    email = userObj?.optString("email") ?: "",
                                    displayName = userObj?.optString("displayName") ?: "",
                                    trackId = userObj?.optString("trackId") ?: ""
                                )
                                onSignedIn(user, token)
                            } else {
                                errorMessage =
                                    response?.optString("error") ?: "Google authentication failed"
                            }
                        }
                    }
                } else {
                    errorMessage = "Failed to get Google ID token"
                }
            } catch (e: ApiException) {
                errorMessage = "Google Sign-In failed: ${e.message}"
            }
        }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(DarkBg), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(320.dp)
                .align(Alignment.Center)
                .offset(y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            EmeraldGreen.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ), CircleShape
                )
        )
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(EmeraldDeep, NeonGreen))),
                Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Diving",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = TextOnDark,
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Real-time location sharing",
                fontSize = 13.sp,
                color = TextOnDarkMuted,
                letterSpacing = 0.3.sp
            )
            Spacer(Modifier.height(48.dp))

            // Google Sign-In Button
            if (loading) {
                Box(Modifier
                    .fillMaxWidth()
                    .height(56.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EmeraldGreen, strokeWidth = 2.dp)
                }
            } else {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .border(1.dp, DarkBorderLight, RoundedCornerShape(16.dp))
                        .clickable {
                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                                    .requestEmail()
                                    .build()
                            val googleSignInClient = GoogleSignIn.getClient(ctx, gso)
                            googleLauncher.launch(googleSignInClient.signInIntent)
                        }, contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "G",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color(0xFF4285F4)
                        )
                        Spacer(Modifier.width(14.dp))
                        Text(
                            "Continue with Google",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextOnDark
                        )
                    }
                }
            }

            errorMessage?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = RedRecord, fontSize = 12.sp, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(32.dp))
            Text(
                "Google Authentication · MongoDB secured",
                fontSize = 11.sp,
                color = TextOnDarkDim,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Main App ─────────────────────────────────────────────────────────────────
@Composable
fun MainApp(
    fused: FusedLocationProviderClient,
    onCb: (LocationCallback) -> Unit,
    user: AppUser,
    authToken: String?,
    notifEvent: NotifEvent? = null,
    onSignOut: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var myLocation by remember {
        // Load cached location synchronously on first composition
        mutableStateOf(LocationCache.loadMyLocation(ctx))
    }
    var rawPoint by remember { mutableStateOf<Point?>(null) }
    var lastGeocodedPoint by remember { mutableStateOf<Point?>(null) }
    var rawAccuracy by remember { mutableStateOf(0f) }
    var rawSpeedMs by remember { mutableStateOf(0f) }
    var rawHeading by remember { mutableStateOf(0f) }
    var gpsAvailable by remember { mutableStateOf(true) }

    val localPointBuffer = remember { mutableListOf<JSONObject>() }
    var lastBatchSentMs by remember { mutableStateOf(0L) }
    val BATCH_SEND_MS = 150L
    val MAX_BUFFER_SIZE = 3

    var myTrackId by remember { mutableStateOf(prefs.getString(PREF_TRACK_ID, null) ?: "Loading…") }
    var trackIdReady by remember { mutableStateOf(prefs.contains(PREF_TRACK_ID)) }
    var isConnected by remember { mutableStateOf(false) }
    var bgTrackingOn by remember { mutableStateOf(prefs.getBoolean("bgTrackingOn", false)) }

    var pathPoints by remember { mutableStateOf<List<Point>>(emptyList()) }
    var isRecording by remember { mutableStateOf(false) }
    var sessionId by remember { mutableStateOf<String?>(null) }
    var lastRecPoint by remember { mutableStateOf<Point?>(null) }
    var lastRecTimeMs by remember { mutableStateOf(0L) }

    val trackedIds = remember { mutableStateListOf<String>() }
    val friendLocations = remember { mutableStateMapOf<String, FriendLocation?>() }

    // ── Server-first mode: DISABLED - using local GPS with StationaryGate instead ─
    // Local GPS has better stationary detection and anti-flickering
    val useServerFirstMode = false

    // ── Smooth display positions (interpolated 30fps, separate from raw data) ─────
    val displayPositions = remember { mutableStateMapOf<String, FriendLocation>() }
    val displayMyLocation = remember {
        mutableStateOf(LocationCache.loadMyLocation(ctx))
    }

    // ── Time-based interpolation — tuned for stability ───────────────────
    val lastTargetTime = remember { mutableStateMapOf<String, Long>() }
    LaunchedEffect(Unit) {
        var frameTime = System.currentTimeMillis()
        while (true) {
            delay(33L) // ~30fps is enough — 25ms was drawing more frames than needed
            val now = System.currentTimeMillis()
            val dtSec = ((now - frameTime) / 1000.0).coerceIn(0.001, 0.2)
            frameTime = now

            friendLocations.forEach { (id, target) ->
                if (target == null) return@forEach
                val current = displayPositions[id] ?: run {
                    displayPositions[id] = target; return@forEach
                }
                if (distM(current.point, target.point) > 150.0) {
                    displayPositions[id] = target; return@forEach
                }
                // Slower tau = more smoothing. Fast=0.12, slow=0.35
                val tau = when {
                    target.speedKmh > 20f -> 0.10f
                    target.speedKmh > 5f  -> 0.18f
                    else                  -> 0.40f  // ← was 0.12, stationary was too jittery
                }
                val alpha = 1f - kotlin.math.exp(-dtSec / tau).toFloat()
                val newLat = current.point.latitude()  + alpha * (target.point.latitude()  - current.point.latitude())
                val newLng = current.point.longitude() + alpha * (target.point.longitude() - current.point.longitude())
                displayPositions[id] = target.copy(point = Point.fromLngLat(newLng, newLat))
            }

            val rawLoc = myLocation
            if (rawLoc != null) {
                val cur = displayMyLocation.value
                if (cur == null) {
                    displayMyLocation.value = rawLoc
                } else if (distM(cur.point, rawLoc.point) > 150.0) {
                    displayMyLocation.value = rawLoc
                } else {
                    val tau = when {
                        rawLoc.speedKmh > 20f -> 0.10f
                        rawLoc.speedKmh > 5f  -> 0.18f
                        else                  -> 0.40f
                    }
                    val alpha = 1f - kotlin.math.exp(-dtSec / tau).toFloat()
                    val newLat = cur.point.latitude()  + alpha * (rawLoc.point.latitude()  - cur.point.latitude())
                    val newLng = cur.point.longitude() + alpha * (rawLoc.point.longitude() - cur.point.longitude())
                    displayMyLocation.value = cur.copy(
                        point    = Point.fromLngLat(newLng, newLat),
                        accuracy = cur.accuracy  + alpha * (rawLoc.accuracy  - cur.accuracy),
                        speedKmh = cur.speedKmh  + alpha * (rawLoc.speedKmh  - cur.speedKmh)
                    )
                }
            }
        }
    }

    val savedFriends = remember {
        mutableStateListOf<SavedFriend>().also { list ->
            val cached = prefs.getString(PREF_SAVED_FRIENDS, null)
            if (cached != null) {
                try {
                    val arr = JSONArray(cached)
                    repeat(arr.length()) { list.add(SavedFriend.fromJson(arr.getJSONObject(it))) }
                } catch (_: Exception) {
                }
            }
        }
    }

    var myAvatarBase64 by remember {
        mutableStateOf(prefs.getString(PREF_PROFILE_PIC, "") ?: "")
    }
    var myAvatarVersion by remember { mutableStateOf(0) }

    val friendAvatars = remember {
        mutableStateMapOf<String, String>().also { map ->
            savedFriends.forEach { sf ->
                if (sf.avatarBase64.isNotBlank()) map[sf.trackId] = sf.avatarBase64
            }
        }
    }

    var showFriendDialog by remember { mutableStateOf(false) }
    var initialChatTrackId by remember { mutableStateOf<String?>(null) }  // used to pre-populate chat tab
    var initialChatName by remember { mutableStateOf<String?>(null) }  // used to pre-populate chat tab
    var isChatConvOpen by remember { mutableStateOf(false) }          // true when a conversation is open full-screen
    var viewingProfile by remember { mutableStateOf<SavedFriend?>(null) }

    var arrivalAlerts by remember { mutableStateOf(loadArrivalAlerts(ctx)) }
    var pendingAlertPoint by remember { mutableStateOf<Point?>(null) }
    var mapAlertPlacementMode by remember { mutableStateOf(false) }

    // ── Socket.IO real-time connection ──────────────────────────────────────────
    var socketConnected by remember { mutableStateOf(false) }

    LaunchedEffect(trackIdReady, myTrackId) {
        if (!trackIdReady || myTrackId.isBlank() || myTrackId == "Loading…") return@LaunchedEffect

        withContext(Dispatchers.IO) {
            SocketManager.connect(
                serverUrl = BACKEND,
                myTrackId = myTrackId,
                onFriendLocation = { friendId, data ->
                    if (BlockStore.isBlocked(ctx, friendId)) return@connect

                    val ec = data.optString("encryptedCoords", "")
                    val coords = if (ec.isNotBlank()) EncryptionUtil.decryptCoords(ec) else null
                    val lat = coords?.first ?: data.optDouble("lat", Double.NaN)
                    val lng = coords?.second ?: data.optDouble("lng", Double.NaN)
                    if (lat.isNaN() || lng.isNaN()) return@connect

                    val isReal = data.optBoolean("isReal", true)
                    val isInterpolated = data.optBoolean("isInterpolated", false)
                    val confidence = data.optDouble("confidence", 1.0).toFloat()

                    val newLoc = FriendLocation(
                        point        = Point.fromLngLat(lng, lat),
                        accuracy     = data.optDouble("accuracy", 0.0).toFloat(),
                        speedKmh     = (data.optDouble("speed", 0.0) * 3.6).toFloat(),
                        isRecent     = data.optBoolean("isRecent", true),
                        heading      = data.optDouble("heading", 0.0).toFloat(),
                        altitude     = data.optDouble("altitude", 0.0),
                        extrapolated = data.optBoolean("extrapolated", false),
                        snapped      = data.optBoolean("snapped", false),
                        roadName     = data.optString("roadName", ""),
                        isReal       = isReal,
                        isInterpolated = isInterpolated,
                        confidence   = confidence
                    )

                    // Post to main thread
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        friendLocations[friendId] = newLoc
                        // ── Save to local cache ──
                        LocationCache.saveFriendLocation(ctx, friendId, newLoc)

                        // Arrival alert check
                        val triggered = arrivalAlerts.filter { alert: ArrivalAlert ->
                            alert.contactId == friendId &&
                            distM(newLoc.point, Point.fromLngLat(alert.lng, alert.lat)) <= alert.radiusMeters
                        }
                        if (triggered.isNotEmpty()) {
                            triggered.forEach { alert ->
                                showArrivalNotification(ctx, alert.contactName, "Destination", alert.id.hashCode())
                            }
                            arrivalAlerts = arrivalAlerts.filter { a -> triggered.none { it.id == a.id } }
                            saveArrivalAlerts(ctx, arrivalAlerts)
                        }
                    }
                },
                onConnected = {
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        socketConnected = true
                        isConnected = true
                    }
                    // Subscribe to all tracked friends
                    trackedIds.forEach { id ->
                        SocketManager.watchFriend(myTrackId, id)
                    }
                },
                onDisconnected = {
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        socketConnected = false
                    }
                }
            )
        }
    }

    // Watch new friends as they're added, unwatch removed ones
    LaunchedEffect(trackedIds.toList().joinToString(), socketConnected) {
        if (!trackIdReady || !socketConnected) return@LaunchedEffect
        trackedIds.forEach { id -> SocketManager.watchFriend(myTrackId, id) }
    }

    // Cleanup on leave
    DisposableEffect(Unit) {
        onDispose { SocketManager.disconnect() }
    }

    var mapTheme by remember {
        mutableStateOf(
            if (prefs.getString(PREF_MAP_STYLE, MapTheme.DARK.name) == MapTheme.LIGHT.name)
                MapTheme.LIGHT else MapTheme.DARK
        )
    }
    var activeTab by remember { mutableStateOf(NavTab.MAP) }
    var showPrivacyScreen by remember { mutableStateOf(false) }

    // Handle notification tap — unique seq means same-friend taps always re-fire
    var lastHandledSeq by remember { mutableStateOf(-1L) }
    LaunchedEffect(notifEvent, trackIdReady) {
        val ev = notifEvent ?: return@LaunchedEffect
        if (!trackIdReady || ev.seq == lastHandledSeq) return@LaunchedEffect
        lastHandledSeq = ev.seq
        when (ev.type) {
            NOTIF_TYPE_CHAT -> {
                initialChatTrackId = ev.friendId
                initialChatName = ev.friendName?.ifBlank { ev.friendId }
                activeTab = NavTab.CHAT
            }

            NOTIF_TYPE_MAP -> activeTab = NavTab.MAP
        }
    }

    var hasPerm by remember {
        val granted = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        android.util.Log.d("LocationSync", "Initial permission check: $granted")
        mutableStateOf(granted)
    }
    var hasBgPerm by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            else true
        )
    }
    var hasNotifPerm by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            else true
        )
    }
    val permLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            android.util.Log.d("LocationSync", "Permission result: $it")
            hasPerm = it
        }

    // Show GPS disabled dialog when location service is off
    var showGpsDialog by remember { mutableStateOf(false) }

    // Only check ONCE on startup — don't react to gpsAvailable fluctuations
    LaunchedEffect(Unit) {
        val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE)
            as android.location.LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(
            android.location.LocationManager.GPS_PROVIDER
        )
        if (!isGpsEnabled && hasPerm) {
            showGpsDialog = true
        }
    }
    val bgPermLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasBgPerm = granted
            if (granted) {
                ContextCompat.startForegroundService(
                    ctx,
                    Intent(ctx, LocationForegroundService::class.java)
                )
                bgTrackingOn = true
                prefs.edit().putBoolean("bgTrackingOn", true).apply()
            } else {
                Toast.makeText(
                    ctx,
                    "Select 'Allow all the time' for background tracking",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    val notifPermLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasNotifPerm = granted
        }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            scope.launch(Dispatchers.IO) {
                val b64 = uriToBase64(ctx, uri, 256) ?: return@launch
                withContext(Dispatchers.Main) {
                    myAvatarBase64 = b64
                    myAvatarVersion++
                }
                prefs.edit().putString(PREF_PROFILE_PIC, b64).apply()
                val (code, result) = httpPost(
                    "/api/user/${user.uid}/avatar",
                    JSONObject().apply { put("avatarBase64", b64) })
                if (code == -1) {
                    kotlinx.coroutines.delay(2000)
                    httpPost(
                        "/api/user/${user.uid}/avatar",
                        JSONObject().apply { put("avatarBase64", b64) })
                }
            }
        }

    // ── SOS state ──────────────────────────────────────────────────────────────
    var sosActive by remember { mutableStateOf(false) }
    var incomingSosAlert by remember { mutableStateOf<Pair<String, String>?>(null) } // trackId to name
    var sosLastPressedMs by remember { mutableStateOf(prefs.getLong(PREF_SOS_LAST_MS, 0L)) }

    fun sosCooldownRemaining(): Long {
        val elapsed = System.currentTimeMillis() - sosLastPressedMs
        return (SOS_COOLDOWN_MS - elapsed).coerceAtLeast(0L)
    }

    fun triggerAlert(targetIds: List<String>) {
        val remaining = sosCooldownRemaining()
        if (remaining > 0L) {
            val mins = (remaining / 60_000L).toInt()
            Toast.makeText(ctx, "Alert available again in ${mins + 1} min", Toast.LENGTH_LONG)
                .show()
            return
        }
        sosLastPressedMs = System.currentTimeMillis()
        prefs.edit().putLong(PREF_SOS_LAST_MS, sosLastPressedMs).apply()
        sosActive = true
        if (!bgTrackingOn) ContextCompat.startForegroundService(
            ctx,
            Intent(ctx, LocationForegroundService::class.java)
        )
        val loc = rawPoint
        scope.launch(Dispatchers.IO) {
            val arr = JSONArray()
            targetIds.forEach { arr.put(it) }
            httpPost("/api/sos", JSONObject().apply {
                put("trackId", myTrackId)
                put("displayName", user.displayName ?: myTrackId)
                put("lat", loc?.latitude() ?: 0.0)
                put("lng", loc?.longitude() ?: 0.0)
                put("targetTrackIds", arr)
            })
        }
    }

    // ETA overdue checker — runs every 30s, fires notification for expired deadlines
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            val now = System.currentTimeMillis()
            val overdue = arrivalAlerts.filter { it.deadlineMs > 0L && now > it.deadlineMs }
            if (overdue.isNotEmpty()) {
                overdue.forEach { alert ->
                    val etaMin =
                        ((alert.deadlineMs - (now - 30_000L)) / 60_000L).coerceAtLeast(1).toInt()
                    showEtaOverdueNotification(
                        ctx,
                        alert.contactName,
                        etaMin,
                        (alert.id + "_eta").hashCode()
                    )
                    Toast.makeText(ctx, "${alert.contactName} is overdue!", Toast.LENGTH_LONG)
                        .show()
                }
                arrivalAlerts = arrivalAlerts.filter { a -> overdue.none { it.id == a.id } }
                saveArrivalAlerts(ctx, arrivalAlerts)
            }
        }
    }

    LaunchedEffect(Unit) {
        ensureChatNotificationChannel(ctx)
        ensureArrivalNotificationChannel(ctx)
        ensureSosNotificationChannel(ctx)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotifPerm)
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        // savedFriends already loaded synchronously from prefs in remember{} above
    }

    // Service monitor - keeps service alive by calling startForegroundService periodically
    LaunchedEffect(bgTrackingOn) {
        if (!bgTrackingOn) return@LaunchedEffect
        while (bgTrackingOn) {
            delay(15_000L)
            if (bgTrackingOn) {
                // startForegroundService is safe to call on already-running service
                // onStartCommand runs again but service stays alive — no restart
                ContextCompat.startForegroundService(
                    ctx, Intent(ctx, LocationForegroundService::class.java)
                )
            }
        }
    }

    fun persistSavedFriends(list: List<SavedFriend>) {
        val arr = JSONArray(); list.forEach { arr.put(it.toJson()) }
        prefs.edit().putString(PREF_SAVED_FRIENDS, arr.toString()).apply()
        scope.launch(Dispatchers.IO) {
            val backendArr = JSONArray()
            list.forEach { sf ->
                backendArr.put(JSONObject().apply {
                    put("trackId", sf.trackId); put("displayName", sf.displayName)
                    put("email", sf.email); put("avatarBase64", sf.avatarBase64)
                })
            }
            httpPost(
                "/api/user/${user.uid}/saved-friends",
                JSONObject().apply { put("savedFriends", backendArr) })
        }
    }

    fun addSavedFriend(sf: SavedFriend) {
        if (sf.trackId == myTrackId) return  // cannot save yourself
        if (savedFriends.any { it.trackId == sf.trackId }) return
        savedFriends.add(sf)
        persistSavedFriends(savedFriends.toList())
    }

    fun removeSavedFriend(trackId: String) {
        savedFriends.removeAll { it.trackId == trackId }
        persistSavedFriends(savedFriends.toList())
        if (trackId in trackedIds) {
            trackedIds.remove(trackId); friendLocations.remove(trackId)
        }
    }

    fun mergeSavedFriends(incoming: List<SavedFriend>) {
        incoming.forEach { inc ->
            val idx = savedFriends.indexOfFirst { it.trackId == inc.trackId }
            if (idx < 0) {
                savedFriends.add(inc)
            } else {
                val cur = savedFriends[idx]
                val updated = cur.copy(
                    displayName = inc.displayName.ifBlank { cur.displayName },
                    email = inc.email.ifBlank { cur.email },
                    avatarBase64 = inc.avatarBase64.ifBlank { cur.avatarBase64 }
                )
                if (updated != cur) savedFriends[idx] = updated
            }
        }
    }

    LaunchedEffect(user.uid) {
        withContext(Dispatchers.IO) {
            coroutineScope {
                // Fire warm-up ping immediately (responds in milliseconds even on cold start)
                val pingJob = async<JSONObject?> { httpGet("/api/ping") }
                // Fire user fetch in parallel — don't wait for health check
                val userJob = async<JSONObject?> { httpGet("/api/user/${user.uid}", authToken) }

                // Wait for both to complete
                val ping = pingJob.await()
                val userDoc = userJob.await()

                withContext(Dispatchers.Main) {
                    isConnected = ping?.optBoolean("ok") == true
                }

                if (userDoc != null && userDoc.has("trackId")) {
                    val remoteTrackId = userDoc.getString("trackId")
                    android.util.Log.d(
                        "LocationSync",
                        "JWT auth: Retrieved trackId from server: $remoteTrackId"
                    )
                    myTrackId = remoteTrackId; trackIdReady = true
                    prefs.edit().putString(PREF_TRACK_ID, remoteTrackId).putString(PREF_UID, user.uid)
                        .putString(PREF_DISPLAY_NAME, user.displayName ?: "").apply()

                    val remoteAvatar = userDoc.optString("avatarBase64", "")
                    if (remoteAvatar.isNotBlank() && myAvatarBase64.isBlank()) {
                        withContext(Dispatchers.Main) {
                            myAvatarBase64 = remoteAvatar
                            myAvatarVersion++
                        }
                        prefs.edit().putString(PREF_PROFILE_PIC, remoteAvatar).apply()
                    }

                    val remoteFriends = userDoc.optJSONArray("friends")
                        ?.let { arr -> (0 until arr.length()).map { arr.getString(it) } } ?: emptyList()
                    val remoteSaved = userDoc.optJSONArray("savedFriends")
                    if (remoteSaved != null && remoteSaved.length() > 0) {
                        val list = mutableListOf<SavedFriend>()
                        for (i in 0 until remoteSaved.length()) list.add(
                            SavedFriend.fromJson(
                                remoteSaved.getJSONObject(i)
                            )
                        )
                        withContext(Dispatchers.Main) {
                            mergeSavedFriends(list)
                            persistSavedFriends(savedFriends.toList())
                        }
                    }
                    withContext(Dispatchers.Main) {
                        remoteFriends.forEach { fid ->
                            if (fid != myTrackId && fid !in trackedIds) {
                                trackedIds.add(fid); friendLocations[fid] = null
                            }
                        }
                        trackedIds.filter { it !in remoteFriends }
                            .forEach { fid -> trackedIds.remove(fid); friendLocations.remove(fid) }
                        // Remove own trackId if it was accidentally added
                        if (myTrackId in trackedIds) {
                            android.util.Log.d(
                                "LocationSync",
                                "Removing own trackId $myTrackId from trackedIds"
                            )
                            trackedIds.remove(myTrackId)
                            friendLocations.remove(myTrackId)
                        }
                        android.util.Log.d(
                            "LocationSync",
                            "trackedIds: $trackedIds, myTrackId: $myTrackId"
                        )
                    }
                    httpPost("/api/user/upsert", JSONObject().apply {
                        put("uid", user.uid); put("trackId", remoteTrackId)
                        put("displayName", user.displayName ?: ""); put("email", user.email ?: "")
                    }, authToken)
                } else {
                    val localCached = prefs.getString(PREF_TRACK_ID, null)
                    val trackId = localCached ?: (httpPost(
                        "/api/track/generate",
                        JSONObject()
                    ).second?.optString("trackId") ?: "TRK-${
                        UUID.randomUUID().toString().take(8).uppercase()
                    }")
                    android.util.Log.d("LocationSync", "JWT auth: Generated new trackId: $trackId")
                    httpPost("/api/user/upsert", JSONObject().apply {
                        put("uid", user.uid); put("trackId", trackId)
                        put("displayName", user.displayName ?: ""); put("email", user.email ?: "")
                    }, authToken)
                    withContext(Dispatchers.Main) { myTrackId = trackId; trackIdReady = true }
                    prefs.edit().putString(PREF_TRACK_ID, trackId).putString(PREF_UID, user.uid)
                        .putString(PREF_DISPLAY_NAME, user.displayName ?: "").apply()
                }
            }
            // Sync privacy settings from server
            PrivacyStore.syncFromServer(ctx, myTrackId)
        }
    }

    // ── Load cached friend locations on startup ───────────────────────────────────
    LaunchedEffect(trackedIds.toList().joinToString()) {
        // Restore friend last-known locations instantly on app open
        if (trackedIds.isEmpty()) return@LaunchedEffect
        trackedIds.forEach { id ->
            if (friendLocations[id] == null) {
                val cached = LocationCache.loadFriendLocation(ctx, id)
                if (cached != null) friendLocations[id] = cached
            }
        }
    }

    // ❌ Commented out: HTTP polling conflicts with Socket.IO real-time updates
    // Socket.IO now provides 10Hz interpolated location updates
    /*
    LaunchedEffect(trackIdReady, myTrackId) {
        if (!trackIdReady) return@LaunchedEffect
        while (true) {
            withContext(Dispatchers.IO) {
                val userDoc = httpGet("/api/user/${user.uid}", authToken) ?: return@withContext
                val remoteFriends = userDoc.optJSONArray("friends")?.let { arr ->
                    (0 until arr.length()).map { arr.getString(it) }
                } ?: emptyList()
                withContext(Dispatchers.Main) {
                    remoteFriends.forEach { fid ->
                        if (fid != myTrackId && fid !in trackedIds) {  // Don't add own trackId
                            trackedIds.add(fid)
                            friendLocations[fid] = null
                        }
                    }
                    // Remove own trackId if it was accidentally added
                    if (myTrackId in trackedIds) {
                        android.util.Log.d(
                            "LocationSync",
                            "Poll: Removing own trackId $myTrackId from trackedIds"
                        )
                        trackedIds.remove(myTrackId)
                        friendLocations.remove(myTrackId)
                        scope.launch(Dispatchers.IO) {
                            httpPost(
                                "/api/user/${user.uid}/friends",
                                JSONObject().apply {
                                    put(
                                        "friends",
                                        JSONArray().apply { trackedIds.forEach { put(it) } })
                                })
                        }
                    }
                    android.util.Log.d(
                        "LocationSync",
                        "Poll: trackedIds: $trackedIds, myTrackId: $myTrackId"
                    )
                }

                val idsNeedingProfile = trackedIds.toList().filter { id ->
                    val existing = savedFriends.find { it.trackId == id }
                    existing == null || existing.displayName.isBlank()
                }
                idsNeedingProfile.forEach { id ->
                    val j = httpGet("/api/user/by-trackid/$id") ?: return@forEach
                    if (!j.has("trackId")) return@forEach
                    val sf = SavedFriend(
                        trackId = j.getString("trackId"),
                        displayName = j.optString("displayName", "").ifBlank { id },
                        email = j.optString("email", ""),
                        avatarBase64 = j.optString("avatarBase64", "")
                    )
                    withContext(Dispatchers.Main) {
                        mergeSavedFriends(listOf(sf))
                        if (sf.avatarBase64.isNotBlank()) friendAvatars[sf.trackId] =
                            sf.avatarBase64
                        persistSavedFriends(savedFriends.toList())
                    }
                }

                // Refresh friend avatars from server to catch profile pic updates
                trackedIds.toList().forEach { id ->
                    val j = httpGet("/api/user/by-trackid/$id") ?: return@forEach
                    val remoteAvatar = j.optString("avatarBase64", "")
                    if (remoteAvatar.isNotBlank()) {
                        val currentAvatar = friendAvatars[id] ?: ""
                        if (remoteAvatar != currentAvatar) {
                            withContext(Dispatchers.Main) {
                                friendAvatars[id] = remoteAvatar
                                val idx = savedFriends.indexOfFirst { it.trackId == id }
                                if (idx >= 0 && savedFriends[idx].avatarBase64 != remoteAvatar) {
                                    savedFriends[idx] =
                                        savedFriends[idx].copy(avatarBase64 = remoteAvatar)
                                    persistSavedFriends(savedFriends.toList())
                                }
                            }
                        }
                    }
                }

                // Fetch friend locations from server
                trackedIds.toList().forEach { id ->
                    if (id == myTrackId) return@forEach  // Don't fetch own location
                    val locDoc =
                        httpGet("/api/location/$id?requesterId=$myTrackId") ?: return@forEach
                    if (locDoc.has("lat") && locDoc.has("lng")) {
                        val lat = locDoc.getDouble("lat")
                        val lng = locDoc.getDouble("lng")
                        val accuracy = locDoc.optDouble("accuracy", 0.0).toFloat()
                        val speed = locDoc.optDouble("speed", 0.0).toFloat()
                        val heading = locDoc.optDouble("heading", 0.0).toFloat()
                        val timestamp = locDoc.optLong("timestamp", 0L)
                        val isRecent =
                            timestamp > 0L && (System.currentTimeMillis() - timestamp) < IS_RECENT_MS_CLIENT
                        val friendLoc = FriendLocation(
                            point = Point.fromLngLat(lng, lat),
                            accuracy = accuracy,
                            speedKmh = speed * 3.6f,  // Convert m/s to km/h
                            isRecent = isRecent,
                            heading = heading
                        )
                        withContext(Dispatchers.Main) {
                            // Only update if socket hasn't given us a fresher reading
                            val existing = friendLocations[id]
                            if (existing == null || !socketConnected) {
                                friendLocations[id] = friendLoc
                                android.util.Log.d(
                                    "LocationSync",
                                    "Updated location for $id: lat=$lat, lng=$lng"
                                )
                            }
                        }
                    } else {
                        android.util.Log.d("LocationSync", "No location data for $id")
                    }
                }
            }
            delay(15_000L)
        }
    }
    */

    val trackedSnapshot = trackedIds.toList()

    LaunchedEffect(trackedSnapshot) {
        trackedSnapshot.forEach { id ->
            val existingAvatar = savedFriends.find { it.trackId == id }?.avatarBase64 ?: ""
            if (existingAvatar.isNotBlank()) {
                friendAvatars[id] = existingAvatar; return@forEach
            }
            withContext(Dispatchers.IO) {
                val j = httpGet("/api/user/by-trackid/$id") ?: return@withContext
                val b64 = j.optString("avatarBase64", "")
                if (b64.isNotBlank()) withContext(Dispatchers.Main) { friendAvatars[id] = b64 }
            }
        }
    }

    // Own location push loop
    LaunchedEffect(trackIdReady) {
        if (!trackIdReady) return@LaunchedEffect
        while (true) {
            // Always push from foreground regardless of bgTrackingOn.
            // The service is for background-only (app closed). When the app
            // is open, foreground push is the source of truth.
            val pt = rawPoint
            if (pt != null) {
                val pointObj = JSONObject().apply {
                    put("lat", 0.0)
                    put("lng", 0.0)
                    put("accuracy", rawAccuracy.toDouble())
                    put("speed", rawSpeedMs.toDouble())
                    put("heading", rawHeading.toDouble())
                    put(
                        "encryptedCoords", EncryptionUtil.encryptCoords(
                            pt.latitude(), pt.longitude()
                        )
                    )
                    put("timestamp", System.currentTimeMillis())
                }
                synchronized(localPointBuffer) { localPointBuffer.add(pointObj) }

                val nowMs = System.currentTimeMillis()
                val bufferSize = synchronized(localPointBuffer) { localPointBuffer.size }
                val timeSinceSend = nowMs - lastBatchSentMs

                if (bufferSize >= MAX_BUFFER_SIZE || timeSinceSend >= BATCH_SEND_MS) {
                    val toSend = synchronized(localPointBuffer) {
                        val copy = localPointBuffer.toList()
                        localPointBuffer.clear()
                        copy
                    }
                    if (toSend.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            val ok = if (toSend.size == 1) {
                                val body = JSONObject().apply {
                                    put("trackId", myTrackId)
                                    val keys = toSend[0].keys()
                                    while (keys.hasNext()) {
                                        val k = keys.next() as String
                                        put(k, toSend[0].opt(k))
                                    }
                                }
                                httpPost("/api/location/update", body, authToken).second != null
                            } else {
                                val arr = JSONArray().also { a -> toSend.forEach { a.put(it) } }
                                httpPost("/api/location/batch", JSONObject().apply {
                                    put("trackId", myTrackId); put("points", arr)
                                }).second != null
                            }
                            withContext(Dispatchers.Main) {
                                isConnected = ok
                                lastBatchSentMs = System.currentTimeMillis()
                            }
                        }
                    }
                }
            }
            // Adaptive delay based on speed
            val speedKmh = rawSpeedMs * 3.6f
            val adaptiveDelay = when {
                speedKmh > 30f -> 100L   // fast vehicle — very frequent
                speedKmh > 5f  -> 150L   // walking/cycling
                speedKmh > 0.5f -> 250L  // slow movement
                else            -> 500L  // stationary — save battery
            }
            delay(adaptiveDelay)
        }
    }

    val lastNotifiedChatTs = remember { mutableStateMapOf<String, Long>() }
    var chatNotifBootstrapped by remember { mutableStateOf(false) }
    LaunchedEffect(trackIdReady, myTrackId, hasNotifPerm) {
        if (!trackIdReady) return@LaunchedEffect
        while (true) {
            val conversations = withContext(Dispatchers.IO) {
                httpGetArray("/api/chat/conversations/$myTrackId")?.let { parseConversations(it) }
                    ?: emptyList()
            }
            if (!chatNotifBootstrapped) {
                conversations.forEach { c ->
                    lastNotifiedChatTs[c.conversationId] = c.lastTimestamp
                }
                chatNotifBootstrapped = true
            } else {
                conversations.forEach { c ->
                    val previousTs = lastNotifiedChatTs[c.conversationId] ?: 0L
                    if (c.lastTimestamp > previousTs && (c.unread[myTrackId] ?: 0) > 0) {
                        val friendId = c.participants.firstOrNull { it != myTrackId } ?: "Friend"
                        val friendName = c.names[friendId].orEmpty().ifBlank { friendId }
                        showChatNotification(
                            ctx,
                            friendId,
                            friendName,
                            c.lastMessage,
                            c.conversationId.hashCode()
                        )
                    }
                    lastNotifiedChatTs[c.conversationId] = c.lastTimestamp
                }
            }
            delay(2_500L)
        }
    }

    @SuppressLint("MissingPermission")
    fun bootstrapLastLocation() {
        fused.lastLocation.addOnSuccessListener { loc ->
            if (loc != null && rawPoint == null) {
                val pt = Point.fromLngLat(loc.longitude, loc.latitude)
                rawPoint = pt
                rawAccuracy = loc.accuracy
                rawSpeedMs = if (loc.hasSpeed()) loc.speed else 0f
                myLocation = LocationData(pt, loc.accuracy, rawSpeedMs * 3.6f)
                lastGeocodedPoint = pt
                scope.launch {
                    val addr = withContext(Dispatchers.IO) { geocode(ctx, pt.latitude(), pt.longitude()) }
                    myLocation = myLocation?.copy(address = addr)
                }
            }
        }
    }

    var gpsStarted by remember { mutableStateOf(false) }

    LaunchedEffect(hasPerm, trackIdReady) {
        if (!hasPerm || !trackIdReady || gpsStarted) return@LaunchedEffect
        gpsStarted = true
        // Small delay lets the composable fully settle after permission grant
        delay(300L)
        bootstrapLastLocation()
        startGps(
            fused = fused, onCb = onCb,
            onAvailability = { available ->
                gpsAvailable = available
            },
            context = ctx,
            accelLat = 0.0,
            accelLng = 0.0,
                onLoc = { pt, acc, spdMs, heading, alt, altAcc, speedAcc, headingAcc, provider ->
                    rawPoint = pt
                    rawAccuracy = acc
                    rawSpeedMs = spdMs
                    rawHeading = heading

                    myLocation = myLocation?.copy(
                        point = pt,
                        accuracy = acc,
                        speedKmh = spdMs * 3.6f
                    ) ?: LocationData(pt, acc, spdMs * 3.6f)

                    LocationCache.saveMyLocation(ctx, pt, acc, spdMs * 3.6f)

                    val shouldGeocode = lastGeocodedPoint == null || distM(lastGeocodedPoint!!, pt) > 50.0
                    if (shouldGeocode) {
                        lastGeocodedPoint = pt
                        scope.launch {
                            val addr = withContext(Dispatchers.IO) { geocode(ctx, pt.latitude(), pt.longitude()) }
                            myLocation = myLocation?.copy(address = addr)
                        }
                    }

                    if (isRecording && sessionId != null && acc < ACC_ACCEPTABLE) {
                        val now = System.currentTimeMillis()
                        val moved = lastRecPoint == null || distM(lastRecPoint!!, pt) > MIN_DIST
                        val forced = (now - lastRecTimeMs) > FORCE_MS
                        if (moved || forced) {
                            lastRecTimeMs = now; lastRecPoint = pt; pathPoints = pathPoints + pt
                            val sid = sessionId!!
                            scope.launch(Dispatchers.IO) {
                                httpPost("/api/session/$sid/point", JSONObject().apply {
                                    put("encryptedCoords", EncryptionUtil.encryptCoords(pt.latitude(), pt.longitude()))
                                })
                            }
                        }
                    }
                }
            )
        }

    // SOS incoming alert poll — check every 10s for SOS from contacts
    LaunchedEffect(trackIdReady, myTrackId) {
        if (!trackIdReady) return@LaunchedEffect
        val seenIds = mutableSetOf<String>()
        while (true) {
            delay(10_000L)
            withContext(Dispatchers.IO) {
                val arr = httpGetArray("/api/sos/pending/$myTrackId") ?: return@withContext
                withContext(Dispatchers.Main) {
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val tid = o.optString("trackId", "")
                        val name = o.optString("displayName", tid)
                        val ts = o.optString("timestamp", "")
                        val seenKey = "$tid:$ts"
                        if (tid.isNotBlank() && seenKey !in seenIds) {
                            seenIds.add(seenKey)
                            incomingSosAlert = tid to name
                            showSosNotification(ctx, name, tid.hashCode())
                        }
                    }
                }
            }
        }
    }

    // ── Server-first mode: DISABLED - using local GPS with StationaryGate instead ─────
    /*
    LaunchedEffect(trackIdReady, myTrackId) {
        if (!trackIdReady) return@LaunchedEffect
        var lastServerPoint: Point? = null
        var lastServerTime = 0L
        while (true) {
            delay(200L)  // Fetch every 200ms (5 times per second)
            withContext(Dispatchers.IO) {
                try {
                    val loc = httpGet("/api/location/$myTrackId")
                    if (loc != null && loc.has("lat") && loc.has("lng")) {
                        val serverLat = loc.getDouble("lat")
                        val serverLng = loc.getDouble("lng")
                        val serverSpeed = loc.optDouble("speed", 0.0)
                        val serverAccuracy = loc.optDouble("accuracy", 0.0)
                        val serverTimestamp = loc.optLong("timestamp", System.currentTimeMillis())

                        // Check if server data is recent (within 10 seconds)
                        val ageMs = System.currentTimeMillis() - serverTimestamp
                        if (ageMs < 10_000L) {
                            val serverPoint = Point.fromLngLat(serverLng, serverLat)
                            withContext(Dispatchers.Main) {
                                // Apply interpolation filtering between server points
                                val cur = displayMyLocation.value
                                val dtSec = if (lastServerTime > 0) {
                                    (System.currentTimeMillis() - lastServerTime) / 1000.0
                                } else 1.0
                                lastServerTime = System.currentTimeMillis()

                                val lastPt = lastServerPoint
                                if (cur != null && lastPt != null) {
                                    val distM = distM(lastPt, serverPoint)
                                    // Exponential smoothing interpolation
                                    val tau = if (serverSpeed > 10.0) 0.08 else 0.12
                                    val alpha = (1f - kotlin.math.exp(-dtSec / tau)).toFloat()
                                    val newLat = cur.point.latitude() + alpha * (serverPoint.latitude() - cur.point.latitude())
                                    val newLng = cur.point.longitude() + alpha * (serverPoint.longitude() - cur.point.longitude())
                                    val newAcc = cur.accuracy + alpha * (serverAccuracy.toFloat() - cur.accuracy)
                                    val newSpeed = cur.speedKmh + alpha * (serverSpeed * 3.6f - cur.speedKmh)

                                    displayMyLocation.value = cur.copy(
                                        point = Point.fromLngLat(newLng, newLat),
                                        accuracy = newAcc,
                                        speedKmh = newSpeed.toFloat()
                                    )
                                } else {
                                    // First fetch or no previous data - use server data directly
                                    displayMyLocation.value = LocationData(
                                        serverPoint,
                                        serverAccuracy.toFloat(),
                                        serverSpeed.toFloat() * 3.6f,
                                        "Last known location"
                                    )
                                }

                                // Update rawPoint for consistency
                                rawPoint = serverPoint
                                rawSpeedMs = serverSpeed.toFloat()
                                rawAccuracy = serverAccuracy.toFloat()
                                lastServerPoint = serverPoint
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Server fetch failed, continue with last known location
                }
            }
        }
    }
    */

    var friendPaths by remember { mutableStateOf<Map<String, List<Point>>>(emptyMap()) }

    LaunchedEffect(trackedSnapshot, showFriendDialog) {
        if (trackedSnapshot.isEmpty() || showFriendDialog) return@LaunchedEffect
        while (true) {
            // When socket is live, we only need occasional HTTP checks for consistency
            // When socket is down, poll aggressively at 1s as fallback
            val pollInterval = if (socketConnected) 10_000L else 1_000L
            delay(pollInterval)

            if (trackedSnapshot.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    val ids = trackedSnapshot.joinToString(",")
                    val arr = httpGetBulkArray("/api/locations/bulk?ids=$ids&requesterId=$myTrackId")
                    if (arr != null) {
                        for (i in 0 until arr.length()) {
                            val j = arr.getJSONObject(i)
                            val id = j.optString("trackId") ?: continue
                            if (id == myTrackId) continue  // never process self as a friend location
                            if (BlockStore.isBlocked(ctx, id)) continue
                            if (j.optBoolean("blocked") || j.optBoolean("privacy")) continue

                            val ec = j.optString("encryptedCoords", "")
                            val coords = if (ec.isNotBlank()) EncryptionUtil.decryptCoords(ec) else null
                            val lat = coords?.first ?: j.optDouble("lat", Double.NaN)
                            val lng = coords?.second ?: j.optDouble("lng", Double.NaN)
                            if (lat.isNaN() || lng.isNaN()) continue

                            val newLoc = FriendLocation(
                                point = Point.fromLngLat(lng, lat),
                                accuracy = j.optDouble("accuracy", 0.0).toFloat(),
                                speedKmh = (j.optDouble("speed", 0.0) * 3.6).toFloat(),
                                isRecent = j.optBoolean("isRecent", false),
                                heading = j.optDouble("heading", 0.0).toFloat(),
                                altitude = j.optDouble("altitude", 0.0),
                                extrapolated = j.optBoolean("extrapolated", false),
                                snapped = j.optBoolean("snapped", false),
                                roadName = j.optString("roadName", "")
                            )
                            withContext(Dispatchers.Main) {
                                // Only update if socket hasn't given us a fresher reading
                                val existing = friendLocations[id]
                                val newTs = j.optLong("timestamp", 0L)
                                // Accept HTTP result only if it's newer or socket has no data
                                if (existing == null || !socketConnected) {
                                    friendLocations[id] = newLoc
                                    LocationCache.saveFriendLocation(ctx, id, newLoc)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Fetch path history for friends with active arrival alerts (for map highlighting)
    LaunchedEffect(arrivalAlerts, trackedSnapshot) {
        while (true) {
            val alertIds = arrivalAlerts.map { it.contactId }.distinct()
            if (alertIds.isNotEmpty()) {
                val newPaths = mutableMapOf<String, List<Point>>()
                withContext(Dispatchers.IO) {
                    alertIds.forEach { id ->
                        try {
                            val j = httpGet("/api/path/$id?hours=2") ?: return@forEach
                            val pts = j.optJSONArray("points") ?: return@forEach
                            val pointList = (0 until pts.length()).mapNotNull { i ->
                                val p = pts.getJSONObject(i)
                                val lat = p.optDouble("lat", Double.NaN)
                                val lng = p.optDouble("lng", Double.NaN)
                                if (!lat.isNaN() && !lng.isNaN()) Point.fromLngLat(
                                    lng,
                                    lat
                                ) else null
                            }
                            if (pointList.isNotEmpty()) newPaths[id] = pointList
                        } catch (_: Exception) {
                        }
                    }
                }
                friendPaths = newPaths
            }
            delay(15_000L)
        }
    }

    LaunchedEffect(trackedSnapshot, user.uid) {
        val idsNeedingProfile = trackedSnapshot.filter { id ->
            val existing = savedFriends.find { it.trackId == id }
            existing == null || existing.displayName.isBlank() || existing.email.isBlank()
        }
        if (idsNeedingProfile.isEmpty()) return@LaunchedEffect
        val fetched = withContext(Dispatchers.IO) {
            idsNeedingProfile.mapNotNull { id ->
                val j = httpGet("/api/user/by-trackid/$id") ?: return@mapNotNull null
                if (!j.has("trackId")) return@mapNotNull null
                SavedFriend(
                    trackId = j.getString("trackId"),
                    displayName = j.optString("displayName", "").ifBlank { j.getString("trackId") },
                    email = j.optString("email", ""),
                    avatarBase64 = j.optString("avatarBase64", "")
                )
            }
        }
        if (fetched.isEmpty()) return@LaunchedEffect
        mergeSavedFriends(fetched)
        fetched.forEach { incoming ->
            if (incoming.avatarBase64.isNotBlank()) friendAvatars[incoming.trackId] =
                incoming.avatarBase64
        }
        persistSavedFriends(savedFriends.toList())
    }

    fun persistFriends(newList: List<String>) {
        scope.launch(Dispatchers.IO) {
            httpPost(
                "/api/user/${user.uid}/friends",
                JSONObject().apply {
                    put(
                        "friends",
                        JSONArray().apply { newList.forEach { put(it) } })
                })
        }
    }

    fun addFriend(id: String, profile: SavedFriend? = null) {
        // Self-tracking: instead of blocking, just ensure self location is visible
        // Self pin is always shown via selfAnnotation — no need to add to trackedIds
        if (id == myTrackId) {
            Toast.makeText(ctx, "Your location is always shown on the map", Toast.LENGTH_SHORT).show()
            return
        }
        if (trackedIds.size >= MAX_FRIENDS || id in trackedIds) return
        trackedIds.add(id); friendLocations[id] = null; persistFriends(trackedIds.toList())
        if (profile != null) addSavedFriend(profile)
    }

    fun fetchFriendProfile(trackId: String, fallback: SavedFriend? = null): SavedFriend? {
        val normalized = trackId.trim().uppercase()
        val j = httpGet("/api/user/by-trackid/$normalized")
        if (j != null && j.has("trackId")) {
            return SavedFriend(
                trackId = j.getString("trackId"),
                displayName = j.optString("displayName", "").ifBlank { j.getString("trackId") },
                email = j.optString("email", ""),
                avatarBase64 = j.optString("avatarBase64", "")
            )
        }
        return fallback?.copy(
            trackId = normalized,
            displayName = fallback.displayName.ifBlank { normalized })
    }

    fun removeFriend(id: String) {
        trackedIds.remove(id); friendLocations.remove(id); persistFriends(trackedIds.toList())
    }

    fun toggleTrackSavedFriend(trackId: String) {
        if (trackId == myTrackId) {
            Toast.makeText(ctx, "Your location is always visible on the map", Toast.LENGTH_SHORT).show()
            return
        }
        if (BlockStore.isBlocked(ctx, trackId)) {
            Toast.makeText(ctx, "Cannot track blocked user", Toast.LENGTH_SHORT).show()
            return
        }
        if (trackId in trackedIds) {
            removeFriend(trackId)
        } else {
            if (trackedIds.size >= MAX_FRIENDS) {
                Toast.makeText(ctx, "Maximum $MAX_FRIENDS contacts reached", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            val profile = savedFriends.find { it.trackId == trackId }
            addFriend(trackId, profile)
        }
    }

    val onStartRecording: () -> Unit = {
        val sid = UUID.randomUUID().toString()
        sessionId = sid; pathPoints = emptyList(); lastRecPoint = null; lastRecTimeMs = 0L
        isRecording = true
        val startTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        scope.launch(Dispatchers.IO) {
            httpPost(
                "/api/session/start",
                JSONObject().apply {
                    put("sessionId", sid); put("uid", user.uid); put(
                    "trackId",
                    myTrackId
                ); put("startTime", startTime)
                })
        }
    }

    val onStopRecording: () -> Unit = {
        isRecording = false
        val sid = sessionId
        val endTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        if (sid != null) scope.launch(Dispatchers.IO) {
            httpPatch(
                "/api/session/$sid/end",
                JSONObject().apply { put("endTime", endTime) })
        }
        sessionId = null; lastRecPoint = null
    }

    fun startBgTracking() {
        android.util.Log.d(
            "LocationSync",
            "startBgTracking called, hasPerm=$hasPerm, hasBgPerm=$hasBgPerm"
        )
        if (!hasPerm) {
            Toast.makeText(ctx, "Grant location permission first", Toast.LENGTH_SHORT)
                .show(); return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasBgPerm) bgPermLauncher.launch(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        else {
            android.util.Log.d("LocationSync", "Starting foreground service")
            ContextCompat.startForegroundService(
                ctx,
                Intent(ctx, LocationForegroundService::class.java)
            )
            bgTrackingOn = true
            prefs.edit().putBoolean("bgTrackingOn", true).apply()
            android.util.Log.d(
                "LocationSync",
                "Foreground service started, bgTrackingOn=$bgTrackingOn"
            )
            Toast.makeText(
                ctx,
                "For best results, disable battery optimization for this app in settings",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun stopBgTracking() {
        ctx.stopService(Intent(ctx, LocationForegroundService::class.java))
        bgTrackingOn = false
        prefs.edit().putBoolean("bgTrackingOn", false).apply()
    }

    if (!hasPerm) {
        android.util.Log.d("LocationSync", "Showing PermScreen - hasPerm: $hasPerm")
        PermScreen { permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }; return
    }

    if (showGpsDialog) {
        Dialog(onDismissRequest = { showGpsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(0.4f))
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📍", fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Location is Off",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = TextOnDark
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Turn on device location to see your position and share with friends.",
                        fontSize = 14.sp,
                        color = TextOnDarkMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen)))
                            .clickable {
                                showGpsDialog = false
                                // Open location settings directly
                                ctx.startActivity(
                                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Open Location Settings",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    TextButton(onClick = { showGpsDialog = false }) {
                        Text("Continue without GPS", color = TextOnDarkMuted, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Main tabs are ALWAYS in composition — overlays slide on top so back never causes a remount/blink
    MainTabContent(
        activeTab = activeTab,
        onTabSelected = { activeTab = it },
        isChatConvOpen = isChatConvOpen,
        displayMyLocation = displayMyLocation.value,
        myTrackId = myTrackId,
        trackIdReady = trackIdReady,
        isConnected = isConnected,
        gpsAvailable = gpsAvailable,
        pathPoints = pathPoints,
        isRecording = isRecording,
        trackedIds = trackedIds,
        displayPositions = displayPositions,
        myAvatarBase64 = myAvatarBase64,
        myAvatarVersion = myAvatarVersion,
        friendAvatars = friendAvatars,
        savedFriends = savedFriends,
        arrivalAlerts = arrivalAlerts,
        friendPaths = friendPaths,
        mapAlertPlacementMode = mapAlertPlacementMode,
        onLongPressMap = { point ->
            if (trackedIds.isNotEmpty()) {
                pendingAlertPoint = point
                mapAlertPlacementMode = false
            }
        },
        onStartRecording = onStartRecording,
        onStopRecording = onStopRecording,
        bgTrackingOn = bgTrackingOn,
        onStartBgTracking = { startBgTracking() },
        onStopBgTracking = { stopBgTracking() },
        mapTheme = mapTheme,
        onMapThemeChange = { theme ->
            mapTheme = theme
            prefs.edit().putString(PREF_MAP_STYLE, theme.name).apply()
        },
        rawHeading = rawHeading,
        onShowFriendDialog = { showFriendDialog = true },
        onToggleTrack = { toggleTrackSavedFriend(it) },
        onRemoveSavedFriend = { removeSavedFriend(it) },
        onViewProfile = { sf -> viewingProfile = sf },
        onOpenChatWith = { sf ->
            if (BlockStore.isBlocked(ctx, sf.trackId)) {
                Toast.makeText(ctx, "Cannot chat with blocked user", Toast.LENGTH_SHORT).show()
                return@MainTabContent
            }
            initialChatTrackId = sf.trackId
            initialChatName = sf.displayName.ifBlank { sf.trackId }
            activeTab = NavTab.CHAT
        },
        user = user,
        myLocation = myLocation,
        onPickProfilePicture = { imagePickerLauncher.launch("image/*") },
        onSignOut = onSignOut,
        onGetNotified = { mapAlertPlacementMode = true; activeTab = NavTab.MAP },
        onCancelAlert = { alertId ->
            val updated = arrivalAlerts.filter { it.id != alertId }
            arrivalAlerts = updated
            saveArrivalAlerts(ctx, updated)
            Toast.makeText(ctx, "Alert cancelled", Toast.LENGTH_SHORT).show()
        },
        sosActive = sosActive,
        onSendAlert = { targetIds -> triggerAlert(targetIds) },
        onShowPrivacy = { showPrivacyScreen = true },
        initialChatTrackId = initialChatTrackId,
        initialChatName = initialChatName,
        onChatBack = {
            activeTab = NavTab.MAP
            initialChatTrackId = null
            initialChatName = null
        },
        onConversationVisible = { isChatConvOpen = it },
        incomingSosAlert = incomingSosAlert,
        onDismissSos = { incomingSosAlert = null },
        viewingProfile = viewingProfile,
        onProfileBack = { viewingProfile = null },
        onAddFriend = { sf -> addSavedFriend(sf) },
        onProfileOpenChat = { sf ->
            if (BlockStore.isBlocked(ctx, sf.trackId)) {
                Toast.makeText(ctx, "Cannot chat with blocked user", Toast.LENGTH_SHORT).show()
                return@MainTabContent
            }
            initialChatTrackId = sf.trackId
            initialChatName = sf.displayName.ifBlank { sf.trackId }
            viewingProfile = null
            activeTab = NavTab.CHAT
        },
        showPrivacyScreen = showPrivacyScreen,
        onPrivacyBack = { showPrivacyScreen = false },
        friendLocations = friendLocations
    )

    // Arrival alert dialog — shown when user long-presses a point on the map
    val alertPoint = pendingAlertPoint
    if (alertPoint != null) {
        ArrivalAlertSetDialog(
            point = alertPoint,
            trackedIds = trackedIds.toList(),
            savedFriends = savedFriends,
            existingAlerts = arrivalAlerts,
            onConfirm = { alerts ->
                val updated = arrivalAlerts + alerts
                arrivalAlerts = updated
                saveArrivalAlerts(ctx, updated)
                pendingAlertPoint = null
                val names = alerts.joinToString(", ") { it.contactName }
                Toast.makeText(ctx, "Alert set for $names", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { pendingAlertPoint = null }
        )
    }

    if (showFriendDialog) {
        FriendDialog(
            currentIds = trackedIds.toList(),
            savedFriends = savedFriends,
            myUid = user.uid,
            myTrackId = myTrackId,
            onDismiss = { showFriendDialog = false },
            onTrackNow = { sf ->
                scope.launch(Dispatchers.IO) {
                    val resolved = fetchFriendProfile(sf.trackId, sf)
                    withContext(Dispatchers.Main) {
                        if (resolved == null) {
                            Toast.makeText(
                                ctx,
                                "Could not load this contact profile",
                                Toast.LENGTH_SHORT
                            ).show(); return@withContext
                        }
                        if (trackedIds.size >= MAX_FRIENDS) {
                            Toast.makeText(
                                ctx,
                                "Maximum $MAX_FRIENDS contacts reached",
                                Toast.LENGTH_SHORT
                            ).show(); return@withContext
                        }
                        addFriend(resolved.trackId.trim().uppercase(), resolved); showFriendDialog =
                        false
                    }
                }
            },
            onAddAndTrack = { sf ->
                scope.launch(Dispatchers.IO) {
                    val resolved = fetchFriendProfile(sf.trackId, sf) ?: sf
                    withContext(Dispatchers.Main) {
                        addSavedFriend(resolved); addFriend(
                        resolved.trackId,
                        resolved
                    ); showFriendDialog = false
                    }
                }
            },
            onViewProfile = { sf -> showFriendDialog = false; viewingProfile = sf }
        )
    }
}

// ArrivalAlertSetDialog moved to ArrivalAlerts.kt
@Composable
fun MainTabContent(
    activeTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    isChatConvOpen: Boolean,
    displayMyLocation: LocationData?,
    myTrackId: String,
    trackIdReady: Boolean,
    isConnected: Boolean,
    gpsAvailable: Boolean,
    pathPoints: List<Point>,
    isRecording: Boolean,
    trackedIds: List<String>,
    displayPositions: Map<String, FriendLocation>,
    myAvatarBase64: String,
    myAvatarVersion: Int,
    friendAvatars: Map<String, String>,
    savedFriends: List<SavedFriend>,
    arrivalAlerts: List<ArrivalAlert>,
    friendPaths: Map<String, List<Point>>,
    mapAlertPlacementMode: Boolean,
    onLongPressMap: (Point) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    bgTrackingOn: Boolean,
    onStartBgTracking: () -> Unit,
    onStopBgTracking: () -> Unit,
    mapTheme: MapTheme,
    onMapThemeChange: (MapTheme) -> Unit,
    rawHeading: Float,
    onShowFriendDialog: () -> Unit,
    onToggleTrack: (String) -> Unit,
    onRemoveSavedFriend: (String) -> Unit,
    onViewProfile: (SavedFriend) -> Unit,
    onOpenChatWith: (SavedFriend) -> Unit,
    user: AppUser,
    myLocation: LocationData?,
    onPickProfilePicture: () -> Unit,
    onSignOut: () -> Unit,
    onGetNotified: () -> Unit,
    onCancelAlert: (String) -> Unit,
    sosActive: Boolean,
    onSendAlert: (List<String>) -> Unit,
    onShowPrivacy: () -> Unit,
    initialChatTrackId: String?,
    initialChatName: String?,
    onChatBack: () -> Unit,
    onConversationVisible: (Boolean) -> Unit,
    incomingSosAlert: Pair<String, String>?,
    onDismissSos: () -> Unit,
    viewingProfile: SavedFriend?,
    onProfileBack: () -> Unit,
    onAddFriend: (SavedFriend) -> Unit,
    onProfileOpenChat: (SavedFriend) -> Unit,
    showPrivacyScreen: Boolean,
    onPrivacyBack: () -> Unit,
    friendLocations: Map<String, FriendLocation?>
) {
    Box(Modifier
        .fillMaxSize()
        .background(DarkBg)) {

        val hideBottomNav = isChatConvOpen && activeTab == NavTab.CHAT
        // Padded content area for non-chat tabs (Map, Contacts, Profile)
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
                .navigationBarsPadding()
        ) {

            // ── MAP — always full size, hidden via alpha to keep GL surface alive ──
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = if (activeTab == NavTab.MAP) 1f else 0f }
                    .then(
                        if (activeTab != NavTab.MAP) Modifier.pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial)
                                        .changes.forEach { it.consume() }
                                }
                            }
                        } else Modifier
                    )
            ) {
                MapScreen(
                    myLocation = displayMyLocation,
                    myTrackId = myTrackId,
                    trackIdReady = trackIdReady,
                    isConnected = isConnected,
                    gpsAvailable = gpsAvailable,
                    pathPoints = pathPoints,
                    isRecording = isRecording,
                    trackedIds = trackedIds,
                    friendLocations = displayPositions,
                    myAvatarBase64 = myAvatarBase64,
                    myAvatarVersion = myAvatarVersion,
                    friendAvatars = friendAvatars,
                    savedFriends = savedFriends,
                    arrivalAlerts = arrivalAlerts,
                    friendPaths = friendPaths,
                    placementMode = mapAlertPlacementMode,
                    onLongPressMap = onLongPressMap,
                    onStartRecording = onStartRecording,
                    onStopRecording = onStopRecording,
                    bgTrackingOn = bgTrackingOn,
                    onStartBgTracking = onStartBgTracking,
                    onStopBgTracking = onStopBgTracking,
                    mapTheme = mapTheme,
                    onMapThemeChange = onMapThemeChange,
                    rawHeading = rawHeading
                )
            }

            // ── CONTACTS ─────────────────────────────────────────────────────
            Box(
                modifier = if (activeTab == NavTab.CONTACTS) Modifier.fillMaxSize()
                else Modifier.size(0.dp)
            ) {
                FriendsTab(
                    savedFriends = savedFriends,
                    trackedIds = trackedIds,
                    friendLocations = friendLocations,
                    friendAvatars = friendAvatars,
                    onShowFriendDialog = onShowFriendDialog,
                    onToggleTrack = onToggleTrack,
                    onRemoveSavedFriend = onRemoveSavedFriend,
                    onViewProfile = onViewProfile,
                    onOpenChatWith = onOpenChatWith
                )
            }

            // ── PROFILE ──────────────────────────────────────────────────────
            Box(
                modifier = if (activeTab == NavTab.PROFILE) Modifier.fillMaxSize()
                else Modifier.size(0.dp)
            ) {
                ProfileTab(
                    user = user,
                    myTrackId = myTrackId,
                    trackIdReady = trackIdReady,
                    myLocation = myLocation,
                    isConnected = isConnected,
                    gpsAvailable = gpsAvailable,
                    bgTrackingOn = bgTrackingOn,
                    myAvatarBase64 = myAvatarBase64,
                    myAvatarVersion = myAvatarVersion,
                    onPickProfilePicture = onPickProfilePicture,
                    onStartBgTracking = onStartBgTracking,
                    onStopBgTracking = onStopBgTracking,
                    onStartRecording = onStartRecording,
                    onStopRecording = onStopRecording,
                    isRecording = isRecording,
                    pathPoints = pathPoints,
                    onSignOut = onSignOut,
                    onGetNotified = onGetNotified,
                    activeAlertCount = arrivalAlerts.size,
                    arrivalAlerts = arrivalAlerts,
                    onCancelAlert = onCancelAlert,
                    savedFriends = savedFriends,
                    trackedIds = trackedIds,
                    sosActive = sosActive,
                    onSendAlert = onSendAlert,
                    onShowPrivacy = onShowPrivacy
                )
            }
        }

        // ── CHAT tab ────────────────────────────────────────────────────────
        Box(
            modifier = if (activeTab == NavTab.CHAT) Modifier.fillMaxSize()
            else Modifier.size(0.dp)
        ) {
            ChatEntryPoint(
                myTrackId = myTrackId,
                myName = user.displayName ?: myTrackId,
                myAvatarBase64 = myAvatarBase64,
                friendAvatars = friendAvatars,
                savedFriends = savedFriends,
                trackedIds = trackedIds,
                friendLocations = friendLocations,
                initialChatTrackId = initialChatTrackId,
                initialChatName = initialChatName,
                onBack = onChatBack,
                onConversationVisible = onConversationVisible
            )
        }

        // ── Bottom nav ──────────────────────────────────────────────────────
        if (!hideBottomNav) {
            DarkBottomNav(
                activeTab = activeTab,
                onTabSelected = onTabSelected,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }

        // ── Incoming SOS banner ─────────────────────────────────────────────
        val incoming = incomingSosAlert
        if (incoming != null) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(RedRecord)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚨", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "🚨 Alert — ${incoming.second} needs help!",
                            fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.White
                        )
                        Text(
                            "Emergency alert — check their location",
                            fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onDismissSos() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text(
                            "Dismiss", color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // ── Profile screen overlay ──────────────────────────────────────────
        AnimatedVisibility(
            visible = viewingProfile != null,
            enter = slideInVertically(tween(240)) { it / 10 } + fadeIn(tween(220)),
            exit = slideOutVertically(tween(200)) { it / 10 } + fadeOut(tween(200))
        ) {
            viewingProfile?.let { vp ->
                UserProfileScreen(
                    profile = vp, isTracking = vp.trackId in trackedIds,
                    liveLocation = friendLocations[vp.trackId],
                    isAlreadySaved = savedFriends.any { it.trackId == vp.trackId },
                    canTrack = vp.trackId in trackedIds || trackedIds.count() < MAX_FRIENDS,
                    onBack = onProfileBack,
                    onToggleTrack = onToggleTrack,
                    onAddFriend = onAddFriend,
                    onRemoveFriend = onRemoveSavedFriend,
                    onOpenChat = onProfileOpenChat
                )
            }
        }

        // ── Privacy settings screen overlay ─────────────────────────────────
        AnimatedVisibility(
            visible = showPrivacyScreen,
            enter = slideInVertically(tween(240)) { it / 10 } + fadeIn(tween(220)),
            exit = slideOutVertically(tween(200)) { it / 10 } + fadeOut(tween(200))
        ) {
            PrivacySettingsScreen(
                myTrackId = myTrackId,
                savedFriends = savedFriends,
                onBack = onPrivacyBack
            )
        }
    }
}

// ─── Dark Bottom Navigation Bar ─────────────────────────────────────────────
@Composable
fun DarkBottomNav(
    activeTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    data class NavEntry(val tab: NavTab, val label: String, val icon: ImageVector)

    val items = listOf(
        NavEntry(NavTab.MAP, "Map", Icons.Default.Map),
        NavEntry(NavTab.CONTACTS, "Contacts", Icons.Default.Group),
        NavEntry(NavTab.CHAT, "Chat", Icons.Default.Chat),
        NavEntry(NavTab.PROFILE, "Profile", Icons.Default.Person)
    )
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkSurface,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = Color(0xFF1E1F22),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { entry ->
                    NavItem(
                        icon = entry.icon,
                        label = entry.label,
                        isSelected = activeTab == entry.tab,
                        onClick = { onTabSelected(entry.tab) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    // FIX 7: no animation on these — they run on UI thread and compete with scroll
    // Direct values are instant and cost nothing
    val iconColor = if (isSelected) Color.White else Color(0xFF80848E)
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isSelected) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldGreen.copy(alpha = 0.2f))
                )
            }
            Icon(
                icon, contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(3.dp))
        Text(
            label, fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = iconColor
        )
    }
}

// ─── Avatar composable ────────────────────────────────────────────────────────
@Composable
fun AvatarCircle(
    base64: String,
    fallbackInitial: String,
    size: Int = 56,
    isTracking: Boolean = false,
    isLive: Boolean = false,
    avatarVersion: Int = 0,
    // NEW: option to use square (rounded) instead of circle
    squareShape: Boolean = false
) {
    val bitmap = remember(base64, avatarVersion) { base64ToBitmap(base64) }
    val shape = if (squareShape) RoundedCornerShape(12.dp) else CircleShape
    Box(
        Modifier
            .size(size.dp)
            .clip(shape)
            .background(
                if (isTracking) Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen))
                else Brush.linearGradient(listOf(DarkCardAlt, DarkBorderLight))
            )
            .border(2.dp, if (isLive) GreenOnline.copy(alpha = 0.6f) else Color.Transparent, shape),
        Alignment.Center
    ) {
        if (bitmap != null) {
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
            )
        } else {
            Text(
                fallbackInitial, fontSize = (size * 0.38f).sp, fontWeight = FontWeight.ExtraBold,
                color = if (isTracking) DarkBg else TextOnDark
            )
        }
    }
}

// ─── Map Screen ───────────────────────────────────────────────────────────────
@Composable
fun MapScreen(
    myLocation: LocationData?, myTrackId: String, trackIdReady: Boolean,
    isConnected: Boolean, gpsAvailable: Boolean,
    pathPoints: List<Point>, isRecording: Boolean,
    trackedIds: List<String>,
    friendLocations: Map<String, FriendLocation?>,
    myAvatarBase64: String = "",
    myAvatarVersion: Int = 0,
    friendAvatars: Map<String, String> = emptyMap(),
    savedFriends: List<SavedFriend> = emptyList(),
    arrivalAlerts: List<ArrivalAlert> = emptyList(),
    friendPaths: Map<String, List<Point>> = emptyMap(),
    onLongPressMap: ((Point) -> Unit)? = null,
    placementMode: Boolean = false,
    bgTrackingOn: Boolean,
    onStartRecording: () -> Unit, onStopRecording: () -> Unit,
    onStartBgTracking: () -> Unit, onStopBgTracking: () -> Unit,
    mapTheme: MapTheme = MapTheme.DARK,
    onMapThemeChange: (MapTheme) -> Unit = {},
    rawHeading: Float = 0f
) {
    val ctx = LocalContext.current

    data class MapRefs(
        val map: MapboxMap,
        val pointMgr: PointAnnotationManager,
        val polylineMgr: PolylineAnnotationManager,
        val polygonMgr: PolygonAnnotationManager,
        var myHeading: Float = 0f
    )

    val mapRefsHolder = remember { mutableStateOf<MapRefs?>(null) }
    LaunchedEffect(rawHeading) {
        mapRefsHolder.value?.myHeading = rawHeading
    }
    val myCentred = remember { mutableStateOf(false) }
    var previousTrackedIds by remember { mutableStateOf(trackedIds) }
    var pendingFriendFocusId by remember { mutableStateOf<String?>(null) }

    val overlayBg =
        if (mapTheme == MapTheme.DARK) DarkCard.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.92f)
    val overlayBorder = if (mapTheme == MapTheme.DARK) DarkBorderLight else Color(0xFFDDDDDD)
    val overlayText = if (mapTheme == MapTheme.DARK) TextOnDark else Color(0xFF1A1A1A)
    val overlayMuted = if (mapTheme == MapTheme.DARK) TextOnDarkMuted else Color(0xFF777777)

    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Triple<String, Double, Double>>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var routeDestination by remember { mutableStateOf<Triple<String, Double, Double>?>(null) }
    var routePoints by remember { mutableStateOf<List<Point>>(emptyList()) }
    var routeInfo by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val pinModeActive = remember { mutableStateOf(false) }
    val placedPin = remember { mutableStateOf<Point?>(null) }

    // Load cached location synchronously before map creates
    val initialCenter = remember {
        LocationCache.loadMyLocation(ctx)?.point
    }

    LaunchedEffect(myLocation?.point?.latitude(), mapRefsHolder.value) {
        val d = myLocation ?: return@LaunchedEffect
        val map = mapRefsHolder.value?.map ?: return@LaunchedEffect
        if (!myCentred.value) {
            // Use setCamera (instant) instead of flyTo (animated) for cached location
            // flyTo only when it's a real GPS fix, not cached
            val isCached = d.address == "Last known location"
            if (isCached) {
                map.setCamera(CameraOptions.Builder().center(d.point.toMapboxPoint()).zoom(15.0).build())
            } else {
                map.flyTo(CameraOptions.Builder().center(d.point.toMapboxPoint()).zoom(16.0).build())
            }
            myCentred.value = true
        }
    }
    LaunchedEffect(trackedIds) {
        val added = trackedIds.filter { it !in previousTrackedIds }
        if (added.isNotEmpty()) pendingFriendFocusId = added.last()
        previousTrackedIds = trackedIds
    }
    LaunchedEffect(pendingFriendFocusId, friendLocations, mapRefsHolder.value) {
        val id = pendingFriendFocusId ?: return@LaunchedEffect
        val loc = friendLocations[id] ?: return@LaunchedEffect
        mapRefsHolder.value?.map?.flyTo(
            CameraOptions.Builder().center(loc.point.toMapboxPoint()).zoom(16.5).build()
        )
        pendingFriendFocusId = null
    }
    LaunchedEffect(searchQuery) {
        if (searchQuery.length < 2) {
            searchResults = emptyList(); return@LaunchedEffect
        }
        delay(400L)
        isSearching = true
        try {
            val encoded = Uri.encode(searchQuery)
            val raw = withContext(Dispatchers.IO) {
                URL("https://api.mapbox.com/geocoding/v5/mapbox.places/$encoded.json?access_token=$MAPBOX_TOKEN&limit=6").readText()
            }
            val arr = JSONObject(raw).getJSONArray("features")
            searchResults = (0 until arr.length()).map { i ->
                val f = arr.getJSONObject(i)
                val c = f.getJSONObject("geometry").getJSONArray("coordinates")
                Triple(f.getString("place_name"), c.getDouble(1), c.getDouble(0))
            }
        } catch (_: Exception) {
            searchResults = emptyList()
        }
        isSearching = false
    }
    LaunchedEffect(routeDestination) {
        val dest = routeDestination ?: run {
            routePoints = emptyList(); routeInfo = null; return@LaunchedEffect
        }
        val origin = myLocation?.point ?: return@LaunchedEffect
        isSearching = true
        try {
            val url = "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                    "${origin.longitude()},${origin.latitude()};${dest.third},${dest.second}" +
                    "?geometries=geojson&overview=full&access_token=$MAPBOX_TOKEN"
            val raw = withContext(Dispatchers.IO) { URL(url).readText() }
            val route = JSONObject(raw).getJSONArray("routes").getJSONObject(0)
            val dist = route.getDouble("distance") / 1000.0
            val dur = route.getDouble("duration") / 60.0
            val coords = route.getJSONObject("geometry").getJSONArray("coordinates")
            routePoints = (0 until coords.length()).map { i ->
                val c = coords.getJSONArray(i)
                Point.fromLngLat(c.getDouble(0), c.getDouble(1))
            }
            routeInfo = Pair(dist, dur)
            mapRefsHolder.value?.map?.flyTo(
                CameraOptions.Builder()
                    .center(MapboxPoint.fromLngLat(dest.third, dest.second))
                    .zoom(12.0).build()
            )
        } catch (_: Exception) {
            routePoints = emptyList(); routeInfo = null
        }
        isSearching = false
    }

    // Pre-compute destination pin bitmaps for arrival alerts
    val alertPinBitmaps = remember(arrivalAlerts.map { it.id }.joinToString()) {
        arrivalAlerts.mapIndexed { i, alert ->
            val colorHex = FriendColorHex[trackedIds.indexOf(alert.contactId)
                .let { if (it < 0) i else it } % FriendColorHex.size]
            alert.id to createDestinationMarkerBitmap(alert.contactName, colorHex, sizePx = 100)
        }.toMap()
    }

    // Pre-compute avatar bitmaps (remember must not be in a loop)
    val avatarBitmaps = remember(
        trackedIds.joinToString(),
        trackedIds.joinToString { id -> (friendAvatars[id] ?: "").take(32) }
    ) {
        trackedIds.mapIndexed { slot, id ->
            val hex = FriendColorHex[slot % FriendColorHex.size]
            val sf = savedFriends.find { it.trackId == id }
            val name = sf?.displayName?.ifBlank { id } ?: id
            val avatarB64 = friendAvatars[id] ?: sf?.avatarBase64 ?: ""
            id to createCircularAvatarBitmap(name, avatarB64, hex, sizePx = 120)
        }.toMap()
    }
    val myAvatarBitmap = remember(myAvatarBase64, myAvatarVersion) {
        val col = if (isRecording) "#FF4757" else "#00D4FF"
        createCircularAvatarBitmap("Me", myAvatarBase64, col, sizePx = 108)
    }

    // ── One-time setup: create annotation managers and register markers ────────
    // Tracks the actual PointAnnotation objects so we can move them in-place
    val friendAnnotations = remember { mutableStateMapOf<String, com.mapbox.maps.plugin.annotation.generated.PointAnnotation>() }
    val alertAnnotations = remember { mutableStateMapOf<String, com.mapbox.maps.plugin.annotation.generated.PointAnnotation>() }
    val selfAnnotation = remember { mutableStateOf<com.mapbox.maps.plugin.annotation.generated.PointAnnotation?>(null) }
    val accuracyAnnotation = remember { mutableStateOf<com.mapbox.maps.plugin.annotation.generated.PolygonAnnotation?>(null) }

    // When map style changes, the annotation managers are replaced — clear cached refs
    LaunchedEffect(mapRefsHolder.value) {
        if (mapRefsHolder.value != null) {
            // Clear cached annotations — they belong to the old managers
            friendAnnotations.clear()
            alertAnnotations.clear()
            selfAnnotation.value = null
            accuracyAnnotation.value = null
        }
    }

    // ── Effect 1: Structure changes only (friends added/removed, avatars, style) ──
    LaunchedEffect(
        mapRefsHolder.value,
        trackedIds.joinToString(),
        trackedIds.joinToString { id -> (friendAvatars[id] ?: "").take(32) },
        arrivalAlerts.map { it.id }.joinToString(),
        placedPin.value
    ) {
        val refs = mapRefsHolder.value ?: return@LaunchedEffect
        val style = refs.map.getStyle() ?: return@LaunchedEffect

        // Remove annotations for friends no longer tracked
        val toRemove = friendAnnotations.keys.filter { it !in trackedIds }
        toRemove.forEach { id ->
            friendAnnotations[id]?.let { refs.pointMgr.delete(it) }
            friendAnnotations.remove(id)
        }

        // Add annotations for new friends
        // KEY FIX: skip myTrackId here — self is handled by selfAnnotation separately
        trackedIds.forEach { id ->
            if (id == myTrackId) return@forEach  // never add self to friend annotations
            val bmp = avatarBitmaps[id] ?: return@forEach
            val imgId = "avatar-$id"
            try { style.addImage(imgId, bmp) } catch (_: Exception) {}

            if (id !in friendAnnotations) {
                val loc = friendLocations[id] ?: return@forEach
                val ann = refs.pointMgr.create(
                    PointAnnotationOptions()
                        .withPoint(loc.point.toMapboxPoint())
                        .withIconImage(imgId)
                        .withIconRotate(loc.heading.toDouble())
                )
                friendAnnotations[id] = ann
            } else {
                // Friend already has annotation — just refresh avatar image in case it changed
                try { style.addImage("avatar-$id", bmp) } catch (_: Exception) {}
            }
        }

        // Self avatar — create once, never recreated unless style changes
        try { style.addImage("avatar-self", myAvatarBitmap) } catch (_: Exception) {}
        val myLoc = myLocation
        if (myLoc != null && selfAnnotation.value == null) {
            selfAnnotation.value = refs.pointMgr.create(
                PointAnnotationOptions()
                    .withPoint(myLoc.point.toMapboxPoint())
                    .withIconImage("avatar-self")
                    .withIconRotate(refs.myHeading.toDouble())
            )
        }

        // Remove annotations for cancelled alerts
        val alertIds = arrivalAlerts.map { it.id }
        val alertsToRemove = alertAnnotations.keys.filter { it !in alertIds }
        alertsToRemove.forEach { id ->
            alertAnnotations[id]?.let { refs.pointMgr.delete(it) }
            alertAnnotations.remove(id)
        }

        // Arrival alert destination pins
        arrivalAlerts.forEachIndexed { i, alert ->
            alertPinBitmaps[alert.id]?.let { bmp ->
                val imgId = "dest-pin-${alert.id}"
                try { style.addImage(imgId, bmp) } catch (_: Exception) {}
                if (alert.id !in alertAnnotations) {
                    val ann = refs.pointMgr.create(
                        PointAnnotationOptions()
                            .withPoint(MapboxPoint.fromLngLat(alert.lng, alert.lat))
                            .withIconImage(imgId)
                    )
                    alertAnnotations[alert.id] = ann
                }
            }
        }

        // Dropped pin
        placedPin.value?.let { pin ->
            val bmp = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888)
            val cv = Canvas(bmp)
            val pt = Paint(Paint.ANTI_ALIAS_FLAG)
            pt.color = android.graphics.Color.parseColor("#FF3B30")
            cv.drawCircle(40f, 40f, 36f, pt)
            pt.color = android.graphics.Color.WHITE
            pt.style = Paint.Style.STROKE; pt.strokeWidth = 7f
            cv.drawCircle(40f, 40f, 30f, pt)
            try { style.addImage("placed-pin", bmp) } catch (_: Exception) {}
            refs.pointMgr.create(
                PointAnnotationOptions()
                    .withPoint(pin.toMapboxPoint())
                    .withIconImage("placed-pin")
            )
        }
    }

    // ── Effect 2: Polylines only — completely isolated from point annotations ──
    LaunchedEffect(
        mapRefsHolder.value,
        pathPoints.size,
        routePoints.size,
        friendPaths.keys.joinToString()
    ) {
        val refs = mapRefsHolder.value ?: return@LaunchedEffect
        // Delete and recreate path polyline (simpler than update in Mapbox v11)
        refs.polylineMgr.deleteAll()
        if (pathPoints.size >= 2) {
            refs.polylineMgr.create(
                PolylineAnnotationOptions()
                    .withPoints(pathPoints.map { it.toMapboxPoint() })
                    .withLineColor("#00D4FF").withLineWidth(4.0))
        }
        // Recreate friend paths and route (they're lightweight)
        friendPaths.forEach { (id, pts) ->
            val slot = trackedIds.indexOf(id)
            val hex = FriendColorHex[if (slot >= 0) slot % FriendColorHex.size else 0]
            if (pts.size >= 2)
                refs.polylineMgr.create(
                    PolylineAnnotationOptions()
                        .withPoints(pts.map { it.toMapboxPoint() })
                        .withLineColor(hex).withLineWidth(3.0))
        }
        if (routePoints.size >= 2)
            refs.polylineMgr.create(
                PolylineAnnotationOptions()
                    .withPoints(routePoints.map { it.toMapboxPoint() })
                    .withLineColor("#FF9500").withLineWidth(5.0))
    }

    // ── Effect 3: Position updates — moves pins in-place with dead-band filtering
    // Uses 4 decimal places (~11m precision) so minor GPS noise doesn't trigger redraws
    val friendLocationKey = friendLocations.entries
        .filter { it.key != myTrackId }  // exclude self from this key
        .joinToString { "${it.key}:${it.value?.point?.latitude()?.let { l -> "%.4f".format(l) }}" }

    // ── Throttled annotation updates with dead band ───────────────────────────────
    val lastAnnotationUpdateMs = remember { mutableStateMapOf<String, Long>() }
    val MIN_UPDATE_MS = 50L         // was 120 — allow faster smooth movement
    val DEAD_BAND_DEG = 0.000008    // ~0.9m — was 0.00003 (~3.3m), which caused visible snaps

    LaunchedEffect(
        friendLocationKey,
        myLocation?.point?.latitude()?.let { "%.4f".format(it) },
        myLocation?.point?.longitude()?.let { "%.4f".format(it) },
        rawHeading
    ) {
        val refs = mapRefsHolder.value ?: return@LaunchedEffect
        val now = System.currentTimeMillis()

        // Update friend markers
        friendAnnotations.forEach { (id, ann) ->
            if (id == myTrackId) return@forEach
            val loc = friendLocations[id] ?: return@forEach
            val lastUpdate = lastAnnotationUpdateMs[id] ?: 0L
            if (now - lastUpdate < MIN_UPDATE_MS) return@forEach

            val current = ann.point
            val distDeg = kotlin.math.sqrt(
                (loc.point.latitude() - current.latitude()).pow(2) +
                (loc.point.longitude() - current.longitude()).pow(2)
            )
            if (distDeg < DEAD_BAND_DEG) return@forEach

            ann.point = loc.point.toMapboxPoint()
            ann.iconRotate = loc.heading.toDouble()
            refs.pointMgr.update(ann)
            lastAnnotationUpdateMs[id] = now
        }

        // Move self marker in-place
        val myLoc = myLocation
        val selfAnn = selfAnnotation.value
        if (myLoc != null && selfAnn != null) {
            val lastSelfUpdate = lastAnnotationUpdateMs["self"] ?: 0L
            if (now - lastSelfUpdate >= MIN_UPDATE_MS) {
                selfAnn.point = myLoc.point.toMapboxPoint()
                selfAnn.iconRotate = refs.myHeading.toDouble()
                refs.pointMgr.update(selfAnn)
                lastAnnotationUpdateMs["self"] = now
            }
        }

        // Accuracy circle REMOVED - cleaner UI
    }

    // Effect 4: Self avatar image refresh (only when avatar changes, not position)
    LaunchedEffect(myAvatarBase64, myAvatarVersion, isRecording, mapRefsHolder.value) {
        val refs = mapRefsHolder.value ?: return@LaunchedEffect
        val style = refs.map.getStyle() ?: return@LaunchedEffect
        try { style.addImage("avatar-self", myAvatarBitmap) } catch (_: Exception) {}
        selfAnnotation.value?.let { ann ->
            refs.pointMgr.update(ann)
        }
    }

    // Place self avatar marker immediately when both map and location exist
    LaunchedEffect(mapRefsHolder.value, myLocation != null) {
        val refs = mapRefsHolder.value ?: return@LaunchedEffect
        val myLoc = myLocation ?: return@LaunchedEffect
        val style = refs.map.getStyle() ?: return@LaunchedEffect

        // Place self pin immediately if not yet placed
        if (selfAnnotation.value == null) {
            try { style.addImage("avatar-self", myAvatarBitmap) } catch (_: Exception) {}
            selfAnnotation.value = refs.pointMgr.create(
                PointAnnotationOptions()
                    .withPoint(myLoc.point.toMapboxPoint())
                    .withIconImage("avatar-self")
                    .withIconRotate(refs.myHeading.toDouble())
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewCtx ->
                MapView(viewCtx).also { mapView ->
                    val styleUri = when (mapTheme) {
                        MapTheme.DARK -> Style.DARK
                        MapTheme.LIGHT -> Style.MAPBOX_STREETS
                        MapTheme.SATELLITE -> Style.SATELLITE_STREETS
                    }
                    mapView.mapboxMap.loadStyle(styleUri) { _ ->
                        val pointMgr = mapView.annotations.createPointAnnotationManager()
                        val polylineMgr = mapView.annotations.createPolylineAnnotationManager()
                        val polygonMgr = mapView.annotations.createPolygonAnnotationManager()
                        mapRefsHolder.value =
                            MapRefs(mapView.mapboxMap, pointMgr, polylineMgr, polygonMgr)

                        // ── Set initial camera to cached location OR India fallback ──
                        val startCenter = initialCenter?.toMapboxPoint()
                            ?: MapboxPoint.fromLngLat(78.9629, 20.5937)
                        val startZoom = if (initialCenter != null) 15.0 else 4.0
                        mapView.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(startCenter)
                                .zoom(startZoom)
                                .build()
                        )
                    }
                    // Compass: bottom-left, above the MY LOCATION card
                    val density = viewCtx.resources.displayMetrics.density
                    mapView.compass.updateSettings {
                        position = Gravity.BOTTOM or Gravity.START
                        marginBottom = 130f * density
                        marginLeft = 16f * density
                    }
                    mapView.location.updateSettings { enabled = false }
                    if (onLongPressMap != null) {
                        mapView.gestures.addOnMapLongClickListener { mbPoint ->
                            onLongPressMap(
                                Point.fromLngLat(
                                    mbPoint.longitude(),
                                    mbPoint.latitude()
                                )
                            )
                            true
                        }
                    }
                    mapView.gestures.addOnMapClickListener { mbPoint ->
                        if (pinModeActive.value) {
                            placedPin.value =
                                Point.fromLngLat(mbPoint.longitude(), mbPoint.latitude())
                            pinModeActive.value = false
                            true
                        } else false
                    }
                }
            },
            update = { mapView ->
                val targetUri = when (mapTheme) {
                    MapTheme.DARK -> Style.DARK
                    MapTheme.LIGHT -> Style.MAPBOX_STREETS
                    MapTheme.SATELLITE -> Style.SATELLITE_STREETS
                }
                val loaded = mapView.mapboxMap.getStyle()
                if (loaded != null && loaded.styleURI != targetUri) {
                    // Clear old managers' cached annotations before style switch
                    // so they cannot re-add stale pins to the incoming style
                    mapRefsHolder.value?.let { old ->
                        old.pointMgr.deleteAll()
                        old.polylineMgr.deleteAll()
                        old.polygonMgr.deleteAll()
                    }
                    mapView.mapboxMap.loadStyle(targetUri) { _ ->
                        val pm = mapView.annotations.createPointAnnotationManager()
                        val lm = mapView.annotations.createPolylineAnnotationManager()
                        val pgm = mapView.annotations.createPolygonAnnotationManager()
                        mapRefsHolder.value = MapRefs(mapView.mapboxMap, pm, lm, pgm)
                    }
                }
            }
        )

        // ── Top bar: status card (left) + theme toggle (right) ─────────────────
        val liveTrackedCount = trackedIds.count { id -> friendLocations[id]?.isRecent == true }
        Row(
            Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()  // ← replace hardcoded top padding
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status card (React map-view.tsx style)
            Surface(
                shape = RoundedCornerShape(16.dp), color = overlayBg,
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, overlayBorder)
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) GreenOnline else TextOnDarkMuted)
                    )
                    Text(
                        if (isConnected) "Live Tracking" else "Offline",
                        fontSize = 13.sp, color = overlayText, fontWeight = FontWeight.Bold
                    )
                    if (liveTrackedCount > 0) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldGreen.copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "$liveTrackedCount live",
                                fontSize = 10.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    if (myLocation != null) {
                        val (_, color) = accuracyLabel(myLocation.accuracy)
                        Text(
                            "±${myLocation.accuracy.toInt()}m",
                            fontSize = 11.sp,
                            color = color,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (showSearch) EmeraldGreen.copy(0.2f) else overlayBg)
                    .border(1.dp, overlayBorder, RoundedCornerShape(12.dp))
                    .clickable {
                        showSearch = !showSearch
                        if (!showSearch) {
                            searchQuery = ""; searchResults = emptyList()
                        }
                    },
                Alignment.Center
            ) {
                Icon(
                    Icons.Default.Search,
                    null,
                    tint = if (showSearch) EmeraldGreen else overlayText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ── Places search overlay ─────────────────────────────────────────────
        if (showSearch) {
            Column(
                Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 56.dp, end = 12.dp)
                    .width(185.dp)
            ) {
                // ── Input row ─────────────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(12.dp), color = overlayBg,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, overlayBorder)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = EmeraldGreen,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Search,
                                null,
                                tint = overlayMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(color = overlayText, fontSize = 13.sp),
                            cursorBrush = SolidColor(EmeraldGreen),
                            modifier = Modifier.weight(1f),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) Text(
                                    "Search places...",
                                    color = overlayMuted,
                                    fontSize = 13.sp
                                )
                                inner()
                            }
                        )
                    }
                }
                // ── Results list ──────────────────────────────────────────────
                if (searchResults.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp), color = overlayBg,
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, overlayBorder)
                    ) {
                        Column {
                            searchResults.forEachIndexed { i, result ->
                                if (i > 0) Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(overlayBorder)
                                )
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 10.dp,
                                            end = 6.dp,
                                            top = 8.dp,
                                            bottom = 8.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Column(
                                        Modifier
                                            .weight(1f)
                                            .clickable {
                                                mapRefsHolder.value?.map?.flyTo(
                                                    CameraOptions.Builder()
                                                        .center(
                                                            MapboxPoint.fromLngLat(
                                                                result.third,
                                                                result.second
                                                            )
                                                        )
                                                        .zoom(14.0).build()
                                                )
                                                showSearch = false
                                                searchQuery = ""
                                                searchResults = emptyList()
                                            }
                                    ) {
                                        Text(
                                            result.first.substringBefore(","),
                                            fontSize = 12.sp,
                                            color = overlayText,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1
                                        )
                                        Text(
                                            result.first,
                                            fontSize = 9.sp,
                                            color = overlayMuted,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    // Route button
                                    Box(
                                        Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFF9500).copy(alpha = 0.15f))
                                            .clickable {
                                                routeDestination = result
                                                showSearch = false
                                                searchQuery = ""
                                                searchResults = emptyList()
                                            },
                                        Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Navigation,
                                            null,
                                            tint = Color(0xFFFF9500),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Recording indicator ────────────────────────────────────────────────
        if (isRecording) {
            Row(
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(RedRecord.copy(alpha = 0.15f))
                    .border(1.dp, RedRecord.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(RedRecord))
                Text(
                    "Recording · ${pathPoints.size} pts",
                    fontSize = 11.sp,
                    color = RedRecord,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── Placement mode banner ────────────────────────────────────────────
        if (placementMode) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = if (isRecording) 104.dp else 64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen)))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📍", fontSize = 14.sp)
                    Text(
                        "Long press on the map to place destination",
                        fontSize = 13.sp, color = DarkBg, fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // ── FRIEND AVATAR SQUARES — top left, below top bar ───────────────────
        if (trackedIds.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(
                        start = 14.dp,
                        top = when {
                            isRecording && placementMode -> 148.dp
                            isRecording || placementMode -> 108.dp
                            else -> 72.dp
                        }
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trackedIds.forEachIndexed { slot, id ->
                    val loc = friendLocations[id]
                    val friendProfile = savedFriends.find { it.trackId == id }
                    val friendColor = FriendColors[slot % FriendColors.size]
                    val friendName = friendProfile?.displayName?.ifBlank { id } ?: id
                    val avatarB64 = friendAvatars[id] ?: friendProfile?.avatarBase64 ?: ""
                    val isLive = loc?.isRecent == true

                    FriendMapSquareCard(
                        name = friendName,
                        avatarBase64 = avatarB64,
                        color = friendColor,
                        isLive = isLive,
                        speedKmh = loc?.speedKmh ?: 0f,
                        heading = loc?.heading ?: 0f,
                        roadName = loc?.roadName ?: "",
                        overlayBg = overlayBg,
                        overlayBorder = overlayBorder,
                        overlayText = overlayText,
                        overlayMuted = overlayMuted,
                        onTap = {
                            loc?.let {
                                mapRefsHolder.value?.map?.flyTo(
                                    CameraOptions.Builder().center(it.point.toMapboxPoint())
                                        .zoom(17.0).build()
                                )
                            }
                        }
                    )
                }
            }
        }

        // ── RIGHT FAB COLUMN — theme / pin / locate / record ────────────
        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 14.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Map theme cycle button
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(overlayBg)
                    .border(1.dp, overlayBorder, CircleShape)
                    .clickable {
                        val next =
                            MapTheme.entries[(MapTheme.entries.indexOf(mapTheme) + 1) % MapTheme.entries.size]
                        onMapThemeChange(next)
                    },
                Alignment.Center
            ) {
                Text(mapTheme.icon, fontSize = 20.sp)
            }
            // Pin drop
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (pinModeActive.value) EmeraldGreen.copy(0.25f) else overlayBg)
                    .border(
                        1.dp,
                        if (pinModeActive.value) EmeraldGreen else overlayBorder,
                        CircleShape
                    )
                    .clickable {
                        pinModeActive.value =
                            !pinModeActive.value; if (!pinModeActive.value) placedPin.value = null
                    },
                Alignment.Center
            ) {
                Icon(
                    Icons.Default.PinDrop, null,
                    tint = if (pinModeActive.value) EmeraldGreen else overlayText,
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(overlayBg)
                    .border(1.dp, overlayBorder, CircleShape)
                    .clickable {
                        myLocation?.let { d ->
                            mapRefsHolder.value?.map?.flyTo(
                                CameraOptions.Builder().center(d.point.toMapboxPoint()).zoom(16.0)
                                    .build()
                            )
                        }
                    },
                Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) RedRecord.copy(0.18f) else overlayBg)
                    .border(1.dp, if (isRecording) RedRecord else overlayBorder, CircleShape)
                    .clickable { if (isRecording) onStopRecording() else onStartRecording() },
                Alignment.Center
            ) {
                Icon(
                    if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                    null,
                    tint = if (isRecording) RedRecord else overlayText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ── Pin-mode hint banner ────────────────────────────────────────────
        if (pinModeActive.value) {
            Row(
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = if (isRecording) 104.dp else 64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFF3B30).copy(0.15f))
                    .border(1.dp, Color(0xFFFF3B30).copy(0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.PinDrop,
                    null,
                    tint = Color(0xFFFF3B30),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    "Tap map to drop pin",
                    fontSize = 12.sp,
                    color = Color(0xFFFF3B30),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── Placed pin action card (shown when pin placed, no route yet) ──────
        if (placedPin.value != null && routeInfo == null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 156.dp)
                    .widthIn(max = 240.dp),
                shape = RoundedCornerShape(20.dp), color = overlayBg,
                shadowElevation = 10.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFFF3B30).copy(0.4f)
                )
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.PinDrop,
                        null,
                        tint = Color(0xFFFF3B30),
                        modifier = Modifier.size(18.dp)
                    )
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Dropped Pin",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = overlayText
                        )
                        Text(
                            "%.4f, %.4f".format(
                                placedPin.value!!.latitude(),
                                placedPin.value!!.longitude()
                            ), fontSize = 10.sp, color = overlayMuted
                        )
                    }
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF9500).copy(0.15f))
                            .clickable {
                                routeDestination = Triple(
                                    "Dropped Pin",
                                    placedPin.value!!.latitude(),
                                    placedPin.value!!.longitude()
                                )
                            },
                        Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Navigation,
                            null,
                            tint = Color(0xFFFF9500),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Box(
                        Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(overlayBorder)
                            .clickable { placedPin.value = null; pinModeActive.value = false },
                        Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            null,
                            tint = overlayText,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        routeInfo?.let { (km, mins) ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 156.dp)
                    .widthIn(min = 220.dp, max = 280.dp),
                shape = RoundedCornerShape(20.dp), color = overlayBg,
                shadowElevation = 10.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFFF9500).copy(0.4f)
                )
            ) {
                Column {
                    // ── Route summary row ──────────────────────────────────────
                    Row(
                        Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9500).copy(0.15f)),
                            Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Navigation,
                                null,
                                tint = Color(0xFFFF9500),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (km >= 1.0) "%.1f km".format(km) else "%.0f m".format(km * 1000),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = overlayText
                            )
                            Text(
                                "${mins.toInt()} min · ${routeDestination?.first?.substringBefore(",")}",
                                fontSize = 10.sp, color = overlayMuted, maxLines = 1
                            )
                        }
                    }
                    // ── Divider ────────────────────────────────────────────────
                    Box(Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(overlayBorder))
                    // ── Cancel Route button ────────────────────────────────────
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                            .clickable {
                                routeDestination = null
                                placedPin.value = null
                                pinModeActive.value = false
                            }
                            .padding(vertical = 11.dp),
                        Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                null,
                                tint = RedRecord,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Cancel Route",
                                fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                                color = RedRecord
                            )
                        }
                    }
                }
            }
        }

        // ── Bottom left: my location coords ───────────────────────────────────
        myLocation?.let { loc ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(start = 14.dp, bottom = 14.dp),
                shape = RoundedCornerShape(16.dp), color = overlayBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, overlayBorder)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "MY LOCATION",
                        fontSize = 9.sp,
                        color = overlayMuted,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "%.5f, %.5f".format(loc.point.latitude(), loc.point.longitude()),
                        fontSize = 11.sp,
                        color = overlayText,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    if (loc.speedKmh > 0.5f) Text(
                        "%.1f km/h".format(loc.speedKmh),
                        fontSize = 10.sp,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── Friend map card — Life360-style circular chip ──────────────────────────
@Composable
fun FriendMapSquareCard(
    name: String,
    avatarBase64: String,
    color: Color,
    isLive: Boolean,
    speedKmh: Float,
    heading: Float = 0f,
    roadName: String = "",
    overlayBg: Color,
    overlayBorder: Color,
    overlayText: Color,
    overlayMuted: Color,
    onTap: () -> Unit
) {
    val bitmap = remember(avatarBase64) { base64ToBitmap(avatarBase64) }
    val shortName = name.split(" ").first().take(9)
    val statusText = when {
        isLive && speedKmh > 0.5f && roadName.isNotBlank() ->
            "%.0f km/h · $roadName".format(speedKmh)

        isLive && speedKmh > 0.5f -> "%.0f km/h".format(speedKmh)
        isLive -> "Live"
        else -> "Offline"
    }

    Surface(
        modifier = Modifier.clickable { onTap() },
        shape = RoundedCornerShape(20.dp),
        color = overlayBg,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(
            if (isLive) 1.5.dp else 1.dp,
            if (isLive) color.copy(alpha = 0.7f) else overlayBorder
        )
    ) {
        Row(
            Modifier.padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    Alignment.Center
                ) {}
                Box(
                    Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.85f)),
                    Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(29.dp)
                            .clip(CircleShape)
                            .background(if (bitmap != null) Color.Transparent else color.copy(0.5f)),
                        Alignment.Center
                    ) {
                        if (bitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = bitmap.asImageBitmap(), contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                name.firstOrNull()?.toString()?.uppercase() ?: "?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
                if (isLive) {
                    Box(
                        Modifier
                            .size(11.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(GreenOnline)
                            .border(1.5.dp, overlayBg, CircleShape)
                    )
                }
                // Directional chevron when moving
                if (speedKmh > 0.5f) {
                    androidx.compose.material3.Icon(
                        androidx.compose.material.icons.Icons.Default.Navigation,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = (-4).dp)
                            .graphicsLayer { rotationZ = heading - 90f }  // -90° offset for correct direction
                    )
                }
            }
            Column {
                Text(
                    shortName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = overlayText,
                    maxLines = 1
                )
                Text(
                    statusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isLive) color else overlayMuted
                )
            }
        }
    }
}

@Composable
fun FriendMapChip(
    name: String,
    avatarBase64: String,
    color: Color,
    isLive: Boolean,
    speedKmh: Float,
    overlayBg: Color,
    overlayBorder: Color,
    overlayText: Color,
    overlayMuted: Color,
    onTap: () -> Unit
) {
    val bitmap = remember(avatarBase64) { base64ToBitmap(avatarBase64) }
    val shortName = name.split(" ").first().take(10)

    Surface(
        modifier = Modifier.clickable { onTap() },
        shape = RoundedCornerShape(24.dp),
        color = overlayBg,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isLive) color.copy(alpha = 0.8f) else overlayBorder
        )
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        if (bitmap != null) Brush.linearGradient(
                            listOf(
                                color.copy(0.2f),
                                color.copy(0.2f)
                            )
                        )
                        else Brush.linearGradient(listOf(color.copy(0.7f), color))
                    )
                    .border(1.5.dp, color, CircleShape),
                Alignment.Center
            ) {
                if (bitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        name.firstOrNull()?.toString()?.uppercase() ?: "?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkBg
                    )
                }
            }
            Spacer(Modifier.width(7.dp))
            Column {
                Text(
                    shortName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = overlayText,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isLive) GreenOnline else overlayMuted)
                    )
                    Text(
                        text = if (isLive && speedKmh > 0.5f) "%.0f km/h".format(speedKmh) else if (isLive) "Live" else "Offline",
                        fontSize = 9.sp,
                        color = if (isLive) GreenOnline else overlayMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ─── Friends Tab ─────────────────────────────────────────────────────────────
@Composable
fun FriendsTab(
    savedFriends: List<SavedFriend>, trackedIds: List<String>,
    friendLocations: Map<String, FriendLocation?>,
    friendAvatars: Map<String, String> = emptyMap(),
    onShowFriendDialog: () -> Unit, onToggleTrack: (String) -> Unit,
    onRemoveSavedFriend: (String) -> Unit, onViewProfile: (SavedFriend) -> Unit,
    onOpenChatWith: (SavedFriend) -> Unit
) {
    val liveCount = trackedIds.count { id -> friendLocations[id]?.isRecent == true }
    val trackCount = trackedIds.size
    val totalFriends = savedFriends.size
    var searchQuery by remember { mutableStateOf("") }

    val filteredFriends = remember(savedFriends, searchQuery) {
        if (searchQuery.isBlank()) savedFriends
        else savedFriends.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true) ||
                    it.trackId.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(Modifier
        .fillMaxSize()
        .background(DarkBg)) {
        // ── Sticky header with search ──────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkSurface,
            shadowElevation = 4.dp
        ) {
            Column(
                Modifier
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
            ) {
                Text(
                    "Friends",
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                    color = TextOnDark, letterSpacing = (-0.3).sp
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCard)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = TextOnDarkMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = TextOnDark, fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(EmeraldGreen),
                        modifier = Modifier.weight(1f),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) Text(
                                "Find or start a conversation",
                                color = TextOnDarkMuted,
                                fontSize = 14.sp
                            )
                            inner()
                        }
                    )
                }
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldGreen)
                        .clickable(enabled = trackedIds.size < MAX_FRIENDS) { onShowFriendDialog() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "Add Friend",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // ── List ─────────────────────────────────────────────────────────
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            if (filteredFriends.isEmpty() && searchQuery.isNotBlank()) {
                Box(Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = TextOnDarkMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No contacts found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnDark
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Try a different search term",
                            fontSize = 13.sp,
                            color = TextOnDarkMuted
                        )
                    }
                }
            } else if (savedFriends.isEmpty()) {
                Box(Modifier
                    .fillMaxWidth()
                    .padding(vertical = 56.dp), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            EmeraldDeep.copy(0.3f),
                                            EmeraldGreen.copy(0.3f)
                                        )
                                    )
                                ),
                            Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Group,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No contacts yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextOnDark
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Add a contact's Track ID to get started",
                            fontSize = 13.sp,
                            color = TextOnDarkMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen)))
                                .clickable { onShowFriendDialog() }
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                "+ Add First Contact",
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkBg
                            )
                        }
                    }
                }
            } else {
                filteredFriends.forEachIndexed { index, sf ->
                    val latestAvatar =
                        friendAvatars[sf.trackId]?.takeIf { it.isNotBlank() } ?: sf.avatarBase64
                    val sfWithAvatar =
                        if (latestAvatar != sf.avatarBase64) sf.copy(avatarBase64 = latestAvatar) else sf
                    DarkFriendProfileCard(
                        sf = sfWithAvatar,
                        isTracking = sf.trackId in trackedIds,
                        liveLocation = friendLocations[sf.trackId],
                        onViewProfile = { onViewProfile(sfWithAvatar) },
                        onToggleTrack = { onToggleTrack(sf.trackId) },
                        onOpenChat = { onOpenChatWith(sfWithAvatar) },
                        slotOverride = index
                    )
                    Spacer(Modifier.height(12.dp))
                }
                val untitledTracked =
                    trackedIds.filter { id -> savedFriends.none { it.trackId == id } }
                if (untitledTracked.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp)); DarkSectionLabel("TRACKING WITHOUT PROFILE"); Spacer(
                        Modifier.height(10.dp)
                    )
                    untitledTracked.forEachIndexed { idx, id ->
                        val avatarB64 = friendAvatars[id] ?: ""
                        DarkFriendProfileCard(
                            sf = SavedFriend(
                                trackId = id,
                                displayName = "",
                                email = "",
                                avatarBase64 = avatarB64
                            ),
                            isTracking = true,
                            liveLocation = friendLocations[id],
                            onViewProfile = {},
                            onToggleTrack = { onToggleTrack(id) },
                            onOpenChat = {},
                            slotOverride = trackedIds.indexOf(id).takeIf { it >= 0 } ?: idx
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

// ─── Profile Tab ──────────────────────────────────────────────────────────────
@Composable
fun ProfileTab(
    user: AppUser, myTrackId: String, trackIdReady: Boolean,
    myLocation: LocationData?, isConnected: Boolean, gpsAvailable: Boolean,
    bgTrackingOn: Boolean, myAvatarBase64: String,
    myAvatarVersion: Int = 0,
    onPickProfilePicture: () -> Unit,
    onStartBgTracking: () -> Unit, onStopBgTracking: () -> Unit,
    onStartRecording: () -> Unit, onStopRecording: () -> Unit,
    isRecording: Boolean, pathPoints: List<Point>, onSignOut: () -> Unit,
    onGetNotified: () -> Unit = {},
    activeAlertCount: Int = 0,
    arrivalAlerts: List<ArrivalAlert> = emptyList(),
    onCancelAlert: (String) -> Unit = {},
    savedFriends: List<SavedFriend> = emptyList(),
    trackedIds: List<String> = emptyList(),
    sosActive: Boolean = false,
    onSendAlert: (List<String>) -> Unit = {},
    onShowPrivacy: () -> Unit = {}
) {
    var showAlertPicker by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val (accLabel, accColor) = myLocation?.let { accuracyLabel(it.accuracy) }
        ?: ("—" to TextOnDarkMuted)
    val myBitmap = remember(myAvatarBase64, myAvatarVersion) { base64ToBitmap(myAvatarBase64) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()  // ← add this
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Profile Card (React-style with cover banner + overlapping avatar) ──
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLight)
        ) {
            Column {
                // Cover banner gradient
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen)))
                ) {
                    // Sign-out button — top right
                    TextButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                    ) {
                        Text(
                            "Sign out",
                            color = Color.White.copy(0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                // Profile content — overlapping the banner
                Column(Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
                    .padding(horizontal = 20.dp)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        // Discord-style round avatar overlapping banner
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                Modifier
                                    .size(88.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurface)
                                    .border(4.dp, DarkSurface, CircleShape)
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(EmeraldGreen)
                                        .clickable { onPickProfilePicture() },
                                    Alignment.Center
                                ) {
                                    if (myBitmap != null) {
                                        androidx.compose.foundation.Image(
                                            bitmap = myBitmap.asImageBitmap(),
                                            contentDescription = "Profile picture",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        Text(
                                            (user.displayName?.firstOrNull() ?: "U").toString()
                                                .uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 32.sp
                                        )
                                    }
                                }
                            }
                            // Online dot
                            Box(
                                Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(GreenOnline)
                                    .border(3.dp, DarkSurface, CircleShape)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier
                            .weight(1f)
                            .padding(bottom = 4.dp)) {
                            Text(
                                user.displayName ?: "User",
                                fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextOnDark
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(user.email ?: "", fontSize = 12.sp, color = TextOnDarkMuted)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    // Status badges row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Online pill
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isConnected) GreenOnline.copy(0.12f) else DarkCardAlt)
                                .border(
                                    1.dp,
                                    if (isConnected) GreenOnline.copy(0.3f) else DarkBorderLight,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isConnected) GreenOnline else TextOnDarkMuted)
                            )
                            Text(
                                if (isConnected) "Online" else "Offline",
                                fontSize = 11.sp,
                                color = if (isConnected) GreenOnline else TextOnDarkMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // GPS pill
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (gpsAvailable) accColor.copy(0.12f) else DarkCardAlt)
                                .border(
                                    1.dp,
                                    if (gpsAvailable) accColor.copy(0.3f) else DarkBorderLight,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                null,
                                tint = if (gpsAvailable) accColor else TextOnDarkMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                if (gpsAvailable) "GPS $accLabel" else "No GPS",
                                fontSize = 11.sp,
                                color = if (gpsAvailable) accColor else TextOnDarkMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Speed pill (only when moving)
                        if ((myLocation?.speedKmh ?: 0f) > 0.5f) {
                            Row(
                                Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(EmeraldGreen.copy(0.12f))
                                    .border(
                                        1.dp,
                                        EmeraldGreen.copy(0.3f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "%.0f km/h".format(myLocation!!.speedKmh),
                                    fontSize = 11.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        Column(Modifier.padding(horizontal = 16.dp)) {
            DarkSectionLabel("YOUR TRACK ID"); Spacer(Modifier.height(10.dp))
            Surface(
                Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0A1520),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldGreen.copy(0.5f))
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "TRACK ID", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp, color = EmeraldGreen.copy(0.6f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (trackIdReady) myTrackId else "Loading…",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                    var copied by remember { mutableStateOf(false) }
                    if (trackIdReady) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (copied) EmeraldGreen.copy(0.25f) else EmeraldGreen.copy(
                                        0.12f
                                    )
                                )
                                .border(1.dp, EmeraldGreen.copy(0.4f), RoundedCornerShape(12.dp))
                                .clickable {
                                    val cm =
                                        ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    cm.setPrimaryClip(
                                        android.content.ClipData.newPlainText(
                                            "Track ID",
                                            myTrackId
                                        )
                                    )
                                    copied = true
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                if (copied) "✓ Copied" else "Copy",
                                fontSize = 13.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            DarkSectionLabel("MY LOCATION"); Spacer(Modifier.height(10.dp))
            Surface(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = DarkCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLight)
            ) {
                Column(Modifier.padding(16.dp)) {
                    val addressText = when {
                        myLocation == null -> "Acquiring GPS fix…"; !gpsAvailable -> "${myLocation!!.address} (last known)"; else -> myLocation!!.address
                    }
                    Text(addressText, fontSize = 13.sp, color = TextOnDark, lineHeight = 20.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DarkMiniStat(
                            "LAT",
                            myLocation?.point?.latitude()?.let { "%.5f°".format(it) } ?: "—",
                            Modifier.weight(1f))
                        DarkMiniStat(
                            "LNG",
                            myLocation?.point?.longitude()?.let { "%.5f°".format(it) } ?: "—",
                            Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DarkMiniStat(
                            "SPEED",
                            myLocation?.speedKmh?.let {
                                if (it < 0.5f) "0.0 km/h" else "%.1f km/h".format(it)
                            } ?: "—",
                            Modifier.weight(1f))
                        Surface(
                            Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = DarkCardAlt
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    "ACCURACY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = TextOnDarkMuted
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(myLocation?.accuracy?.let { "±${it.toInt()}m" } ?: "—",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (gpsAvailable) accColor else TextOnDarkMuted,
                                        fontFamily = FontFamily.Monospace)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        if (gpsAvailable) accLabel else "Stale",
                                        fontSize = 11.sp,
                                        color = if (gpsAvailable) accColor else TextOnDarkMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            DarkSectionLabel("BACKGROUND TRACKING"); Spacer(Modifier.height(10.dp))
            Surface(
                Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                color = if (bgTrackingOn) Color(0xFF0A1520) else DarkCard,
                border = androidx.compose.foundation.BorderStroke(
                    if (bgTrackingOn) 1.5.dp else 1.dp,
                    if (bgTrackingOn) EmeraldGreen.copy(0.5f) else DarkBorderLight
                )
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (bgTrackingOn)
                                    Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen))
                                else
                                    Brush.linearGradient(listOf(DarkCardAlt, DarkCardAlt))
                            ),
                        Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn, null,
                            tint = if (bgTrackingOn) DarkBg else TextOnDarkMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (bgTrackingOn) "Sharing in background" else "Background sharing off",
                            fontWeight = FontWeight.ExtraBold, fontSize = 15.sp,
                            color = if (bgTrackingOn) EmeraldGreen else TextOnDark
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            if (bgTrackingOn) "Friends can see you when app is closed"
                            else "Enable to keep sharing when app is closed",
                            fontSize = 12.sp, color = TextOnDarkMuted, lineHeight = 17.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Switch(
                        checked = bgTrackingOn,
                        onCheckedChange = { if (it) onStartBgTracking() else onStopBgTracking() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBg, checkedTrackColor = EmeraldGreen,
                            uncheckedThumbColor = TextOnDarkMuted, uncheckedTrackColor = DarkCardAlt
                        )
                    )
                }
            }
            // ── Arrival Alerts section ────────────────────────────────────────

            // ── Privacy Settings ─────────────────────────────────────────────
            Spacer(Modifier.height(20.dp))
            PrivacySettingsSection(
                myTrackId = myTrackId,
                savedFriends = savedFriends,
                onShowFullPrivacy = onShowPrivacy
            )

            ArrivalAlertsSection(
                arrivalAlerts = arrivalAlerts,
                trackedIds = trackedIds,
                onGetNotified = onGetNotified,
                onCancelAlert = onCancelAlert
            )

            Spacer(Modifier.height(20.dp))
            DarkSectionLabel("PATH RECORDING"); Spacer(Modifier.height(10.dp))
            if (isRecording) {
                Surface(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = RedRecord.copy(alpha = 0.10f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        RedRecord.copy(alpha = 0.3f)
                    )
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(RedRecord)); Spacer(
                        Modifier.width(10.dp)
                    )
                        Text(
                            "Recording  •  ${pathPoints.size} points",
                            fontSize = 13.sp,
                            color = RedRecord,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isRecording) RedRecord.copy(alpha = 0.15f) else DarkCard)
                    .border(
                        1.dp,
                        if (isRecording) RedRecord else DarkBorderLight,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { if (isRecording) onStopRecording() else onStartRecording() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                        contentDescription = null,
                        tint = if (isRecording) RedRecord else TextOnDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        if (isRecording) "Stop Recording" else "Start Recording Path",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = if (isRecording) RedRecord else TextOnDark
                    )
                }
            }
            Spacer(Modifier.height(32.dp))

            // ── Send Alert Button ────────────────────────────────────────────────────
            DarkSectionLabel("EMERGENCY")
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (sosActive)
                            Brush.linearGradient(listOf(Color(0xFFCC1C2E), Color(0xFF8B0000)))
                        else
                            Brush.linearGradient(listOf(Color(0xFFFF4757), Color(0xFFCC1C2E)))
                    )
                    .border(
                        2.dp,
                        if (sosActive) Color(0xFFFF4757).copy(alpha = 0.5f) else Color.White.copy(
                            alpha = 0.15f
                        ),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable(enabled = !sosActive) { showAlertPicker = true },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🚨", fontSize = 22.sp)
                    Column {
                        Text(
                            if (sosActive) "Alert Sent" else "Send Alert",
                            fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White
                        )
                        Text(
                            if (sosActive) "Selected contacts notified • Location sharing on"
                            else "Choose who to alert — location sharing activates",
                            fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Sends an in-app emergency alert to chosen contacts and enables background location.",
                fontSize = 11.sp, color = TextOnDarkMuted, lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            if (showAlertPicker) {
                AlertPickerDialog(
                    contacts = savedFriends,
                    onDismiss = { showAlertPicker = false },
                    onSend = { chosen ->
                        showAlertPicker = false
                        if (chosen.isNotEmpty()) onSendAlert(chosen)
                    }
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Alert Picker Dialog ──────────────────────────────────────────────────────
@Composable
fun AlertPickerDialog(
    contacts: List<SavedFriend>,
    onDismiss: () -> Unit,
    onSend: (List<String>) -> Unit
) {
    var selected by remember { mutableStateOf(setOf<String>()) }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(Modifier
            .fillMaxSize()
            .background(DarkBg)) {
            Column(Modifier.fillMaxSize()) {

                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorderLight)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = TextOnDark
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Send Alert",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = TextOnDark
                                )
                                Text(
                                    if (selected.isEmpty()) "Select who to alert"
                                    else "${selected.size} selected",
                                    fontSize = 12.sp,
                                    color = if (selected.isEmpty()) TextOnDarkMuted else Color(
                                        0xFFFF4757
                                    )
                                )
                            }
                        }
                    }
                }

                // Contact list
                if (contacts.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("No contacts added yet", color = TextOnDarkMuted, fontSize = 15.sp)
                    }
                } else {
                    LazyColumn(
                        Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(contacts, key = { it.trackId }) { c ->
                            val isSelected = c.trackId in selected
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) Color(0xFFFF4757).copy(alpha = 0.15f) else DarkCard)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) Color(0xFFFF4757).copy(alpha = 0.6f) else DarkBorderLight,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        selected =
                                            if (isSelected) selected - c.trackId else selected + c.trackId
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .then(
                                            if (isSelected) Modifier.background(Color(0xFFFF4757))
                                            else Modifier.background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        EmeraldDeep,
                                                        EmeraldGreen
                                                    )
                                                )
                                            )
                                        ),
                                    Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Text(
                                            c.displayName.firstOrNull()?.uppercase()
                                                ?: c.trackId.firstOrNull()?.uppercase() ?: "?",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DarkBg
                                        )
                                    }
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        c.displayName.ifBlank { c.trackId },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnDark
                                    )
                                    Text(
                                        c.trackId,
                                        fontSize = 11.sp,
                                        color = TextOnDarkMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // Send FAB
                if (selected.isNotEmpty()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(DarkSurface)
                            .padding(16.dp),
                        Alignment.Center
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFFF4757),
                                            Color(0xFFCC1C2E)
                                        )
                                    )
                                )
                                .clickable { onSend(selected.toList()) },
                            Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("🚨", fontSize = 20.sp)
                                Text(
                                    "Send Alert to ${selected.size} contact${if (selected.size > 1) "s" else ""}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Dark Friend Profile Card ─────────────────────────────────────────────────
@Composable
fun DarkFriendProfileCard(
    sf: SavedFriend, isTracking: Boolean, liveLocation: FriendLocation?,
    onViewProfile: () -> Unit, onToggleTrack: () -> Unit, onOpenChat: () -> Unit,
    slotOverride: Int = 0
) {
    val hasProfile = sf.displayName.isNotBlank()
    val isLive = liveLocation?.isRecent == true
    val speedKmh = liveLocation?.speedKmh ?: 0f
    val accentColors = listOf(
        Color(0xFF5865F2), Color(0xFF3BA55C), Color(0xFFEB459E),
        Color(0xFFF0B232), Color(0xFFED4245), Color(0xFF7983F5)
    )
    val accent = accentColors[slotOverride % accentColors.size]
    val avatarBitmap = remember(sf.avatarBase64) {
        if (sf.avatarBase64.isNotBlank()) base64ToBitmap(sf.avatarBase64) else null
    }
    val initial = sf.displayName.firstOrNull()?.toString()?.uppercase()
        ?: sf.trackId.firstOrNull()?.toString()?.uppercase() ?: "?"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (hasProfile) Modifier.clickable { onViewProfile() } else Modifier),
        shape = RoundedCornerShape(12.dp), color = DarkCard, shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLight)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Discord-style clean circle avatar
                    Box(Modifier.size(46.dp), contentAlignment = Alignment.Center) {
                        Box(
                            Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    if (avatarBitmap != null) DarkCardAlt else accent.copy(
                                        0.85f
                                    )
                                ),
                            Alignment.Center
                        ) {
                            if (avatarBitmap != null) {
                                androidx.compose.foundation.Image(
                                    bitmap = avatarBitmap.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
                                Text(
                                    initial,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        if (isLive || isTracking) {
                            Box(
                                Modifier
                                    .size(13.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(if (isLive) GreenOnline else AmberWarning)
                                    .border(2.dp, DarkCard, CircleShape)
                            )
                        }
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(Modifier.weight(1f)) {
                        if (hasProfile) {
                            Text(
                                sf.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextOnDark,
                                maxLines = 1
                            )
                            Spacer(Modifier.height(1.dp))
                            Text(
                                sf.email.ifBlank { "No email" },
                                fontSize = 11.sp,
                                color = TextOnDarkMuted,
                                maxLines = 1
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            sf.trackId,
                            fontSize = if (hasProfile) 10.sp else 15.sp,
                            color = accent.copy(if (hasProfile) 0.8f else 1f),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    when {
                                        isLive -> GreenOnline.copy(0.12f); isTracking -> AmberWarning.copy(
                                        0.10f
                                    ); else -> DarkCardAlt
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isLive -> GreenOnline; isTracking -> AmberWarning; else -> TextOnDarkMuted
                                        }
                                    )
                            )
                            Text(
                                when {
                                    isLive && speedKmh > 0.5f -> "Live  •  %.0f km/h".format(
                                        speedKmh
                                    )

                                    isLive -> "Live now"
                                    isTracking -> "Tracking"
                                    else -> "Not tracked"
                                },
                                fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                color = when {
                                    isLive -> GreenOnline; isTracking -> AmberWarning; else -> TextOnDarkMuted
                                }
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isTracking)
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFCC1C2E),
                                            Color(0xFFFF4757)
                                        )
                                    )
                                else
                                    Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen))
                            )
                            .clickable { onToggleTrack() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            if (isTracking) "Stop" else "Track",
                            fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White
                        )
                    }
                }

                if (hasProfile) {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        accent.copy(0.18f),
                                        accent.copy(0.08f)
                                    )
                                )
                            )
                            .border(1.dp, accent.copy(0.3f), RoundedCornerShape(14.dp))
                            .clickable { onOpenChat() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Icon(
                                Icons.Default.Chat,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                "Message ${sf.displayName.split(" ").first()}",
                                fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = accent
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── User Profile Screen ──────────────────────────────────────────────────────
@Composable
fun UserProfileScreen(
    profile: SavedFriend, isTracking: Boolean, liveLocation: FriendLocation?,
    isAlreadySaved: Boolean, canTrack: Boolean,
    onBack: () -> Unit, onToggleTrack: (String) -> Unit,
    onAddFriend: (SavedFriend) -> Unit, onRemoveFriend: (String) -> Unit,
    onOpenChat: (SavedFriend) -> Unit
) {
    val ctx = LocalContext.current
    val isLive = liveLocation?.isRecent == true
    val myBitmap = remember(profile.avatarBase64) { base64ToBitmap(profile.avatarBase64) }
    var trackIdCopied by remember { mutableStateOf(false) }

    Box(Modifier
        .fillMaxSize()
        .background(DarkBg)) {
        Column(Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())) {
            // Profile card with cover banner (React profiles-view.tsx style)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLight)
            ) {
                Column {
                    // Cover banner with back button
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen)))
                    ) {
                        Box(
                            Modifier
                                .padding(top = 12.dp, start = 12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(0.3f))
                                .clickable { onBack() },
                            Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    // Profile content overlapping banner
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .offset(y = (-36).dp)
                            .padding(horizontal = 20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            // Discord-style round avatar overlapping banner
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(DarkSurface)
                                        .border(4.dp, DarkSurface, CircleShape)
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(EmeraldGreen),
                                        Alignment.Center
                                    ) {
                                        if (myBitmap != null) {
                                            androidx.compose.foundation.Image(
                                                bitmap = myBitmap.asImageBitmap(),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape)
                                            )
                                        } else {
                                            Text(
                                                profile.displayName.firstOrNull()?.toString()
                                                    ?.uppercase() ?: "?",
                                                fontSize = 30.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                                if (isLive) Box(
                                    Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(GreenOnline)
                                        .border(3.dp, DarkSurface, CircleShape)
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier
                                .weight(1f)
                                .padding(bottom = 4.dp)) {
                                Text(
                                    profile.displayName.ifBlank { "Unknown" },
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextOnDark
                                )
                                if (profile.email.isNotBlank()) {
                                    Spacer(Modifier.height(2.dp))
                                    Text(profile.email, fontSize = 12.sp, color = TextOnDarkMuted)
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        // Track ID pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldGreen.copy(0.08f))
                                .border(1.dp, EmeraldGreen.copy(0.2f), RoundedCornerShape(12.dp))
                                .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                profile.trackId,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(if (trackIdCopied) EmeraldGreen.copy(0.25f) else DarkCardAlt)
                                    .clickable {
                                        val cm =
                                            ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        cm.setPrimaryClip(
                                            android.content.ClipData.newPlainText(
                                                "Track ID",
                                                profile.trackId
                                            )
                                        )
                                        trackIdCopied = true
                                    },
                                Alignment.Center
                            ) {
                                Icon(
                                    if (trackIdCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy Track ID",
                                    tint = if (trackIdCopied) EmeraldGreen else TextOnDarkMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .then(
                                if (isTracking) Modifier.background(
                                    RedRecord.copy(alpha = 0.15f),
                                    RoundedCornerShape(14.dp)
                                )
                                else Modifier.background(
                                    Brush.linearGradient(
                                        listOf(
                                            EmeraldDeep,
                                            EmeraldGreen
                                        )
                                    ), RoundedCornerShape(14.dp)
                                )
                            )
                            .border(
                                1.dp,
                                if (isTracking) RedRecord.copy(alpha = 0.4f) else Color.Transparent,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable(enabled = canTrack) { onToggleTrack(profile.trackId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                if (isTracking) Icons.Default.Stop else Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isTracking) RedRecord else DarkBg,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                if (isTracking) "Untrack" else "Track",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = if (isTracking) RedRecord else DarkBg
                            )
                        }
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isAlreadySaved) DarkCard else EmeraldGreen.copy(alpha = 0.15f))
                            .border(
                                1.dp,
                                if (isAlreadySaved) DarkBorderLight else EmeraldGreen.copy(alpha = 0.4f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                if (isAlreadySaved) onRemoveFriend(profile.trackId) else onAddFriend(
                                    profile
                                )
                            }, contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                if (isAlreadySaved) Icons.Default.Check else Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = if (isAlreadySaved) TextOnDarkMuted else EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                if (isAlreadySaved) "In Contacts" else "Add Friend",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = if (isAlreadySaved) TextOnDarkMuted else EmeraldGreen
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(EmeraldGreen.copy(alpha = 0.10f))
                        .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .clickable { onOpenChat(profile) }, contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "Message ${
                                profile.displayName.split(" ").first().ifBlank { "Friend" }
                            }",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = EmeraldGreen
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Surface(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLight)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isLive -> EmeraldGreen.copy(0.12f); isTracking -> AmberWarning.copy(
                                            0.12f
                                        ); else -> DarkCardAlt
                                        }
                                    ), Alignment.Center
                            ) {
                                Box(
                                    Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isLive -> GreenOnline; isTracking -> AmberWarning; else -> TextOnDarkMuted
                                            }
                                        )
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    when {
                                        isLive -> "Live Now"; isTracking -> "Being Tracked"; else -> "Not Tracked"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        isLive -> GreenOnline; isTracking -> AmberWarning; else -> TextOnDarkMuted
                                    }
                                )
                                Text(
                                    when {
                                        isLive -> "Location updating in real-time"; isTracking -> "Last known location available"; else -> "Add to tracking to see location"
                                    }, fontSize = 11.sp, color = TextOnDarkMuted
                                )
                            }
                        }
                        if (profile.email.isNotBlank()) {
                            Spacer(Modifier.height(16.dp)); Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(DarkBorderLight)
                            ); Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(DarkCardAlt),
                                    Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = null,
                                        tint = TextOnDarkMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "EMAIL",
                                        fontSize = 10.sp,
                                        color = TextOnDarkMuted,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    ); Text(profile.email, fontSize = 13.sp, color = TextOnDark)
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp)); Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DarkBorderLight)
                    ); Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DarkCardAlt),
                                Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = TextOnDarkMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "TRACK ID",
                                    fontSize = 10.sp,
                                    color = TextOnDarkMuted,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    profile.trackId,
                                    fontSize = 13.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Box(
                                Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (trackIdCopied) EmeraldGreen.copy(0.2f) else DarkCardAlt)
                                    .clickable {
                                        val cm =
                                            ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        cm.setPrimaryClip(
                                            android.content.ClipData.newPlainText(
                                                "Track ID",
                                                profile.trackId
                                            )
                                        )
                                        trackIdCopied = true
                                    },
                                Alignment.Center
                            ) {
                                Icon(
                                    if (trackIdCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = if (trackIdCopied) EmeraldGreen else TextOnDark,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
                if (isTracking && liveLocation != null) {
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = DarkCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isLive) EmeraldGreen.copy(0.3f) else DarkBorderLight
                        )
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            DarkSectionLabel("LIVE LOCATION"); Spacer(Modifier.height(14.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DarkMiniStat(
                                    "LAT",
                                    "%.5f°".format(liveLocation.point.latitude()),
                                    Modifier.weight(1f)
                                )
                                DarkMiniStat(
                                    "LNG",
                                    "%.5f°".format(liveLocation.point.longitude()),
                                    Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DarkMiniStat(
                                    "SPEED",
                                    if (liveLocation.speedKmh < 0.5f) "0.0 km/h" else "%.1f km/h".format(
                                        liveLocation.speedKmh
                                    ),
                                    Modifier.weight(1f)
                                )
                                DarkMiniStat(
                                    "ACCURACY",
                                    "±${liveLocation.accuracy.toInt()} m",
                                    Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ─── Friend Dialog ────────────────────────────────────────────────────────────
@Composable
fun FriendDialog(
    currentIds: List<String>, savedFriends: List<SavedFriend>, myUid: String, myTrackId: String,
    onDismiss: () -> Unit, onTrackNow: (SavedFriend) -> Unit,
    onAddAndTrack: (SavedFriend) -> Unit, onViewProfile: (SavedFriend) -> Unit
) {
    var idInput by remember { mutableStateOf("") }
    var lookupResult by remember { mutableStateOf<SavedFriend?>(null) }
    var lookupState by remember { mutableStateOf("idle") }
    var lookupRequestId by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()
    val kb = LocalSoftwareKeyboardController.current

    val idNorm = idInput.trim().uppercase()
    val alreadyTracked = idNorm in currentIds
    val slotsLeft = MAX_FRIENDS - currentIds.size
    val canTrackById =
        idNorm.isNotBlank() && !alreadyTracked && slotsLeft > 0 && lookupState == "found" && lookupResult != null

    fun doIdLookup() {
        if (idNorm.isBlank()) return
        val requestedId = idNorm; lookupRequestId += 1;
        val requestId = lookupRequestId
        lookupState = "loading"; lookupResult = null
        scope.launch(Dispatchers.IO) {
            android.util.Log.d("FriendDialog", "Looking up: $requestedId")
            val result = httpGet("/api/user/by-trackid/$requestedId")
            android.util.Log.d("FriendDialog", "Result: $result")
            withContext(Dispatchers.Main) {
                android.util.Log.d("FriendDialog",
                    "requestId=$requestId, lookupRequestId=$lookupRequestId, " +
                    "idInput=${idInput.trim().uppercase()}, requested=$requestedId")
                if (requestId != lookupRequestId || idInput.trim()
                        .uppercase() != requestedId
                ) {
                    android.util.Log.d("FriendDialog", "Result discarded - stale request")
                    return@withContext
                }
                if (result != null && result.has("trackId")) {
                    val resolvedTrackId = result.getString("trackId")
                    android.util.Log.d("FriendDialog", "Resolved trackId: $resolvedTrackId, myTrackId: $myTrackId")
                    if (resolvedTrackId == myTrackId) {
                        android.util.Log.d("FriendDialog", "Self-lookup blocked")
                        lookupState = "notfound"
                        return@withContext
                    }
                    lookupResult = SavedFriend(
                        trackId = resolvedTrackId,
                        displayName = result.optString("displayName", "")
                            .ifBlank { resolvedTrackId },
                        email = result.optString("email", ""),
                        avatarBase64 = result.optString("avatarBase64", "")
                    )
                    lookupState = "found"
                    android.util.Log.d("FriendDialog", "Lookup successful: ${lookupResult?.displayName}")
                } else {
                    android.util.Log.d("FriendDialog", "Lookup failed - result null or missing trackId")
                    lookupState = "notfound"
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp), color = DarkCard, modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLight)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    "Add Contact",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = TextOnDark
                )
                Text(
                    "Enter a Track ID to find a contact",
                    fontSize = 13.sp,
                    color = TextOnDarkMuted
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = idInput,
                        onValueChange = {
                            idInput = it.uppercase(); lookupRequestId += 1; lookupState =
                            "idle"; lookupResult = null
                        },
                        placeholder = { Text("TRK-XXXXXXXX", color = TextOnDarkMuted) },
                        isError = alreadyTracked,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = DarkBorderLight,
                            focusedContainerColor = DarkCardAlt,
                            unfocusedContainerColor = DarkCardAlt,
                            focusedTextColor = TextOnDark,
                            unfocusedTextColor = TextOnDark,
                            cursorColor = EmeraldGreen
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { kb?.hide(); doIdLookup() })
                    )
                    Box(
                        Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(listOf(EmeraldDeep, EmeraldGreen)))
                            .clickable(enabled = idNorm.isNotBlank() && lookupState != "loading") { kb?.hide(); doIdLookup() },
                        Alignment.Center
                    ) {
                        if (lookupState == "loading") CircularProgressIndicator(
                            Modifier.size(20.dp),
                            color = DarkBg,
                            strokeWidth = 2.dp
                        )
                        else Text("🔍", fontSize = 18.sp)
                    }
                }
                if (lookupState == "found" && lookupResult != null) {
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = DarkCardAlt,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            EmeraldGreen.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarCircle(
                                base64 = lookupResult!!.avatarBase64,
                                fallbackInitial = lookupResult!!.displayName.firstOrNull()
                                    ?.toString()?.uppercase() ?: "?",
                                size = 44, isTracking = true
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    lookupResult!!.displayName.ifBlank { "Unknown" },
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = TextOnDark
                                )
                                Text(
                                    lookupResult!!.trackId,
                                    fontSize = 10.sp,
                                    color = EmeraldGreen,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (lookupResult!!.email.isNotBlank()) {
                                    Text(
                                        lookupResult!!.email,
                                        fontSize = 10.sp,
                                        color = TextOnDarkMuted
                                    )
                                }
                            }
                        }
                    }
                }
                if (lookupState == "notfound") {
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = AmberWarning.copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            AmberWarning.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(Modifier.padding(12.dp)) {
                            Text(
                                "⚠ No account found for this Track ID",
                                fontSize = 12.sp,
                                color = AmberWarning,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkCardAlt)
                            .border(1.dp, DarkBorderLight, RoundedCornerShape(14.dp))
                            .clickable { onDismiss() }, Alignment.Center
                    ) {
                        Text("Cancel", color = TextOnDarkMuted, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (canTrackById) Brush.linearGradient(
                                    listOf(
                                        EmeraldDeep,
                                        EmeraldGreen
                                    )
                                ) else Brush.linearGradient(listOf(DarkCardAlt, DarkCardAlt))
                            )
                            .clickable(enabled = canTrackById) { lookupResult?.let { onTrackNow(it) } },
                        Alignment.Center
                    ) {
                        Text(
                            if (lookupState == "found") "Add Friend" else "Search First",
                            fontWeight = FontWeight.ExtraBold,
                            color = if (canTrackById) DarkBg else TextOnDarkMuted
                        )
                    }
                }
            }
        }
    }
}

// ─── Permission Screen ────────────────────────────────────────────────────────
@Composable
fun PermScreen(onRequest: () -> Unit) {
    Box(Modifier
        .fillMaxSize()
        .background(DarkBg), Alignment.Center) {
        Box(
            Modifier
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            EmeraldGreen.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ), CircleShape
                )
        )
        Surface(
            modifier = Modifier.padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLight)
        ) {
            Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(listOf(EmeraldDeep, NeonGreen))),
                    Alignment.Center
                ) { Text("📍", fontSize = 40.sp) }
                Spacer(Modifier.height(24.dp))
                Text(
                    "Location Access",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = TextOnDark,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Grant location access to see your position and share with friends.",
                    fontSize = 14.sp,
                    color = TextOnDarkMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                Spacer(Modifier.height(36.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.linearGradient(listOf(EmeraldDeep, NeonGreen)))
                        .clickable { onRequest() }, Alignment.Center
                ) {
                    Text(
                        "Grant Permission",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Reusable dark UI components ──────────────────────────────────────────────
@Composable
fun DarkSectionLabel(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextOnDarkMuted,
        letterSpacing = 1.2.sp
    )
}

@Composable
fun DarkMiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier,
        shape = RoundedCornerShape(12.dp),
        color = DarkCardAlt,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLight)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                label,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = TextOnDarkMuted
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextOnDark,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun SectionLabel(text: String) = DarkSectionLabel(text)
@Composable
fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) =
    DarkMiniStat(label, value, modifier)

// ─── FriendCard legacy ────────────────────────────────────────────────────────
@Composable
fun FriendCard(
    id: String,
    slot: Int,
    color: Color,
    label: String,
    loc: FriendLocation?,
    savedName: String? = null,
    onRemove: () -> Unit
) {
    Surface(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = DarkCard,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (loc?.isRecent == true) color.copy(alpha = 0.40f) else DarkBorderLight
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    Alignment.Center
                ) {
                    Text(
                        savedName?.firstOrNull()?.toString()?.uppercase() ?: "${slot + 1}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = color
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    if (!savedName.isNullOrBlank()) {
                        Text(
                            savedName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextOnDark
                        ); Spacer(Modifier.height(1.dp))
                    }
                    Text(
                        id,
                        fontSize = if (savedName.isNullOrBlank()) 14.sp else 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (savedName.isNullOrBlank()) TextOnDark else TextOnDarkMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DarkCardAlt)
                        .clickable { onRemove() }, Alignment.Center
                ) {
                    Text(
                        "✕",
                        fontSize = 13.sp,
                        color = TextOnDarkMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SavedFriendsSection(
    savedFriends: List<SavedFriend>,
    trackedIds: List<String>,
    onToggleTrack: (String) -> Unit,
    onRemoveSavedFriend: (String) -> Unit,
    onViewProfile: (SavedFriend) -> Unit,
    onOpenChatWith: (SavedFriend) -> Unit
) {
    savedFriends.forEach { sf ->
        DarkFriendProfileCard(
            sf = sf,
            isTracking = sf.trackId in trackedIds,
            liveLocation = null,
            onViewProfile = { onViewProfile(sf) },
            onToggleTrack = { onToggleTrack(sf.trackId) },
            onOpenChat = { onOpenChatWith(sf) })
        Spacer(Modifier.height(10.dp))
    }
}

// ─── GPS ──────────────────────────────────────────────────────────────────────
@SuppressLint("MissingPermission")
fun startGps(
    fused: FusedLocationProviderClient,
    onCb: (LocationCallback) -> Unit,
    onAvailability: (Boolean) -> Unit,
    onLoc: (Point, Float, Float, Float, Double, Float, Float, Float, String) -> Unit,
    context: Context,
    accelLat: Double = 0.0,
    accelLng: Double = 0.0
) {
    val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 200L) // ← 200ms, not 100ms
        .setMinUpdateIntervalMillis(100L)
        .setWaitForAccurateLocation(false)
        .build()

    // Low-pass filter state
    var filteredLat = Double.NaN
    var filteredLng = Double.NaN
    var filteredAcc = 0f
    var filteredSpd = 0f
    val ALPHA_MOVING = 0.55f   // higher = more responsive when moving
    val ALPHA_STILL  = 0.15f   // lower  = more smoothing when stationary

    var lastPoint: Point? = null

    val cb = object : LocationCallback() {
        override fun onLocationAvailability(availability: LocationAvailability) {
            onAvailability(availability.isLocationAvailable)
        }

        override fun onLocationResult(result: LocationResult) {
            val loc = result.lastLocation ?: return
            val acc = loc.accuracy
            if (acc > 150f) return

            val rawLat = loc.latitude
            val rawLng = loc.longitude
            val spdMs  = if (loc.hasSpeed() && loc.speed >= 0) loc.speed else 0f
            val heading = if (loc.hasBearing()) loc.bearing else 0f

            // Choose alpha: be more responsive when moving
            val alpha = if (spdMs > 0.5f) ALPHA_MOVING else ALPHA_STILL

            // First fix — seed directly, no filter
            if (filteredLat.isNaN()) {
                filteredLat = rawLat; filteredLng = rawLng
                filteredAcc = acc;    filteredSpd = spdMs
            } else {
                filteredLat = filteredLat + alpha * (rawLat - filteredLat)
                filteredLng = filteredLng + alpha * (rawLng - filteredLng)
                filteredAcc = filteredAcc + alpha * (acc - filteredAcc)
                filteredSpd = filteredSpd + alpha * (spdMs - filteredSpd)
            }

            val pt = Point.fromLngLat(filteredLng, filteredLat)

            // Only emit if moved > 1m OR speed > 0.3 m/s
            val prev = lastPoint
            if (prev != null && distM(prev, pt) < 1.0 && filteredSpd < 0.3f) return

            lastPoint = pt
            onLoc(
                pt, filteredAcc, filteredSpd, heading,
                0.0, 0f, 0f, 0f,
                loc.provider ?: "gps"
            )
        }
    }
    onCb(cb)
    fused.requestLocationUpdates(req, cb, Looper.getMainLooper())
}

// ─── Geocoder ─────────────────────────────────────────────────────────────────
fun geocode(ctx: Context, lat: Double, lon: Double): String = try {
    val list = Geocoder(ctx, Locale.getDefault()).getFromLocation(lat, lon, 1)
    if (!list.isNullOrEmpty()) {
        val a = list[0]
        listOfNotNull(
            a.subLocality ?: a.thoroughfare,
            a.locality ?: a.subAdminArea,
            a.adminArea,
            a.countryName
        ).joinToString(", ")
    } else "Address not found"
} catch (_: Exception) {
    "Address unavailable"
}

// ─── MyFriendsScreen ─────────────────────────────────────────────────────────
@Composable
fun MyFriendsScreen(
    savedFriends: List<SavedFriend>,
    trackedIds: List<String>,
    friendLocations: Map<String, FriendLocation?>,
    onBack: () -> Unit,
    onViewProfile: (SavedFriend) -> Unit,
    onToggleTrack: (String) -> Unit,
    onRemoveSavedFriend: (String) -> Unit,
    onOpenChat: (SavedFriend) -> Unit
) {
    Box(Modifier
        .fillMaxSize()
        .background(DarkBg)) {
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .statusBarsPadding()
                    .padding(bottom = 20.dp, start = 20.dp, end = 20.dp, top = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkCard)
                            .clickable { onBack() }, Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "My Friends",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = TextOnDark
                        )
                        Text(
                            "${savedFriends.size} contacts",
                            fontSize = 12.sp,
                            color = TextOnDarkMuted
                        )
                    }
                }
            }
            Column(Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)) {
                savedFriends.forEach { sf ->
                    DarkFriendProfileCard(
                        sf = sf,
                        isTracking = sf.trackId in trackedIds,
                        liveLocation = friendLocations[sf.trackId],
                        onViewProfile = { onViewProfile(sf) },
                        onToggleTrack = { onToggleTrack(sf.trackId) },
                        onOpenChat = { onOpenChat(sf) })
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
