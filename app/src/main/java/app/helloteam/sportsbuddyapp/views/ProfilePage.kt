package app.helloteam.sportsbuddyapp.views

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.parse.UserHandling
import app.helloteam.sportsbuddyapp.databinding.ActivityProfilePageBinding
import com.parse.ParseUser

class ProfilePage : AppCompatActivity() {

    private lateinit var binding: ActivityProfilePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        ParseUser.getCurrentUser().fetch()
        binding.userNameEdit.text= ParseUser.getCurrentUser().username.toString()
        binding.dateText.text=ParseUser.getCurrentUser().createdAt.toString()
        if (ParseUser.getCurrentUser().getString("aboutMe")!=null) {
            binding.aboutMeText.text = ParseUser.getCurrentUser().getString("aboutMe").toString()
        }
        if (ParseUser.getCurrentUser().getString("favouriteSport")!=null) {
            binding.favSportText.text = ParseUser.getCurrentUser().getString("favouriteSport").toString()
        }


    }
    fun editProfile(view: View) {
        finish()
        ParseUser.getCurrentUser().fetch()
        val intent = Intent(this, EditProfilePage::class.java)
        startActivity(intent)
    }

    fun afterLogout() {//method to go back to login screen after logout
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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
        R.id.action_logout -> {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Do you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, id ->
                    UserHandling.Logout()
                    afterLogout()
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