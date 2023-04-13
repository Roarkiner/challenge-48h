package com.example.challenge48h

import android.content.Context
import androidx.core.content.ContextCompat;
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.challenge48h.manager.CoffeeApiManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val coffeeMachineManager = CoffeeApiManager()
        val coffeeMachineStatus = coffeeMachineManager.GetCoffeeMachineStatus()

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

    private fun startRotationAnimation(imageView: ImageView) {
        val rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.spin_center_animation)
        imageView.animation = rotationAnimation
    }
}