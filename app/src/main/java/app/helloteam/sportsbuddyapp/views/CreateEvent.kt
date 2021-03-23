package app.helloteam.sportsbuddyapp.views

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.*
import app.helloteam.sportsbuddyapp.data.SportTypes
import app.helloteam.sportsbuddyapp.data.TimePickerFragment
import app.helloteam.sportsbuddyapp.models.SportEvents
import app.helloteam.sportsbuddyapp.models.SportLocation
import app.helloteam.sportsbuddyapp.parse.ParseCode
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateEvent : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    // sports attributes
    private var hour: Int = 0
    private var min: Int = 0
    private var yearPicked: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var monthPicked: Int = (Calendar.getInstance().get(Calendar.MONTH)) + 1
    private var dayPicked: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // location attributes
    private var locationPlaceId: String = "" // place id for the location
    private var locationname: String = "" // the name of the park, (not the address to the park)
    private var address: String = ""
    private var lat: Double = 0.0
    private var long: Double = 0.0
    private var date: Date = Date()
    private val context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        val api: String = getString(R.string.google_key)
        // Initialize the SDK
        Places.initialize(applicationContext, api)
        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)
        val createBtn = findViewById<Button>(R.id.CreateBtn)
        //sport type
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
        //time
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
            val format = SimpleDateFormat("yyyy-MM-dd")
            val dateString: String = format.format(Date())
            date = format.parse("$yearPicked-$monthPicked-$dayPicked")
            Log.i("LOG_TAG", "HAHA Event date:" + date)
        }
        //address
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                address = place.name.toString()
                Log.i("LOG_TAG", "HAHA: address: " + address)
                var latlong = getLocationFromAddress(this@CreateEvent, address)
                if (latlong != null) {
                    long = latlong.longitude
                    lat = latlong.latitude
                    locationPlaceId = place.id.toString()
                }
                Log.i("LOG_TAG", "HAHA: Location from address: ${lat}, ${place.id}, ${long}")
            }

            override fun onError(status: Status) {
                Log.i("LOG_TAG", "An error occurred: $status")
            }
        })
        createBtn.setOnClickListener {
            // enter required fields
            if (!address.equals("") && sportSelection != SportTypes.NONE && hour != 0) {
                //check if a record with that address already exists
                val query = ParseQuery.getQuery<ParseObject>("Location")
                query.whereEqualTo("locationPlaceId", locationPlaceId)
                query.findInBackground { locationlist, e ->
                    if (e == null) {
                        // if location doesnt exist, create location
                        if (locationlist.size == 0) {
                            // create a new location
                            Log.i("LOG_TAG", "HAHA: creating new location")
                            var ec = SportLocation(locationPlaceId, address, address, lat, long, 1)
                            ParseCode.LocationCreation(ec)
                            Log.i("LOG_TAG", "HAHA: IN IF")
                        } else {
                            Log.i("LOG_TAG", "HAHA: IN ELSE")
                            for (locations in locationlist) {
                                Log.i("LOG_TAG", "HAHA In FOR")
                                locations.put("amount", locations.getInt("amount") + 1)
                                Log.i("LOG_TAG", "HAHA " + locations.getInt("amount"))
                                locations.save()
                            }
                        }
                    } else {
                        Log.i(
                            "LOG_TAG",
                            "HAHA: There was an error with getting the locations : " + e.message
                        )
                    }
                }
                Log.i("LOG_TAG", "HAHA: Creating event record in database")
                var se = SportEvents(
                    sportSelection, ParseUser.getCurrentUser().username,
                    hour, min, yearPicked, monthPicked, dayPicked, locationPlaceId, date
                );
                ParseCode.EventCreation(se)
                val intent = Intent(this, LandingPageActivity::class.java)
                startActivity(intent)
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

    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        Log.i("LOG_TAG", "HAHA In LAT LONG METHOD ")
        val coder = Geocoder(context)
        val address: List<Address>?
        var place: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                Log.i("LOG_TAG", "HAHA In LAT LONG METHOD NUll ")
                return null
            }
            Log.i("LOG_TAG", "HAHA In LAT LONG METHOD NOT NULL")
            val location = address[0]
            place = LatLng(location.latitude, location.longitude)
        } catch (ex: IOException) {
            Log.i("LOG_TAG", "HAHA In LAT LONG METHOD ERROR " + ex.toString())
            ex.printStackTrace()
        }
        return place
    }
}