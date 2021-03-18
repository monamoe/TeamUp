package app.helloteam.sportsbuddyapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog

class ProfilePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)
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
        R.id.action_logout  -> {
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