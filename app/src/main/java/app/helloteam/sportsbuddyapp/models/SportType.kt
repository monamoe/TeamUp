package app.helloteam.sportsbuddyapp.models

class SportType constructor(
    val SportID: Int,
    val SportName: String,
    val Equipment: String,
    val MinPlayers: Int,
    val MaxPlayers: Int,
    val IdealLocation: String,
    val IsTeamSport: Boolean
){

}