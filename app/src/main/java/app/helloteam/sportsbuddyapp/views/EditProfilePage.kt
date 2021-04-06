package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.data.ImageStorage
import app.helloteam.sportsbuddyapp.data.SportTypes
import app.helloteam.sportsbuddyapp.databinding.ActivityEditProfilePageBinding
import app.helloteam.sportsbuddyapp.models.User
import app.helloteam.sportsbuddyapp.parse.ParseCode
import com.parse.ParseUser
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class EditProfilePage : AppCompatActivity() {


    lateinit var profilepic: ImageView
    lateinit var button: Button
    private val pickImage = 100
    private var imageUri: Uri? = null


    private lateinit var binding: ActivityEditProfilePageBinding
    private var sport = "none"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProfilePageBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val btnLoadPic =findViewById<Button>(R.id.btnLoadPicture)
        val profilepic = findViewById<ImageView>(R.id.profilepic)



        ParseUser.getCurrentUser().fetch()

        binding.userNameEdit.text= ParseUser.getCurrentUser().username
        binding.dateText.text= ParseUser.getCurrentUser().createdAt.toString()


        binding.btnLoadPicture.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }


        if (ParseUser.getCurrentUser().getString("aboutMe")!=null) {
            binding.aboutMeEdit.setText(ParseUser.getCurrentUser().getString("aboutMe").toString())
        }
        sport=ParseUser.getCurrentUser().getString("favouriteSport").toString()
            when(sport){
                "None" -> binding.noneBtn.isChecked = true
                "Soccer" -> binding.soccerBtn.isChecked = true
                "Ball Hockey" -> binding.ballHockeyBtn.isChecked = true
                "Basketball" -> binding.basketballBtn.isChecked = true
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
            Log.i("LOG_TAG", "HAHA wordcount: " + binding.aboutMeEdit.text.toString().length)
            var user = User(
                ParseUser.getCurrentUser().username,
                binding.aboutMeEdit.text.toString(),
                sport
            )
            ParseCode.UpdateProfile(user)
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this, "About Me section is too long", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.profilepic.setImageURI(imageUri)
            val bit = getBitMap(imageUri)
            ImageStorage.saveInternalStorage(this, bit, "${ParseUser.getCurrentUser().username}ProfilePic")
        }
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun getBitMap(uri: Uri?): Bitmap? {
        var input: InputStream? = this.contentResolver.openInputStream(uri!!)
        val onlyBoundsOptions = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true
        onlyBoundsOptions.inDither = true //optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        if (input != null) {
            input.close()
        }
        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
            return null
        }
        val originalSize =
            if (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) onlyBoundsOptions.outHeight else onlyBoundsOptions.outWidth
        val ratio = if (originalSize > 200.00) originalSize / 200.00 else 1.0
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)
        bitmapOptions.inDither = true //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //
        input = this.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
        if (input != null) {
            input.close()
        }
        return bitmap
    }

    private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
        val k = Integer.highestOneBit(Math.floor(ratio).toInt())
        return if (k == 0) 1 else k
    }

}