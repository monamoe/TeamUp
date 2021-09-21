package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import app.helloteam.sportsbuddyapp.views.LandingPageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.net.URI

object FileHandling {
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private val db = Firebase.firestore

    fun getStorageRef(): StorageReference {
        return storageRef
    }
    fun uploadProfileImage(imageUri: Uri, context: Context){
        val user = FirebaseAuth.getInstance().currentUser

                val fileRef = storageRef.child("users/${user?.uid}/ProfilePic.jpg")
                fileRef.putFile(imageUri).addOnSuccessListener {
                    Log.i("Image", "Uploaded")
                    fileRef.downloadUrl.addOnCompleteListener {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(it.result)
                            .build()

                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                db.collection("User").document(user.uid).update(
                                    mapOf(
                                        "photoUrl" to it.result.toString()
                                    )
                                )
                                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
    }
}