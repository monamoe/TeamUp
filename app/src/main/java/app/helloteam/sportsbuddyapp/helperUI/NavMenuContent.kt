package app.helloteam.sportsbuddyapp.helperUI

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.views.EditProfilePage
import app.helloteam.sportsbuddyapp.views.LandingPage2
import app.helloteam.sportsbuddyapp.views.map
import app.helloteam.sportsbuddyapp.views.useIntentOnRoute


// each view needs to have this function inside it
//fun useIntentOnRoute(context: Context, route: String) {
//    var intent = Intent(context, LandingPage2::class.java)
//    when (route) {
//        "home" -> Log.i("LOG_NAVIGATION", "ALREADY ON REQUESTED PAGE")
////        "messages" -> intent = Intent(context, messages::class.java)
//        "map" -> intent = Intent(context, map::class.java)
////        "notifications" -> intent = Intent(context, notifications::class.java)
//        "profile" -> intent = Intent(context, EditProfilePage::class.java)
//        else -> {
//            Log.i("LOG_TAG", "FATAL ERROR! UNABLE TO GO TO THE VIEW REQUESTED! ")
//        }
//    }
//    context.startActivity(intent)
//}



data class NavMenuContent(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int = 0
)

private val items = listOf(
    NavMenuContent(
        title = "Home",
        route = "home",
        icon = Icons.Default.Home,
    ),
    NavMenuContent(
        title = "Messages",
        route = "chat",
        icon = Icons.Default.Email,
        badgeCount = 5

    ),
    NavMenuContent(
        title = "Map",
        route = "map",
        icon = Icons.Default.Favorite,
    ),
    NavMenuContent(
        title = "Notifications",
        route = "notifications",
        icon = Icons.Default.Notifications,
        badgeCount = 4
    ),
    NavMenuContent(
        title = "Profile",
        route = "profile",
        icon = Icons.Default.Person
    ),
)

/**
 * NavBar Creator
 *
 * This class returns a listOf EventCard objects
 * @param userID
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClicker: (NavMenuContent) -> Unit
) {
    val a = navController.currentBackStackEntryAsState()
    val currentcontext = LocalContext.current
    BottomNavigation(
        elevation = 5.dp,
        backgroundColor = colorResource(id = R.color.primaryDarkColor),
        modifier = modifier
    ) {
//        RowScope
        items.forEach { item ->
            val selected = item.route == a.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = {
                    useIntentOnRoute(currentcontext, item.route)
                },
                selectedContentColor = colorResource(id = R.color.secondaryDarkColor),
                unselectedContentColor = Color.Gray,
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (item.badgeCount > 0) {
                            BadgeBox(
                                badgeContent = {
                                    Text(
                                        text = item.badgeCount.toString()
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            }
                        } else {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        }
                        if (selected) {
                            Text(
                                text = item.title,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            )
        }
    }
}