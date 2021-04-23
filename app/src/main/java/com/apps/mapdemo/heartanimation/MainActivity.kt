package com.apps.mapdemo.heartanimation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.URL


class MainActivity : AppCompatActivity() {

    companion object
    {
        val MSG_SHOW = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val aa = findViewById<TextView>(R.id.member_send_good)
        val heartLayout = findViewById<HeartLayout>(R.id.heart_layout)

        aa.setOnClickListener {

            heartLayout.addFavor();

        }
    }
}