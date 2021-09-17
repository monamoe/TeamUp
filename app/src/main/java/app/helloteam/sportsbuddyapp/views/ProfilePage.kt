package app.helloteam.sportsbuddyapp.views

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.data.ImageStorage
import app.helloteam.sportsbuddyapp.databinding.ActivityProfilePageBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.util.*
import java.text.SimpleDateFormat


class ProfilePage : AppCompatActivity() {

    // FIREBASE MIGRATION //
    private val db = Firebase.firestore
    private val uid = Firebase.auth.currentUser?.uid.toString()

    private val pickImage = 100
    private var imageUri: Uri? = null

    private lateinit var binding: ActivityProfilePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        db.collection("User").document(uid)
            .get()
            .addOnSuccessListener { User ->
                var userName = User.get("userName")
                var bio = User.get("bio")
                var favouriteSport = User.get("favouriteSport")

                val sfd = SimpleDateFormat("yyyy-MM-dd")
                var time:Timestamp = User.get("dateCreated") as Timestamp
                var dateCreated = sfd.format(Date(time.seconds*1000))


                if (ImageStorage.checkifImageExists(
                        this,
                        "${User.get("userName").toString()}ProfilePic"
                    )
                ) {
                    val file = (ImageStorage.getImage(
                        this,
                        "${User.get("userName").toString()}ProfilePic.jpg"
                    ))
                    val mSaveBit: File = file
                    val filePath = mSaveBit.path
                    val bitmap = BitmapFactory.decodeFile(filePath)
                    binding.profilepic.setImageBitmap(bitmap)
                }
                if (userName != null) binding.userNameEdit.text = userName.toString()
                if (dateCreated != null) binding.dateText.text = dateCreated.toString()
                if (bio != null) binding.aboutMeText.text = bio.toString()
                if (favouriteSport != null && favouriteSport != "none") binding.favSportText.text = favouriteSport.toString()
            }

        findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            val intent = Intent(this, EditProfilePage::class.java)
            startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_profile -> {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
            true
        }
        R.id.action_events -> {
            val intent = Intent(this, ViewPlayerEvents::class.java)
            startActivity(intent)
            true
        }
        R.id.action_hosted -> {
            val intent = Intent(this, HostEvents::class.java)
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
        R.id.action_map -> {
            val intent = Intent(this, map::class.java)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}