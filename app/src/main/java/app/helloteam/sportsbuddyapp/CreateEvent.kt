package app.helloteam.sportsbuddyapp

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.parse.ParseUser
import java.util.*

class CreateEvent : AppCompatActivity() {
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val sportType= findViewById<RadioGroup>(R.id.SportType)
        val address = findViewById<EditText>(R.id.AddressTxt)
        val createBtn = findViewById<Button>(R.id.CreateBtn)
        var sportSelection : SportTypes =SportTypes.NONE
        sportType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==R.id.SoccerBtn){
                sportSelection= SportTypes.SOCCER
            }
            if (checkedId==R.id.BasketballBtn){
                sportSelection=SportTypes.BASKETBALL
            }
            if(checkedId==R.id.HockeyBtn){
                sportSelection=SportTypes.BallHockey
            }
        }

        createBtn.setOnClickListener {
            if (!address.text.toString().equals("")&& sportSelection!=SportTypes.NONE){
                var se = SportEvents(sportSelection, address.text.toString(), ParseUser.getCurrentUser().username);
                ParseCode.EventCreation(se)
            }else{
               Toast.makeText(this,"Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
