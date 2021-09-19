package com.example.travelspace.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import com.example.travelspace.R
import com.example.travelspace.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_sign_up.*


class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val typeFace: Typeface = Typeface.createFromAsset(assets,"Ruellia.ttf")
        tv_app_name.typeface=typeFace

        Handler(Looper.getMainLooper()).postDelayed({
            var currentUserId = FirestoreClass().getCurrentUserId()

            if (currentUserId.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                val animation1 = AnimationUtils.loadAnimation(this, R.anim.fade_out)
                ll_splash.startAnimation(animation1)
                ll_splash.visibility = View.GONE
                val animation2 = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                ll_intro.visibility = View.INVISIBLE
                ll_intro.startAnimation(animation2)
                ll_intro.visibility = View.VISIBLE
            }


        }, 2500)


        btn_sign_up_intro.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        btn_sign_in_intro.setOnClickListener{

            startActivity(Intent(this,SignInActivity::class.java))
        }
    }
}