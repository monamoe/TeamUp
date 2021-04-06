// implements marker
// custom marker class
// doing it this way so i can save the info window information into this class (title snippet image etc)

package app.helloteam.sportsbuddyapp.models


import com.google.android.gms.maps.model.LatLng


class ParkLocationMarker {


    // ID of the location in the Locations Table
    private var id: String? = ""

    // Name of the location (park name)
    private var name: String = ""

    //
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    //marker i dont think we need this
//    lateinit private var mMarker: Marker

    //constructor
    fun createParkLocationMarker(
        inputId: String?,
        inputName: String,
        inputLat: Double,
        inputLon: Double
    ) {
        id = inputId
        name = inputName
        lon = inputLon
        lat = inputLat
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

}