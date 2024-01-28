package com.example.AproManager.kotlinCode.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.AproManager.databinding.ActivityIntroBinding



class IntroActivity : AppCompatActivity() {

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    private lateinit var binding:ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        binding=ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // This is used to get the file from the assets folder and set it to the title textView.
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding.tvAppNameIntro.typeface = typeface

        binding.btnSignInIntro.setOnClickListener {

            // Launch the sign in screen.
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }

        binding.btnSignUpIntro.setOnClickListener {

            // Launch the sign up screen.
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }
    }
}