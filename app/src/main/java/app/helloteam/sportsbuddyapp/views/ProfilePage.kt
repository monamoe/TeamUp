package app.helloteam.sportsbuddyapp.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class ProfilePage : AppCompatActivity() {

    // FIREBASE MIGRATION //
    private val db = Firebase.firestore
    private val uid = Firebase.auth.currentUser?.uid.toString()

    // Declare views
    private lateinit var profilePic: ImageView
    private lateinit var friendCodeEdit: TextView
    private lateinit var userNameEdit: TextView
    private lateinit var dateText: TextView
    private lateinit var aboutMeText: TextView
    private lateinit var favSportText: TextView
    private lateinit var maxDistanceEdit: TextView
    private lateinit var copyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        supportActionBar?.title = "Your Profile"

        // Initialize views using findViewById
        profilePic = findViewById(R.id.profilepic)
        friendCodeEdit = findViewById(R.id.friendCodeEdit)
        userNameEdit = findViewById(R.id.userNameEdit)
        dateText = findViewById(R.id.dateText)
        aboutMeText = findViewById(R.id.aboutMeText)
        favSportText = findViewById(R.id.favSportText)
        maxDistanceEdit = findViewById(R.id.maxDistanceEdit)
        copyButton = findViewById(R.id.copyButton)

        db.collection("User").document(uid)
            .get()
            .addOnSuccessListener { User ->
                val userName = User.get("userName")
                val bio = User.get("bio")
                val favouriteSport = User.get("favouriteSport")
                val maxDistance = User.get("distance")
                val sfd = SimpleDateFormat("yyyy-MM-dd")
                var getTime = User.get("dateCreated")
                if (getTime != null) {
                    val time: Timestamp = getTime as Timestamp
                    getTime = sfd.format(Date(time.seconds * 1000))
                }
                val user = Firebase.auth.currentUser

                if (user?.photoUrl != null) {
                    Glide.with(this).load(user.photoUrl).into(profilePic)
                }
                db.collection("User/${User.id}/FriendCode").whereEqualTo("user", User.id)
                    .get()
                    .addOnSuccessListener { codes ->
                        var friendCode = ""
                        for (code in codes) {
                            friendCode = code.get("code").toString()
                            if (friendCode != "null") friendCodeEdit.text = friendCode
                        }
                    }
                if (userName != null) userNameEdit.text = userName.toString()
                if (getTime != null) dateText.text = getTime.toString()
                if (bio != "null" && bio != null && bio != "") aboutMeText.text = bio.toString()
                if (favouriteSport != null && favouriteSport != "none") favSportText.text =
                    favouriteSport.toString()
                if (maxDistance != null) maxDistanceEdit.text = "$maxDistance KM"
            }

        copyButton.setOnClickListener {
            val clip = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("friendCode", friendCodeEdit.text.toString())
            clip.setPrimaryClip(clipData)
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_profile -> {
            startActivity(Intent(this, EditProfilePage::class.java))
            true
        }

        R.id.action_logout -> {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Do you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Logout") { dialog, id ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            val alert = dialogBuilder.create()
            alert.setTitle("Logout")
            alert.show()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
