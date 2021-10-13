package app.helloteam.sportsbuddyapp.helperUI

import android.graphics.drawable.VectorDrawable
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
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.views.useIntentOnRoute


data class NavMenuContent(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val displayBadge: Boolean = false,
    val icon2: Int = 0
) {

}


val items = listOf(
    NavMenuContent(
        title = "Home",
        route = "home",
        icon = Icons.Default.Home,
        displayBadge = false
    ),
    NavMenuContent(
        title = "Messages",
        route = "chat",
        icon = Icons.Default.Email,
        displayBadge = true
    ),
    NavMenuContent(
        title = "Map",
        route = "map",
        icon = Icons.Default.Menu,
        icon2 = R.drawable.ic_baseline_map_24,
        displayBadge = false
    ),
    NavMenuContent(
        title = "Teams",
        route = "teams",
        icon = Icons.Default.Notifications,
        icon2 = R.drawable.ic_baseline_people_24,
        displayBadge = false
    ),
    NavMenuContent(
        title = "Profile",
        route = "profile",
        icon = Icons.Default.Person,
        displayBadge = false
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
                unselectedContentColor = Color.White,
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (item.displayBadge == true) {
                            BadgeBox(
                                badgeContent = {}
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
                    }
                }
            )
        }
    }
}