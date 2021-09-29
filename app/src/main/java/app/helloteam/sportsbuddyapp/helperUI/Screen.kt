package app.helloteam.sportsbuddyapp.helperUI

// manages screen routing
sealed class Screen(val route: String) {
    object LoginPage : Screen("login_page")
    object LandingPage : Screen("landing_page")
    object DetailScreen : Screen("detail_screen")


    // constructing routes with NOTNULL arguments
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}