package com.example.AproManager.kotlinCode.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.AproManager.R
import com.example.AproManager.databinding.ActivitySplashBinding
import com.example.AproManager.javaCode.Activities.IntroActivity
import com.example.AproManager.kotlinCode.firebase.FirebaseDatabaseClass


class SplashActivity : AppCompatActivity() {


    private lateinit var binding:ActivitySplashBinding
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)




        binding.webView.settings.javaScriptEnabled=true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.loadUrl("file:///android_asset/anim.html")

         window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )



        Handler().postDelayed({

            val currentUserID = FirebaseDatabaseClass().getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {

                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
            finish()
        }, 10)
    }
}