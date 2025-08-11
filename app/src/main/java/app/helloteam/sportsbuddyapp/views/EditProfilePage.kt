package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.data.SportTypes
import app.helloteam.sportsbuddyapp.firebase.FileHandling
import app.helloteam.sportsbuddyapp.firebase.UserHandling
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ramotion.fluidslider.FluidSlider
import java.text.SimpleDateFormat
import java.util.Date

class EditProfilePage : AppCompatActivity() {

    lateinit var profilepic: ImageView
    lateinit var btnLoadPicture: Button
    lateinit var deleteAccountButton: Button
    lateinit var aboutMeEdit: TextView
    lateinit var userNameEdit: TextView
    lateinit var dateText: TextView
    lateinit var favSportGroup: RadioGroup
    lateinit var noneBtn: RadioButton
    lateinit var soccerBtn: RadioButton
    lateinit var ballHockeyBtn: RadioButton
    lateinit var basketballBtn: RadioButton
    lateinit var fluidSlider: FluidSlider

    private val pickImage = 100
    private var imageUri: Uri? = null

    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().uid.toString()

    private var userName = ""
    private var sport = "none"
    private var bio = ""
    private var distancePosition = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_page) // Use the XML layout directly
        supportActionBar?.title = "Edit Profile"

        // Initialize views
        profilepic = findViewById(R.id.profilepic)
        btnLoadPicture = findViewById(R.id.btnLoadPicture)
        deleteAccountButton = findViewById(R.id.deleteAccountButton)
        aboutMeEdit = findViewById(R.id.aboutMeEdit)
        userNameEdit = findViewById(R.id.userNameEdit)
        dateText = findViewById(R.id.dateText)
        favSportGroup = findViewById(R.id.favSportGroup)
        noneBtn = findViewById(R.id.noneBtn)
        soccerBtn = findViewById(R.id.soccerBtn)
        ballHockeyBtn = findViewById(R.id.ballHockeyBtn)
        basketballBtn = findViewById(R.id.basketballBtn)
        fluidSlider = findViewById(R.id.fluidSlider)

        val max = 100
        val min = 10
        val total = max - min

        fluidSlider.startText = "$min km"
        fluidSlider.endText = "$max km"

        db.collection("User").document(uid)
            .get()
            .addOnSuccessListener { User ->
                val user = Firebase.auth.currentUser
                userName = User.get("userName").toString()
                val sfd = SimpleDateFormat("yyyy-MM-dd")
                val time: Timestamp = User.get("dateCreated") as Timestamp
                val dateCreated = sfd.format(Date(time.seconds * 1000))
                bio = User.get("bio").toString()
                fluidSlider.position = User.get("distance").toString().toFloat() / 100

                if (user?.photoUrl != null) {
                    Glide.with(this).load(user.photoUrl).into(profilepic)
                }
                db.collection("User/" + User.id + "/FriendCode").whereEqualTo("user", User.id)
                    .get()
                    .addOnSuccessListener { codes ->
                        for (code in codes) {
                            val friendCode = code.get("code").toString()
                            if (friendCode != "null") {
                                // You may want to display friend code somewhere; add a TextView if needed
                            }
                        }
                    }
                if (userName != "null") userNameEdit.text = userName
                dateText.text = dateCreated.toString()
                if (bio != "null" && bio.isNotEmpty()) aboutMeEdit.text = bio
                sport = User.get("favouriteSport").toString()

                btnLoadPicture.setOnClickListener {
                    val gallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
                }

                when (sport) {
                    "None" -> noneBtn.isChecked = true
                    "Soccer" -> soccerBtn.isChecked = true
                    "Ball Hockey" -> ballHockeyBtn.isChecked = true
                    "Basketball" -> basketballBtn.isChecked = true
                    else -> noneBtn.isChecked = true
                }

                favSportGroup.setOnCheckedChangeListener { _, checkedId ->
                    sport = when (checkedId) {
                        R.id.noneBtn -> "none"
                        R.id.soccerBtn -> SportTypes.SOCCER.sport
                        R.id.ballHockeyBtn -> SportTypes.BallHockey.sport
                        R.id.basketballBtn -> SportTypes.BASKETBALL.sport
                        else -> "none"
                    }
                }

                fluidSlider.positionListener = { p ->
                    fluidSlider.bubbleText = "${min + (total * p).toInt()}"
                    distancePosition = min + (total * p)
                }
            }

        deleteAccountButton.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "Are you sure you want to delete your account?")
                positiveButton(R.string.yes) { _ ->
                    UserHandling.userDelete(
                        FirebaseAuth.getInstance().currentUser,
                        this@EditProfilePage
                    )
                }
                negativeButton(R.string.cancel)
            }
        }
    }

    fun onSave(view: View) {
        if (aboutMeEdit.text.toString().length <= 200) {
            finish()
            db.collection("User").document(uid).update(
                mapOf(
                    "favouriteSport" to sport,
                    "bio" to aboutMeEdit.text.toString()
                )
            )
            if (distancePosition != 0F) {
                db.collection("User").document(uid).update(
                    mapOf(
                        "distance" to distancePosition.toInt()
                    )
                )
            }
            if (imageUri != null) { // Image uploading
                FileHandling.uploadProfileImage(imageUri!!, this)
            }
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            Toast.makeText(this, "About Me section is too long", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            Glide.with(this).load(imageUri).into(profilepic)
        }
    }

}
