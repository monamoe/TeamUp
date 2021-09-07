package app.helloteam.sportsbuddyapp.views

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.*
import app.helloteam.sportsbuddyapp.data.TimePickerFragment
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.*

class CreateEventActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private val AUTOCOMPLETE_REQUEST_CODE = 1


    //time attributes
    private var hour: Int = 0
    private var min: Int = 0
    private var endHour: Int = 0
    private var endMin: Int = 0
    private var endTimeBool: Boolean = false
    private var yearPicked: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var monthPicked: Int = (Calendar.getInstance().get(Calendar.MONTH)) + 1
    private var dayPicked: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // sports attributes
    private var activitySelection: String? = ""

    // location attributes
    private var locationPlaceId: String = "" // place id for the location
    private var locationname: String = "" // the name of the park, (not the address to the park)
    private var address: String = ""
    private var lat: Double = 0.0
    private var long: Double = 0.0

    //additional information
    private var addionalInformation = ""
    private var eventTitle = ""
    private var eventSpace = 0

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
        val activityType: Spinner = findViewById<Spinner>(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.activitylist,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityType.adapter = adapter
        }
        activityType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                if (parent != null) {
                    activitySelection = parent.getItemAtPosition(pos).toString()
                }
            }

        }

        //set the start time
        val timeBtn = findViewById<Button>(R.id.TimeBtn)
        timeBtn.setOnClickListener {
            endTimeBool = false
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }
        if (hour != 0 || min != 0) {
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

        //set the end time
        val endTimeBtn = findViewById<Button>(R.id.endTimeBtn)
        endTimeBtn.setOnClickListener {
            endTimeBool = true
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }
        if (endHour != 0 || endMin != 0) {
            endTimeBtn.setText("$endHour:$endMin")
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
                var latlong = getLocationFromAddress(this@CreateEventActivity, address)
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
            eventTitle = findViewById<TextView>(R.id.eventTitle).text.toString()
            if (findViewById<EditText>(R.id.eventSpace).text.toString().equals(""))
                eventSpace = 1;
            else
                eventSpace = findViewById<EditText>(R.id.eventSpace).text.toString().toInt()
            addionalInformation = findViewById<TextView>(R.id.additionalInformation).text.toString()

            // enter required fields
            if (!address.equals("") && !activitySelection.equals("") && hour != 0 && !eventTitle.equals(
                    ""
                )
            ) {
                val date: Date = Date(yearPicked - 1900, monthPicked, dayPicked, hour, min)
                val endDate: Date =
                    Date(yearPicked - 1900, monthPicked, dayPicked, endHour, endMin)

                //pushing to firestore database required the use of a hashmap,
                val db = Firebase.firestore

                //hashmap models
                val eventHashMap = hashMapOf(
                    "title" to eventTitle,
                    "eventSpace" to eventSpace,
                    "activity" to activitySelection,
                    "hostID" to FirebaseAuth.getInstance().uid,
                    "eventPlaceID" to locationPlaceId,
                    "date" to date,
                    "endDate" to endDate

                )
                val LocationsHashMap = hashMapOf(
                    "Location Name" to address,
                    "Lat" to lat,
                    "Lon" to long
                )

                // add location unless it already exists
                db.collection("Location").document("$lat$long")
                    .set(LocationsHashMap, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("CreatingEvent", "Created $lat$long document")
                    }
                    .addOnFailureListener { e ->
                        Log.w("a", "Error creating location document", e)
                    }

                // pushes event data to Event Collection inside of Location with ID "$lat$long"
                db.collection("Location").document("$lat$long").collection("Events").document()
                    .set(eventHashMap, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("CreatingEvent", "Created $lat$long document")
                        val intent = Intent(this, LandingPageActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.w("a", "Error creating location document", e)
                    }
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // when the time select fragment is li
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (!endTimeBool) {
            val timeBtn = findViewById<Button>(R.id.TimeBtn)
            hour = hourOfDay
            min = minute
            timeBtn.setText("$hour:$min")
        } else {
            val timeBtn = findViewById<Button>(R.id.endTimeBtn)
            endHour = hourOfDay
            endMin = minute
            timeBtn.setText("$endHour:$endMin")
        }
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

