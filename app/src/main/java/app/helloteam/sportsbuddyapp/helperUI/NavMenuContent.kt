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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.views.*

/**
 * Navigation Bar
 */

data class NavMenuContent(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int = 0,
    val icon2: Int = 0
)

val items = listOf(
    NavMenuContent(
        title = "Home",
        route = "LandingPage2",
        icon = Icons.Default.Home,
    ),
    NavMenuContent(
        title = "Messages",
        route = "LatestMessagesActivity",
        icon = Icons.Default.Email,
        badgeCount = 5

    ),
    NavMenuContent(
        title = "Map",
        route = "map",
        icon = Icons.Default.Menu,
        icon2 = R.drawable.ic_baseline_map_24
    ),
    NavMenuContent(
        title = "Teams",
        route = "TeamsActivity",
        icon = Icons.Default.Notifications,
        icon2 = R.drawable.ic_baseline_people_24
    ),
    NavMenuContent(
        title = "Profile",
        route = "ProfilePage",
        icon = Icons.Default.Person
    ),
)

// TO DO PASS CONTEXT and compare values
/**
 * NavBar Creator
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClicker: (NavMenuContent) -> Unit,
    context: Context
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
                unselectedContentColor = Color.White,
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
                            if (item.icon2 == 0) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = item.icon2),
                                    contentDescription = item.title // decorative element
                                )
                            }
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


/**
 * Intent Navigation Router
 * @param context current context
 * @param route decides on navigation
 */
// TO DO : change routing to compare values NavContent and Context to determine weather or not to switch the view
fun useIntentOnRoute(context: Context, route: String) {
    // jank way to get the name of the file the user is currently looking at
    var listContextName = context.toString().split("@")[0]
    var currentPageNameArray = listContextName.split(".")
    var currentPageName = currentPageNameArray[currentPageNameArray.size - 1]
    Log.i("LOG_TAG", "CURRENT CONTEXT: current page vs route : $currentPageName - $route")


    var intent = Intent(context, LandingPage2::class.java)
    // if the current page is the one the user wants to go to
    if (currentPageName != route) {
        when (route) {
            "LandingPage2" -> intent = Intent(context, LandingPage2::class.java)
            "chat" -> intent = Intent(context, LatestMessagesActivity::class.java)
            "map" -> intent = Intent(context, map::class.java)
            "teams" -> intent = Intent(context, TeamsActivity::class.java)
            "profile" -> intent = Intent(context, ProfilePage::class.java)
            else -> {
                Log.i("LOG_TAG", "FATAL ERROR! UNABLE TO GO TO THE VIEW REQUESTED! ")
            }
        }
        context.startActivity(intent)
    }
}