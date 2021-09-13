package app.helloteam.sportsbuddyapp.models

// Event Displayer class ( for array list)
class EventDisplayer {
    var id: String = ""
    var name: String = ""
    var address: String = ""
    var time: String = ""
    var host: String = ""


    fun getID(): String {
        return this.id
    }

    // main constuctor
    constructor(id: String, name: String, address: String, time: String, host: String) {
        this.id = id
        this.name = name
        this.address = address
        this.time = time
        this.host = host

    }
}