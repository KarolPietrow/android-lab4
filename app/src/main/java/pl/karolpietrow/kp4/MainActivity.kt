package pl.karolpietrow.kp4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

import pl.karolpietrow.kp4.ui.theme.KP4Theme
import java.util.Locale

const val channelId = "default_channel_id"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel(this, channelId)

        setContent {
            KP4Theme {
                NotificationScreen(this)
            }
        }
    }

}

fun createNotificationChannel(context: Context, channelId: String) {
    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, "default", importance)
        channel.description = "Default notification channel"
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun sendShortNotification(context: Context, title:String, message: String, @DrawableRes selectedIconId: Int, alarmHour: Int, alarmMinute: Int) {
    val intent = Intent(context,MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_MESSAGE, "Budzik")
        putExtra(AlarmClock.EXTRA_HOUR, alarmHour)
        putExtra(AlarmClock.EXTRA_MINUTES, alarmMinute)
    }
    val alarmPendingIntent = PendingIntent.getActivity(
        context,
        0,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    val builder = NotificationCompat.Builder(context,channelId)
        .setSmallIcon(selectedIconId)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .addAction(
            0,
            "Ustaw alarm",
            alarmPendingIntent
        )
        .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(1, builder)
}

fun sendPhotoNotification(context: Context, title:String, message: String, @DrawableRes selectedIconId: Int, @DrawableRes selectedPhotoId: Int, alarmHour: Int, alarmMinute: Int) {
    val intent = Intent(context,MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val bitMap= BitmapFactory.decodeResource(context.resources, selectedPhotoId)

    val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_MESSAGE, "Budzik")
        putExtra(AlarmClock.EXTRA_HOUR, alarmHour)
        putExtra(AlarmClock.EXTRA_MINUTES, alarmMinute)
    }
    val alarmPendingIntent = PendingIntent.getActivity(
        context,
        0,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context,channelId)
        .setSmallIcon(selectedIconId)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setStyle(
            NotificationCompat.BigPictureStyle()
            .bigPicture(bitMap)
        )
        .addAction(
            0,
            "Ustaw alarm",
            alarmPendingIntent
        )
        .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(1, builder)
}

fun sendLongNotification(context: Context, title:String, shortMessage: String, @DrawableRes selectedIconId: Int, longMessage: String, alarmHour: Int, alarmMinute: Int) {
    val intent = Intent(context,MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_MESSAGE, "Budzik")
        putExtra(AlarmClock.EXTRA_HOUR, alarmHour)
        putExtra(AlarmClock.EXTRA_MINUTES, alarmMinute)
    }
    val alarmPendingIntent = PendingIntent.getActivity(
        context,
        0,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context,channelId)
        .setSmallIcon(selectedIconId)
        .setContentTitle(title)
        .setContentText(shortMessage)
        .setStyle(NotificationCompat.BigTextStyle().bigText(longMessage))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .addAction(
            0,
            "Ustaw alarm",
            alarmPendingIntent
        )
        .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(1, builder)
}

