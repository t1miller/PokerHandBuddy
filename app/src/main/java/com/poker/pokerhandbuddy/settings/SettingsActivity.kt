package com.poker.pokerhandbuddy.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.poker.pokerhandbuddy.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SettingsFragment())
                .commitNow()
        }
    }
}