package com.codaholic.mylight.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.cache.PrefManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId


class SplashActivity : AppCompatActivity() {
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        prefManager = PrefManager(this)
        setupSplash()
    }

    fun setupSplash() {
        val handler = Handler()
        handler.postDelayed(
            fun() {
                var run: Unit
                run {
                    if (prefManager.userId.isNotEmpty()) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }, 1000L
        ) //3000 L = 3 detik
    }
}
