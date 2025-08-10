package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileHandling {
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val db = Firebase.firestore

    private val client = OkHttpClient()

    fun getStorageRef(): StorageReference {
        return storageRef
    }

    fun uploadProfileImage(imageUri: Uri, context: Context) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val fileRef = storageRef.child("users/${user.uid}/ProfilePic.jpg")
        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                Log.i("Image", "Uploaded")
                fileRef.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    db.collection("User").document(user.uid).update(
                                        mapOf("photoUrl" to downloadUri.toString())
                                    )
                                    Toast.makeText(context, "Profile image updated", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("Image", "Upload failed", it)
            }
    }

    fun uploadEventImage(context: Context, lat: String, long: String, locationID: String) {
        val fileRef = storageRef.child("locations/$locationID/StreetView.jpg")
        val imageUrl =
            "https://maps.googleapis.com/maps/api/streetview?size=500x400&location=$lat,$long&fov=80&heading=70&pitch=0&key=${context.getString(R.string.google_key)}"
        Log.i("ImageDownload", imageUrl)

        val directory: File = context.getDir("imageDir", Context.MODE_PRIVATE)
        val outputFile = File(directory, "streetview.png")

        val request = Request.Builder()
            .url(imageUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ImageDownload", "Download error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("ImageDownload", "Download failed with code: ${response.code}")
                    return
                }

                val sink = FileOutputStream(outputFile)
                try {
                    response.body?.byteStream()?.use { inputStream ->
                        sink.use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    // Upload the downloaded file to Firebase Storage
                    val fileUri = Uri.fromFile(outputFile)
                    fileRef.putFile(fileUri)
                        .addOnSuccessListener {
                            fileRef.downloadUrl.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    db.collection("Location").document(locationID)
                                        .update("StreetView", task.result.toString())
                                }
                                outputFile.delete()
                            }
                        }
                        .addOnFailureListener { error ->
                            Log.e("UploadEventImage", "Upload failed: ${error.message}")
                        }
                } catch (ex: Exception) {
                    Log.e("ImageDownload", "Error saving file: ${ex.message}")
                }
            }
        })
    }

    fun deleteProfilePhoto(userID: String) {
        storageRef.child("users/$userID/ProfilePic.jpg")
            .delete()
            .addOnSuccessListener {
                Log.i("DeletePhoto", "Profile photo deleted for user $userID")
            }
            .addOnFailureListener { e ->
                Log.e("DeletePhoto", "Failed to delete photo: ${e.message}")
            }
    }
}