@Composable
fun NotificationScreen(context: Context) {
    val scrollState = rememberScrollState()
    var titleField by remember { mutableStateOf("")}
    var shortDescField by remember { mutableStateOf("")}
    var longDescField by remember { mutableStateOf("")}
    var permissionGranted by remember { mutableStateOf(checkNotificationPermission(context)) }
    var defaultDescOnly by remember { mutableStateOf(true) }
    var showLongDescField by remember { mutableStateOf(false) }
    var showPhotoField by remember { mutableStateOf(false) }

    var selectedHour by remember { mutableIntStateOf(7) }
    var selectedMinute by remember { mutableIntStateOf(0) }

    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            selectedHour = hour
            selectedMinute = minute
        },
        selectedHour,
        selectedMinute,
        true
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(
                context,
                "Brak zezwolenia ! Nie można wyświetlić powiadomienia.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val icons = listOf(
        R.drawable.clock1,
        R.drawable.emoji,
        R.drawable.star,
        R.drawable.clock3
        )
    var selectedIcon by remember { mutableIntStateOf(icons[0]) }

    val bigPhoto = listOf(
        R.drawable.clock800,
        R.drawable.photo2,
        R.drawable.photo3
    )
    var selectedBigPhoto by remember { mutableIntStateOf(bigPhoto[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
//        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Stwórz powiadomienie",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        TextField(
            value = titleField,
            onValueChange = { titleField = it },
            label = { Text("Tytuł powiadomienia") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        TextField(
            value = shortDescField,
            onValueChange = { shortDescField = it },
            label = { Text("Krótki opis powiadomienia") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        Column(
            modifier = Modifier
                .padding(5.dp)
        ) {
            Text(
                text = "Styl powiadomienia",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(10.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .selectable(
                        selected = defaultDescOnly,
                        onClick = {
                            defaultDescOnly = true
                            showLongDescField = false
                            showPhotoField = false
                        },
                        role = Role.RadioButton
                    )
            ) {
                RadioButton(
                    selected = defaultDescOnly,
                    onClick = {
                        defaultDescOnly = true
                        showLongDescField = false
                        showPhotoField = false
                    }
                )
                Text(text = "Domyślny")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .selectable(
                        selected = showLongDescField,
                        onClick = {
                            defaultDescOnly = false
                            showLongDescField = true
                            showPhotoField = false
                        },
                        role = Role.RadioButton
                    )
            ) {
                RadioButton(
                    selected = showLongDescField,
                    onClick = {
                        defaultDescOnly = false
                        showLongDescField = true
                        showPhotoField = false
                    },
                )
                Text(text = "Długi opis (BigText)")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .selectable(
                        selected = showPhotoField,
                        onClick = {
                            defaultDescOnly = false
                            showLongDescField = false
                            showPhotoField = true
                        },
                        role = Role.RadioButton
                    )
            ) {
                RadioButton(
                    selected = showPhotoField,
                    onClick = {
                        defaultDescOnly = false
                        showLongDescField = false
                        showPhotoField = true
                    },
                )
                Text(text = "Duży obrazek (BigPhoto)")
            }
            if (showLongDescField) {
                TextField(
                    value = longDescField,
                    onValueChange = { longDescField = it },
                    label = { Text("Rozszerzony opis powiadomienia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
            }
            if (showPhotoField) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color(0xFFE3E3E3))
                ) {
                    Text(
                        text = "Duży obrazek (BigPhoto)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(10.dp)
                            .background(Color(0xFFE3E3E3))
                    )
                    bigPhoto.forEach { photoEl ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE3E3E3))
                                .selectable(
                                    selected = selectedBigPhoto == photoEl,
                                    onClick = { selectedBigPhoto = photoEl },
                                    role = Role.RadioButton
                                )
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedBigPhoto == photoEl,
                                onClick = { selectedBigPhoto = photoEl }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = photoEl),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color(0xFFE3E3E3))
            ) {
                Text(
                    text = "Ikona powiadomienia",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(10.dp)
                        .background(Color(0xFFE3E3E3))
                )
                icons.forEach { iconEl ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE3E3E3))
                            .selectable(
                                selected = selectedIcon == iconEl,
                                onClick = { selectedIcon = iconEl },
                                role = Role.RadioButton
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedIcon == iconEl,
                            onClick = { selectedIcon = iconEl }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = iconEl),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
        Button(
            onClick = {
                timePickerDialog.show()
            }, modifier = Modifier.fillMaxWidth()) {
            Text("Wybierz czas alarmu")
        }
        Text(
            text = String.format(Locale.getDefault(), "Wybrana czas alarmu: %d:%02d", selectedHour, selectedMinute),
            fontSize = 15.sp,
            modifier = Modifier
                .padding(10.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green,
                contentColor = Color.Black
            ),
            onClick = {
            if (titleField != "") {
                if (permissionGranted) {
                    if (showLongDescField) {
                        sendLongNotification(context, titleField, shortDescField, selectedIcon, longDescField, selectedHour, selectedMinute)
                    } else if (showPhotoField) {
                        sendPhotoNotification(context, titleField, shortDescField, selectedIcon, selectedBigPhoto, selectedHour, selectedMinute)
                    } else {
                        sendShortNotification(context, titleField, shortDescField, selectedIcon, selectedHour, selectedMinute)
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                Toast.makeText(context, "Tytuł powiadomienia jest wymagany", Toast.LENGTH_SHORT)
                    .show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Wyślij powiadomienie")
        }
    }
}