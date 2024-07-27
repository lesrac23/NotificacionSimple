package com.example.notificaciones

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notificaciones.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

const val CHANNEL_OTHERS= "others"
class MainActivity : AppCompatActivity() {
    private val bindind by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var hasNotificationPermissionGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bindind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setNotificationChannel()
        }

        bindind.apply {
            btnNotify.setOnClickListener {
                simpleNotificacion()
            }

            btnActionNotify.setOnClickListener {
                touchNotification()
            }

            btnNotifyWithBtn.setOnClickListener{
                buttonNotification()
            }


        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Error", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            Log.d("FCM_TOKEN",token)
            Toast.makeText(baseContext,"FCM token: $token", Toast.LENGTH_SHORT).show()
        })
    }


    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            20
                        )
                    }
                }
            }
        }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNotificationChannel() {
        val name = this.getString(R.string.channel_courses)
        val descriptionText = getString(R.string.courses_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            CHANNEL_OTHERS,
            name,
            importance
        ).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    private fun simpleNotificacion() {
        val notification = NotificationCompat.Builder(this, CHANNEL_OTHERS)
            .setSmallIcon(R.drawable.triforce)
            .setColor(getColor(R.color.triforce))
            .setContentTitle(getString(R.string.simple_title))
            .setContentText(getString(R.string.simple_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        //de la version menor 26 no habia canales
        //NotificationManagerCompat.from(this).notify(20, notification) es la misma que la de abajo
        NotificationManagerCompat.from(this).apply {
            notify(20, notification)
        }

    }

    @SuppressLint("MissingPermission")
    private fun touchNotification() {
        val intent =
            Intent(this, NewBeduActivity::class.java).apply { //navergar entre activities
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_OTHERS)
            .setSmallIcon(R.drawable.bedu_icon)
            .setContentTitle(getString(R.string.action_title))
            .setContentText(getString(R.string.action_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).apply { notify(20,notification) }
    }


    @SuppressLint("MissingPermission")
    private fun buttonNotification() {
        val intent =
            Intent(this, NotificationRecever::class.java).apply { //navergar entre activities
                action = NotificationRecever.ACTION_RECEIVED
            }

        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_MUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_OTHERS)
            .setSmallIcon(R.drawable.bedu_icon)
            .setContentTitle(getString(R.string.button_title))
            .setContentText(getString(R.string.action_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.bedu_icon, getString(R.string.button_text),pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).apply { notify(20,notification) }
    }



}
