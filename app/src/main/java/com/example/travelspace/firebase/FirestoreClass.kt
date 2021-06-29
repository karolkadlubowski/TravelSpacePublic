package com.example.travelspace.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.travelspace.activities.EditProfileActivity
import com.example.travelspace.activities.MainActivity
import com.example.travelspace.activities.SignInActivity
import com.example.travelspace.activities.SignUpActivity
import com.example.travelspace.models.User
import com.example.travelspace.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e("RegisterUser", "Error during registering", e)

            }
    }

    fun updateUserProfileData(activity: EditProfileActivity, userHashMap: HashMap<String,Any>){

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile Data updated successfully!")
                Toast.makeText(activity, "Profile updated successfully!",Toast.LENGTH_SHORT).show()
                when(activity){
                    is EditProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                e ->
                when(activity) {
                    is EditProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
                Toast.makeText(activity, "Error when updating profile!",Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is EditProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener {
                    e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignInUser", "Error during logging.", e)
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}