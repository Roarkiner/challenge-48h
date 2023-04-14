package com.example.challenge48h

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private var coffeeMachineStatus: Boolean = true
    private var isActivityActive: Boolean = true
    private lateinit var webSocket: WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request = Request.Builder()
            .url("ws://144.24.199.103:8999")
            .build()

        val listener = MachineWebSocketListener(this)
        val client = OkHttpClient()
        webSocket = client.newWebSocket(request, listener)

        val mainStatusButton = findViewById<Button>(R.id.change_status_button)

        mainStatusButton.setOnClickListener {
            if(coffeeMachineStatus)
                webSocket.send("{data: 'down'}")
            else
                webSocket.send("{data: 'up'}")
        }

        requestNotificationPermission()
        setLayoutStyleForCurrentStatus()
        createNotificationChannel()
    }

    private fun setLayoutStyleForCurrentStatus(){
        val mainContainer = findViewById<LinearLayout>(R.id.main_page_container)
        val mainIcon = findViewById<ImageView>(R.id.main_page_icon)
        val mainStatusText = findViewById<TextView>(R.id.main_page_status_text)
        val mainStatusButton = findViewById<Button>(R.id.change_status_button)

        startRotationAnimation(mainIcon)

        if(coffeeMachineStatus){
            mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            mainIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coffee))
            mainStatusText.text = "La machine est opérationnelle"
            mainStatusButton.text = "Hors service"
            mainStatusButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
        } else {
            mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            mainIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coffee_invalid))
            mainStatusText.text = "La machine est en panne"
            mainStatusButton.text = "Refonctionne"
            mainStatusButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
        }
    }

    private fun showNotification(){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        var notifIcon = R.drawable.ic_coffee_invalid
        var notifDesc = "La machine a café est en panne"
        if(coffeeMachineStatus){
            notifIcon = R.drawable.ic_coffee
            notifDesc = "La machine a café est de nouveau opérationnelle"
        }

        val date = Date()
        val notificationId = SimpleDateFormat("ddhhmmss", Locale.FRANCE).format(date).toInt()
        val notificationBuilder = NotificationCompat.Builder(this, "channel01")
            .setSmallIcon(notifIcon)
            .setContentTitle("Le statut de la machine à café a changé")
            .setContentText(notifDesc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel(){
        val name: CharSequence = "CoffeeMachine"
        val description = "Coffee machine notifications channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel("channel01", name, importance)
        notificationChannel.description = description
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun startRotationAnimation(imageView: ImageView) {
        val rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.spin_center_animation)
        imageView.animation = rotationAnimation
    }

    fun onNewStatusReceived(newStatus: Boolean){
        coffeeMachineStatus = newStatus
        if(isActivityActive){
            runOnUiThread {
                setLayoutStyleForCurrentStatus()
            }
        } else {
            runOnUiThread {
                showNotification()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityActive = true
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    override fun onPause() {
        super.onPause()
        isActivityActive = false
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (!notificationManager.areNotificationsEnabled()) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
            }
        } else {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
            }
        }
    }
}