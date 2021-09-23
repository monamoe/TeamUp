package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.data.SportTypes
import app.helloteam.sportsbuddyapp.databinding.ActivityEditProfilePageBinding
import app.helloteam.sportsbuddyapp.firebase.FileHandling
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

import com.google.firebase.auth.UserProfileChangeRequest

import com.google.firebase.auth.ktx.auth
import com.google.firebase.storage.ktx.storage


class EditProfilePage : AppCompatActivity() {


    lateinit var profilepic: ImageView
    lateinit var button: Button
    private val pickImage = 100
    private var imageUri: Uri? = null

    // FIREBASE MIGRATION //
    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().uid.toString()

    private var userName = ""
    private lateinit var binding: ActivityEditProfilePageBinding
    private var sport = "none"
    private var bio = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilePageBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)


        db.collection("User").document(uid)
            .get()
            .addOnSuccessListener { User ->
                val user = Firebase.auth.currentUser
                userName = User.get("userName").toString()
                val sfd = SimpleDateFormat("yyyy-MM-dd")
                var time: Timestamp = User.get("dateCreated") as Timestamp
                var dateCreated = sfd.format(Date(time.seconds*1000))
                bio = User.get("bio").toString()

                if (user?.photoUrl != null) {
                        Picasso.get()
                            .load(user.photoUrl)
                            .resize(100, 100)
                            .centerCrop()
                            .into(binding.profilepic)
                }
                db.collection("User/"+User.id+"/FriendCode").whereEqualTo("user", User.id)
                    .get()
                    .addOnSuccessListener { codes ->
                        var friendCode =""
                        for(code in codes){
                            friendCode = code.get("code").toString()
                            if (friendCode != "null") binding.friendCodeEdit.text = friendCode
                        }

                    }
                if (userName != "null") binding.userNameEdit.text = userName
                if (dateCreated != null) binding.dateText.text = dateCreated.toString()
                if (bio != "null" && bio != null && bio != "") binding.aboutMeEdit.setText(User.get("bio").toString())
                sport = User.get("favouriteSport").toString()

                binding.btnLoadPicture.setOnClickListener {
                    val gallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
                }

                // needs to be redone with drop down options
                when (sport) {
                    "None" -> binding.noneBtn.isChecked = true
                    "Soccer" -> binding.soccerBtn.isChecked = true
                    "Ball Hockey" -> binding.ballHockeyBtn.isChecked = true
                    "Basketball" -> binding.basketballBtn.isChecked = true
                    else -> {
                        binding.noneBtn.isChecked = true
                    }
                }

                binding.favSportGroup.setOnCheckedChangeListener { group, checkedId ->
                    if (checkedId == R.id.noneBtn) {
                        sport = "none"
                    }
                    if (checkedId == R.id.soccerBtn) {
                        sport = SportTypes.SOCCER.sport
                    }
                    if (checkedId == R.id.ballHockeyBtn) {
                        sport = SportTypes.BallHockey.sport
                    }
                    if (checkedId == R.id.basketballBtn) {
                        sport = SportTypes.BASKETBALL.sport
                    }
                }


            }
    }

    fun onSave(view: View) {
        if (binding.aboutMeEdit.text.toString().length <= 200) {

            finish()
            db.collection("User").document(uid).update(
                mapOf(
                    "favouriteSport" to sport,
                    "bio" to binding.aboutMeEdit.text.toString()
                )
            )
            if (imageUri != null) { //Image uploading
                FileHandling.uploadProfileImage(imageUri!!,this)
            }
        } else {
            Toast.makeText(this, "About Me section is too long", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            Picasso.get()
                .load(imageUri)
                .resize(100, 100)
                .centerCrop()
                .into(binding.profilepic)
        }
    }
}