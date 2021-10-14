package com.axel.makecall

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Make call from app intent"

        val phone = findViewById<Button>(R.id.caller)

        phone.setOnClickListener {
            call(it)
        }

        val mail = findViewById<Button>(R.id.sender)
        mail.setOnClickListener {
            email(it)
        }
        val geolocation = findViewById<Button>(R.id.local)
        geolocation.setOnClickListener {
            location(it)
        }
        val web = findViewById<Button>(R.id.website)
        web.setOnClickListener {
            webs(it)
        }
    }
    private fun call (view:View){
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:"+"254712345674")
        startActivity(dialIntent)
    }
    private fun email(view:View){
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("Email")
        val str = arrayOf("info@lctafrica.net","info@lctafrica.net")
        emailIntent.putExtra(Intent.EXTRA_EMAIL,str)
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject") // shows subject on subject field
//        emailIntent.putExtra(Intent.EXTRA_TEXT,"Body") // shows body on body field
        emailIntent.type = "message/rfc882"
        startActivity(emailIntent)
    }

    private fun location(view:View){
        val locationIntent = Intent(Intent.ACTION_VIEW)
        locationIntent.data = Uri.parse("geo:-1.2870562350750983,36.80844737171941")
        startActivity(locationIntent)
    }

    private fun webs(view:View) {
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse("https://www.lctafrica.net")
        startActivity(browserIntent)
    }
}