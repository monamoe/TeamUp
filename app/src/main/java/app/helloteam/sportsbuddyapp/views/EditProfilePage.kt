package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.data.SportTypes
import app.helloteam.sportsbuddyapp.models.User
import app.helloteam.sportsbuddyapp.databinding.ActivityEditProfilePageBinding
import app.helloteam.sportsbuddyapp.parse.ParseCode
import com.parse.ParseUser

class EditProfilePage : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfilePageBinding
    private var sport = "none"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProfilePageBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        ParseUser.getCurrentUser().fetch()

        binding.userNameEdit.text= ParseUser.getCurrentUser().username
        binding.dateText.text= ParseUser.getCurrentUser().createdAt.toString()

        if (ParseUser.getCurrentUser().getString("aboutMe")!=null) {
            binding.aboutMeEdit.setText(ParseUser.getCurrentUser().getString("aboutMe").toString())
        }
        sport=ParseUser.getCurrentUser().getString("favouriteSport").toString()
            when(sport){
                "None" -> binding.noneBtn.isChecked = true
                "Soccer" -> binding.soccerBtn.isChecked=true
                "Ball Hockey" -> binding.ballHockeyBtn.isChecked = true
                "Basketball" -> binding.basketballBtn.isChecked=true
                else ->{
                    binding.noneBtn.isChecked = true
                }
            }
        binding.favSportGroup.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.noneBtn) {
                sport= "none"
            }
            if (checkedId == R.id.soccerBtn) {
                sport = SportTypes.SOCCER.sport
            }
            if (checkedId == R.id.ballHockeyBtn) {
                sport = SportTypes.BallHockey.sport
            }
            if (checkedId == R.id.basketballBtn) {
                sport= SportTypes.BASKETBALL.sport
            }

        }
    }

    fun onSave(view: View) {
        if(binding.aboutMeEdit.text.toString().length<=250){
            ParseUser.getCurrentUser().fetch()
            finish()
            Log.i("LOG_TAG","HAHA wordcount: "+binding.aboutMeEdit.text.toString().length)
            var user = User(ParseUser.getCurrentUser().username, binding.aboutMeEdit.text.toString(), sport)
            ParseCode.UpdateProfile(user)
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this, "About Me section is too long", Toast.LENGTH_SHORT).show()
        }
    }
}