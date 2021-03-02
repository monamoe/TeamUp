package app.helloteam.sportsbuddyapp

import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.parse.ParseUser
import java.io.IOException
import java.util.*


class CreateEvent : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    var hour:Int =0
    var min:Int=0
    var yearPicked:Int=Calendar.getInstance().get(Calendar.YEAR)
    var monthPicked:Int=(Calendar.getInstance().get(Calendar.MONTH))+1
    var dayPicked:Int=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    var address: String=""
    var lat: Double =0.0
    var long : Double = 0.0
    private val context:Context=this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        val api: String = getString(R.string.places_key)
        // Initialize the SDK
        Places.initialize(applicationContext, api)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        val sportType= findViewById<RadioGroup>(R.id.SportType)
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

        val timeBtn = findViewById<Button>(R.id.TimeBtn)
        timeBtn.setOnClickListener {
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }
        if (hour!=0){
            timeBtn.setText("$hour:$min")
        }

        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)

        ) { view, year, month, day ->
            val month = month + 1
            dayPicked=day
            yearPicked=year
            monthPicked=month
        }

        val autocompleteFragment =
                supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                        as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                address = place.name.toString()
                var latlong = getLocationFromAddress(this@CreateEvent, address)
                if (latlong != null) {
                    long = latlong.longitude
                }
                if (latlong != null) {
                    lat= latlong.latitude
                }
                if (latlong != null) {
                    Log.i("TAG", "Place: ${place.name}, ${place.id}, ${latlong.latitude}")
                }

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: $status")
            }
        })

        createBtn.setOnClickListener {
            if (!address.equals("")&& sportSelection!=SportTypes.NONE&&hour!=0){
                var se = SportEvents(
                        sportSelection, address, ParseUser.getCurrentUser().username,
                        hour, min, yearPicked, monthPicked, dayPicked, lat, long
                );
                ParseCode.EventCreation(se)
            }else{
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val timeBtn = findViewById<Button>(R.id.TimeBtn)
        hour = hourOfDay
        min=minute
        timeBtn.setText("$hour:$min")
    }

    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var place: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            place = LatLng(location.latitude, location.longitude)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return place
    }
}
