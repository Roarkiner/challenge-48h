package com.example.challenge48h

import MachineWebSocketListener
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var coffeeMachineStatus: Boolean = true
    private var isActivityActive: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request = Request.Builder()
            .url("ws://192.168.78.102:8999")
            .build()

        val listener = MachineWebSocketListener(this)
        val client = OkHttpClient()
        val webSocket = client.newWebSocket(request, listener)

        setLayoutStyleForCurrentStatus()
        createNotificationChannel()
    }

    private fun setLayoutStyleForCurrentStatus(){
        val mainContainer = findViewById<LinearLayout>(R.id.main_page_container)
        val mainIcon = findViewById<ImageView>(R.id.main_page_icon)
        val mainStatusText = findViewById<TextView>(R.id.main_page_status_text)

        startRotationAnimation(mainIcon)

        if(coffeeMachineStatus){
            mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            mainIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coffee))
            mainStatusText.text = "La machine a café fonctionne"
        } else {
            mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            mainIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coffee_invalid))
            mainStatusText.text = "La machine a café ne fonctionne pas"
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

    override fun onDestroy() {
        super.onDestroy()
    }
}