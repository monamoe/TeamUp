package app.helloteam.sportsbuddyapp.views

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.data.TimePickerFragment
import app.helloteam.sportsbuddyapp.firebase.FileHandling
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.Date

class CreateEventActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    // Time attributes
    var hour: Int = 0
    var min: Int = 0
    var endHour: Int = 0
    var endMin: Int = 0
    var endTimeBool: Boolean = false
    var yearPicked: Int = Calendar.getInstance().get(Calendar.YEAR)
    var monthPicked: Int = Calendar.getInstance().get(Calendar.MONTH)
    var dayPicked: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // Sports attributes
    var activitySelection: String? = ""

    // Location attributes
    var locationPlaceId: String = "" // place id for the location
    var locationname: String = "" // the name of the park, (not the address)
    var address: String = ""
    var lat: Double = 0.0
    var long: Double = 0.0

    // Additional information
    var addionalInformation = ""
    var eventTitle = ""
    var eventSpace = 0

    val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        supportActionBar?.title = "Event Information"

        val api: String = getString(R.string.google_key)

        // Initialize the Places SDK
        Places.initialize(applicationContext, api)

        val createBtn = findViewById<Button>(R.id.CreateBtn)

        // Setup spinner for sport type
        val activityType: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.activitylist,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityType.adapter = adapter
        }
        activityType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (parent != null) {
                    activitySelection = parent.getItemAtPosition(pos).toString()
                }
            }
        }

        // Set start time button
        val timeBtn = findViewById<Button>(R.id.TimeBtn)
        timeBtn.setOnClickListener {
            endTimeBool = false
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }
        if (hour != 0 || min != 0) {
            timeBtn.text = "$hour:$min"
        }

        // Date picker setup
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        datePicker.setMinDate(System.currentTimeMillis() - 1000)
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            dayPicked = day
            yearPicked = year
            monthPicked = month
        }

        // Set end time button
        val endTimeBtn = findViewById<Button>(R.id.endTimeBtn)
        endTimeBtn.setOnClickListener {
            endTimeBool = true
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }
        if (endHour != 0 || endMin != 0) {
            endTimeBtn.text = "$endHour:$endMin"
        }

        // Place autocomplete fragment setup
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                address = place.name.toString()
                Log.i("LOG_TAG", "Address selected: $address")
                val latlong = getLocationFromAddress(this@CreateEventActivity, address)
                if (latlong != null) {
                    lat = latlong.latitude
                    long = latlong.longitude
                    locationPlaceId = place.id.toString()
                }
                Log.i("LOG_TAG", "Location from address: $lat, $locationPlaceId, $long")
            }

            override fun onError(status: Status) {
                Log.i("LOG_TAG", "Place selection error: $status")
            }
        })

        createBtn.setOnClickListener {
            eventTitle = findViewById<TextView>(R.id.eventTitle).text.toString()
            eventSpace = if (findViewById<EditText>(R.id.eventSpace).text.toString()
                    .isEmpty()
            ) 1 else findViewById<EditText>(R.id.eventSpace).text.toString().toInt()
            val date = Date(yearPicked - 1900, monthPicked, dayPicked, hour, min)
            val endDate = Date(yearPicked - 1900, monthPicked, dayPicked, endHour, endMin)
            val addInfo = findViewById<EditText>(R.id.aboutEventEdit).text.toString()

            if (endDate <= date) {
                Toast.makeText(this, "Invalid start and end time", Toast.LENGTH_SHORT).show()
            } else if (addInfo.length > 200) {
                Toast.makeText(this, "Information too long", Toast.LENGTH_SHORT).show()
            } else if (address.isNotEmpty() && !activitySelection.isNullOrEmpty() && hour != 0 && eventTitle.isNotEmpty()) {

                val db = Firebase.firestore

                val eventHashMap = hashMapOf(
                    "title" to eventTitle,
                    "currentlyAttending" to 0,
                    "eventSpace" to eventSpace,
                    "activity" to activitySelection,
                    "hostID" to FirebaseAuth.getInstance().uid,
                    "eventPlaceID" to locationPlaceId,
                    "date" to date,
                    "endDate" to endDate,
                    "information" to addInfo
                )

                val locationsHashMap = hashMapOf(
                    "Location Name" to address,
                    "Lat" to lat,
                    "Lon" to long,
                )

                val locationID = lat.toString() + long.toString()
                db.collection("Location").document(locationID)
                    .set(locationsHashMap, SetOptions.merge())
                    .addOnSuccessListener {
                        db.collection("Location").document(locationID).get()
                            .addOnSuccessListener { loc ->
                                if (loc.get("StreetView") == null) {
                                    Log.i("CreateEventActivity", "Uploading street view image")
                                    FileHandling.uploadEventImage(
                                        this,
                                        loc.get("Lat").toString(),
                                        loc.get("Lon").toString(),
                                        loc.id
                                    )
                                }
                            }

                        Log.d("CreateEventActivity", "Created Location document: $locationID")

                        val eventID = FirebaseFirestore.getInstance().collection("Location")
                            .document(locationID)
                            .collection("Events").document().id

                        db.collection("Location").document(locationID).collection("Events")
                            .document(eventID)
                            .set(eventHashMap, SetOptions.merge())
                            .addOnSuccessListener {
                                val hostingHashMap = hashMapOf(
                                    "locationID" to locationID,
                                    "eventID" to eventID
                                )
                                db.collection("User")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .collection("Hosting").document(eventID)
                                    .set(hostingHashMap, SetOptions.merge())
                                    .addOnSuccessListener {
                                        val intent = Intent(this, SplashActivity::class.java)
                                        startActivity(intent)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.w("CreateEventActivity", "Error creating event document", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w("CreateEventActivity", "Error creating location document", e)
                    }
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (!endTimeBool) {
            val timeBtn = findViewById<Button>(R.id.TimeBtn)
            hour = hourOfDay
            min = minute
            timeBtn.text = "$hour:$min"
        } else {
            val timeBtn = findViewById<Button>(R.id.endTimeBtn)
            endHour = hourOfDay
            endMin = minute
            timeBtn.text = "$endHour:$endMin"
        }
    }

    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = context?.let { Geocoder(it) }
        val address: List<Address>?
        var place: LatLng? = null
        try {
            if (coder != null) {
                address = strAddress?.let { coder.getFromLocationName(it, 5) }
                if (address == null || address.isEmpty()) {
                    return null
                }
                val location = address[0]
                place = LatLng(location.latitude, location.longitude)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return place
    }
}
