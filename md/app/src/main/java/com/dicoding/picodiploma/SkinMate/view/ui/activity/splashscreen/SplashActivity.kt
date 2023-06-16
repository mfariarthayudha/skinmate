package com.dicoding.picodiploma.SkinMate.view.ui.activity.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.dicoding.picodiploma.SkinMate.R
import com.dicoding.picodiploma.SkinMate.view.ui.activity.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 2000
    private lateinit var gambar_loading: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        gambar_loading = findViewById(R.id.gambar)

        supportActionBar?.hide()
        setAnimation()

        //Instruksi menjalankan main screen setelah timer splash screen selesai
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT)

    }

    private fun setAnimation() {
        val animation = AnimationUtils.loadAnimation(this@SplashActivity,R.anim.top_animation)
        gambar_loading.animation = animation
    }
}