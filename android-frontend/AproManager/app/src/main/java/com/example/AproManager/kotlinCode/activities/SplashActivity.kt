package com.example.AproManager.kotlinCode.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.AproManager.R
import com.example.AproManager.databinding.ActivitySplashBinding
import com.example.AproManager.javaCode.Activities.IntroActivity
import com.example.AproManager.kotlinCode.activities.MainActivity
import com.example.AproManager.kotlinCode.firebase.FirestoreClass


class SplashActivity : AppCompatActivity() {


    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

         window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding.tvAppName.typeface = typeface

        val logoAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.logoImageView.startAnimation(logoAnimation)
        binding.tvAppName.startAnimation(logoAnimation)

        Handler().postDelayed({

            val currentUserID = FirestoreClass().getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {

                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
            finish()
        }, 2000)
    }
}