package app.helloteam.sportsbuddyapp.views

import android.content.Intent
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
import app.helloteam.sportsbuddyapp.databinding.ActivityBlockPageBinding
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
import java.util.*

class BlockPage : AppCompatActivity() {


    private lateinit var binding: ActivityBlockPageBinding


    // FIREBASE MIGRATION //
    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().uid.toString()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockPageBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)






    }






}


