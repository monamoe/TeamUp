// implements marker
// custom marker class
// doing it this way so i can save the info window information into this class (title snippet image etc)

package app.helloteam.sportsbuddyapp


import android.os.IBinder
import com.google.android.gms.dynamic.IObjectWrapper
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class ParkLocationMarker : zzt {


    // ID of the location in the Locations Table
    private var id: String = ""

    // Name of the location (park name)
    private var name: String = ""

    //
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    //marker i dont think we need this
//    lateinit private var mMarker: Marker

    //constructor
    fun createParkLocationMarker(
        inputId: String,
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
    fun getID(): String {
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

    override fun zzg(p0: IObjectWrapper?) {
        TODO("Not yet implemented")
    }

    override fun zze(p0: IObjectWrapper?) {
        TODO("Not yet implemented")
    }

    override fun getSnippet(): String {
        TODO("Not yet implemented")
    }

    override fun setZIndex(p0: Float) {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float {
        TODO("Not yet implemented")
    }

    override fun getId(): String {
        TODO("Not yet implemented")
    }

    override fun setRotation(p0: Float) {
        TODO("Not yet implemented")
    }

    override fun zzk(): IObjectWrapper {
        TODO("Not yet implemented")
    }

    override fun isDraggable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hideInfoWindow() {
        TODO("Not yet implemented")
    }

    override fun setTitle(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun isInfoWindowShown(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getTitle(): String {
        TODO("Not yet implemented")
    }

    override fun getAlpha(): Float {
        TODO("Not yet implemented")
    }

    override fun setAlpha(p0: Float) {
        TODO("Not yet implemented")
    }

    override fun asBinder(): IBinder {
        TODO("Not yet implemented")
    }

    override fun setFlat(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setDraggable(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isFlat(): Boolean {
        TODO("Not yet implemented")
    }

    override fun showInfoWindow() {
        TODO("Not yet implemented")
    }

    override fun remove() {
        TODO("Not yet implemented")
    }

    override fun getPosition(): LatLng {
        return LatLng(lat, lon)
    }

    override fun setVisible(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun zzj(p0: zzt?): Boolean {
        TODO("Not yet implemented")
    }

    override fun zzj(): Int {
        TODO("Not yet implemented")
    }

    override fun setInfoWindowAnchor(p0: Float, p1: Float) {
        TODO("Not yet implemented")
    }

    override fun setPosition(p0: LatLng?) {
        TODO("Not yet implemented")
    }

    override fun setSnippet(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getRotation(): Float {
        TODO("Not yet implemented")
    }

    override fun isVisible(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setAnchor(p0: Float, p1: Float) {
        TODO("Not yet implemented")
    }
}