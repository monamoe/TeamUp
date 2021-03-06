package app.helloteam.sportsbuddyapp

import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser


class CreateEvent : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    var hour: Int = 0
    var min: Int = 0
    var yearPicked: Int = Calendar.getInstance().get(Calendar.YEAR)
    var monthPicked: Int = (Calendar.getInstance().get(Calendar.MONTH)) + 1
    var dayPicked: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    var address: String = ""

    private val context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        val api: String = getString(R.string.places_key)
        // Initialize the SDK
        Places.initialize(applicationContext, api)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        val createBtn = findViewById<Button>(R.id.CreateBtn)

        //spot type
        val sportType = findViewById<RadioGroup>(R.id.SportType)
        var sportSelection: SportTypes = SportTypes.NONE
        sportType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.SoccerBtn) {
                sportSelection = SportTypes.SOCCER
            }
            if (checkedId == R.id.BasketballBtn) {
                sportSelection = SportTypes.BASKETBALL
            }
            if (checkedId == R.id.HockeyBtn) {
                sportSelection = SportTypes.BallHockey
            }
        }

        //date and time
        val timeBtn = findViewById<Button>(R.id.TimeBtn)
        timeBtn.setOnClickListener {
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }
        if (hour != 0) {
            timeBtn.setText("$hour:$min")
        }
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)

        ) { view, year, month, day ->
            val month = month + 1
            dayPicked = day
            yearPicked = year
            monthPicked = month
        }

        //location
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: ${place.name}, ${place.id}")
                address = place.name.toString()
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: $status")
            }
        })

        createBtn.setOnClickListener {
            if (!address.equals("") && sportSelection != SportTypes.NONE && hour != 0) {

                //find if the address exists in the location table
                val locationQuery = ParseQuery.getQuery<ParseObject>("Location")
                locationQuery.whereEqualTo("Address", address)
                locationQuery.findInBackground { locationList, e ->
                    if (e == null) {
                        Log.d("score", "Retrieved " + locationList.size + " scores")
                    } else {
                        Log.d("score", "Error: " + e.toString())
                    }
                }


                var se = SportEvents(
                    sportSelection, ParseUser.getCurrentUser().username,
                    hour, min, yearPicked, monthPicked, dayPicked, ""
                );
                ParseCode.EventCreation(se)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val timeBtn = findViewById<Button>(R.id.TimeBtn)
        hour = hourOfDay
        min = minute
        timeBtn.setText("$hour:$min")
    }
}
