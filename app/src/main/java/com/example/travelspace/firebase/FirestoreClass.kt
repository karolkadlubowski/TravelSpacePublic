package com.example.travelspace.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.travelspace.activities.*
import com.example.travelspace.models.Place
import com.example.travelspace.models.User
import com.example.travelspace.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

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

    fun updateUserProfileData(
        activity: EditProfileActivity,
        userHashMap: HashMap<String, Any>,
        previousPhoto: String?
    ) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data updated successfully!")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                when (activity) {
                    is EditProfileActivity -> {
                        activity.profileUpdateSuccess()
                        if (!previousPhoto.isNullOrEmpty())
                            deleteImage(previousPhoto)
                    }
                }
            }.addOnFailureListener { e ->
                when (activity) {
                    is EditProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
                Toast.makeText(activity, "Error when updating profile!", Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity: Activity, readPlacesList: Boolean = false) {
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
                        activity.updateNavigationUserDetails(loggedInUser,readPlacesList)
                    }
                    is EditProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener { e ->
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

    private fun deleteImage(url: String) {
        try {
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(url)
            sRef.delete().addOnSuccessListener {
                Log.i("DeletionAccomplished", "Picture deleted")
            }.addOnFailureListener {
                Log.i("DeletionFailed", "Picture deletion error")
            }
        } catch (e: Exception) {
            Log.e("DeleteFileError", "Error while deleting file")
        }
    }

    fun createPlace(activity: AddPlaceActivity, place: Place) {
        mFireStore.collection(Constants.PLACES)
            .document()
            .set(place, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Place created successfully.")
                Toast.makeText(activity,"Place created successfully.", Toast.LENGTH_SHORT).show()
                activity.placeCreatedSuccessfully()
            }.addOnFailureListener {exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a place.",
                    exception
                )
            }
    }


    fun getPlacesList(activity: MainActivity){
        mFireStore.collection(Constants.PLACES)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName,document.documents.toString())
                val placeList: ArrayList<Place> = ArrayList()
                for(i in document.documents){
                    val place = i.toObject(Place::class.java)!!
                    place.documentId = i.id
                    placeList.add(place)
                }

                activity.populatePlacesListToUI(placeList)
            }.addOnFailureListener { e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating places list",e)
            }
    }
}