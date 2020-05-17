package com.example.facear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)


        btn_singleFilter.setOnClickListener {
            val intent = Intent(this, SingleFilterActivity::class.java)
            startActivity(intent)
        }
    }
}
