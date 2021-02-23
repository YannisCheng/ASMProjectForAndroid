package com.cwj.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class KotlinActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin1)
        backAct()
    }

    private fun backAct() {
        val tvBack = findViewById<TextView>(R.id.tv_back)
        tvBack.setOnClickListener {finish()}
    }
}