package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import app.helloteam.sportsbuddyapp.R
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.androidnetworking.interfaces.DownloadProgressListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File


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

    fun uploadEventImage(context: Context, lat: String, long: String, locationID: String){
        val fileRef = storageRef.child("locations/${locationID}/StreetView.jpg")
        val imageurl =  "https://maps.googleapis.com/maps/api/streetview?size=500x400&location=${lat},${long}&fov=80&heading=70&pitch=0&key=${context.getString(R.string.google_key)}"
        Log.i("Imageeeee", imageurl)

        AndroidNetworking.initialize(context);
        val directory: File = context.getDir("imageDir", Context.MODE_PRIVATE)

        AndroidNetworking.download(imageurl, directory.path, "streetview.png")
            .setTag("downloadTest")
            .setPriority(Priority.MEDIUM)
            .build()
            .startDownload(object : DownloadListener {
                override fun onDownloadComplete() {
                    fileRef.putFile(Uri.fromFile(File(directory, "streetview.png"))).addOnSuccessListener {
                        fileRef.downloadUrl.addOnCompleteListener {
                            db.collection("Location").document(locationID)
                                .update("StreetView", it.result.toString())
                            File(directory, "streetview.png").delete()

                        }
                    }
                }

                override fun onError(error: ANError?) {
                    Log.i("Imageeeee", error.toString())
                }
            })
    }

    fun deleteProfilePhoto(userID: String){
       storageRef.child("users/${userID}/ProfilePic.jpg").delete()
    }

}