/*
 * monamoe
 * custom marker class to manage marker locations
 * doing it this way so i can save the info window information into this class (title snippet image etc)
 */

package app.helloteam.sportsbuddyapp.models


import android.util.Log
import com.google.android.gms.maps.model.LatLng


class ParkLocationMarker {


    // ID of the location in the Locations Table
    private var id: String? = ""

    // Name of the location (park name)
    private var name: String = ""

    // LatLon locations for marker on the map
    private var lat: Double = 0.0
    private var lon: Double = 0.0


    //constructor
    fun createParkLocationMarker(
        inputId: String?,
        inputName: String,
        inputLat: Double,
        inputLon: Double
    ) {
        id = inputId
        name = inputName
        lat = inputLat
        lon = inputLon
    }


    //getters
    fun getID(): String? {
        return id
    }

    fun getName(): String {
        return name
    }

    fun getLat(): Double {
        return lat;
    }

    fun getLon(): Double {
        return lon;
    }

    fun getLatLng(): LatLng {
        return LatLng(this.getLat(), this.getLon())
    }

    // for testing
    fun showdata() {
        Log.i("ParkLocationsManager", "$name : $lat // $lon \n")
    }

}