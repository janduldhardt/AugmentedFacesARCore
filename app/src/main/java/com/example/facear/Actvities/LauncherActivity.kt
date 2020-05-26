package com.example.facear.Actvities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.facear.R
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)


//        btn_singleFilter.setOnClickListener {
//            val intent = Intent(this, SingleFilterActivity::class.java)
//            startActivity(intent)
//        }

        btn_singleFilter.setOnClickListener {
            val intent = Intent(this, TestPerformanceActivity::class.java)
            startActivity(intent)
        }

        btn_customFilter.setOnClickListener {
            val intent = Intent(this, FaceFilterSelectionActivity::class.java)
            startActivity(intent)
        }
    }
}
