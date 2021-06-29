package com.example.travelspace.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.travelspace.R
import com.example.travelspace.firebase.FirestoreClass
import com.example.travelspace.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
        setContentView(R.layout.activity_sign_in)

        auth= FirebaseAuth.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        btn_sign_in.setOnClickListener {
            signInRegisteredUser()
        }


        setupActionBar()
    }

    fun signInSuccess(user: User){
        hideProgressDialog()


        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }
/*
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
*/
    private fun signInRegisteredUser(){
        val email: String = et_email_sign_in.text.toString().trim{it<=' '}
        val password: String = et_password_sign_in.text.toString().trim{it<=' '}

        if(validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
                //hideProgressDialog()
                if(task.isSuccessful){
                    Log.d("Sign in", "signInWithEmail;success")
                    val user = auth.currentUser
                    FirestoreClass().loadUserData(this)
                }else{
                    Log.w("Sign in", "signInWithEmail:failure", task.exception)
                    hideProgressDialog()
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> {
                true
            }
        }
    }
}