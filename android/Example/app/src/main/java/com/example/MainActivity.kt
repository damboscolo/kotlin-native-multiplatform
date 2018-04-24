package com.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val platformNameTextView: TextView = findViewById(R.id.platform_name) as TextView
        platformNameTextView.setText(Main().sayHello())
    }
}
