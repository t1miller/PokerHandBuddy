package com.poker.pokerhandbuddy.ui.main.about

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.poker.pokerhandbuddy.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val buttonEmail = findViewById<Button>(R.id.emailButton)
        buttonEmail.setOnClickListener {
            sendEmail()
        }

        val jacks = findViewById<ImageView>(R.id.jacks)
        jacks.setOnClickListener {
            loadJacksPlaystore()
        }

        val deuces = findViewById<ImageView>(R.id.deuces)
        deuces.setOnClickListener {
            loadDeucesPlaystore()
        }

        val letItRide = findViewById<ImageView>(R.id.letItRide)
        letItRide.setOnClickListener {
            loadLetItRidePlaystore()
        }


    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "trentrobison@gmail.com", null)
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, "Wild Idea")
        intent.putExtra(Intent.EXTRA_TEXT, "Hi Trent, I have a fun feature idea.")
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }

    private fun loadJacksPlaystore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id=com.poker.jacksorbetter")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private fun loadDeucesPlaystore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id=com.poker.deuceswild")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private fun loadLetItRidePlaystore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id=com.poker.letitride")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }
}