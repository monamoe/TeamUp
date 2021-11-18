package app.helloteam.sportsbuddyapp.views

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.databinding.ActivityProfilePageBinding
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ramotion.fluidslider.FluidSlider
import java.util.*
import java.text.SimpleDateFormat
import kotlin.Unit


class ProfilePage : AppCompatActivity() {

    // FIREBASE MIGRATION //
    private val db = Firebase.firestore
    private val uid = Firebase.auth.currentUser?.uid.toString()

    private lateinit var binding: ActivityProfilePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.title = "Your Profile"

        db.collection("User").document(uid)
            .get()
            .addOnSuccessListener { User ->
                var userName = User.get("userName")
                var bio = User.get("bio")
                var favouriteSport = User.get("favouriteSport")
                var maxDistance = User.get("distance")
                val sfd = SimpleDateFormat("yyyy-MM-dd")
                var getTime = User.get("dateCreated")
                if (getTime != null) {
                    var time: Timestamp = getTime as Timestamp
                    getTime = sfd.format(Date(time.seconds * 1000))
                }
                val user = Firebase.auth.currentUser

                if (user?.photoUrl != null) {

                    Glide.with(this).load(user.photoUrl).into(binding.profilepic)


                }
                db.collection("User/" + User.id + "/FriendCode").whereEqualTo("user", User.id)
                    .get()
                    .addOnSuccessListener { codes ->
                        var friendCode = ""
                        for (code in codes) {
                            friendCode = code.get("code").toString()
                            if (friendCode != "null") binding.friendCodeEdit.text = friendCode
                        }

                    }
                if (userName != null) binding.userNameEdit.text = userName.toString()
                if (getTime != null) binding.dateText.text = getTime.toString()
                if (bio != "null" && bio != null && bio != "") binding.aboutMeText.text =
                    bio.toString()
                if (favouriteSport != null && favouriteSport != "none") binding.favSportText.text =
                    favouriteSport.toString()
                if (maxDistance != null) binding.maxDistanceEdit.text =
                    maxDistance.toString() + "KM"
            }

        binding.copyButton.setOnClickListener {
            var clip = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clipData =
                ClipData.newPlainText("friendCode", binding.friendCodeEdit.text.toString())
            clip.setPrimaryClip(clipData)
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_profile -> {
            val intent = Intent(this, EditProfilePage::class.java)
            startActivity(intent)
            true
        }
        R.id.action_logout -> {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Do you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, id ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
            val alert = dialogBuilder.create()
            alert.setTitle("Logout")
            alert.show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}