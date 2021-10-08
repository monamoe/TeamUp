package app.helloteam.sportsbuddyapp.helperUI

import androidx.compose.foundation.Image
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
import app.helloteam.sportsbuddyapp.views.useIntentOnRoute


data class NavMenuContent(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int = 0
) {

}


val items = listOf(
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
        icon = Icons.Default.Star
    ),
    NavMenuContent(
        title = "Teams",
        route = "teams",
        icon = Icons.Default.Notifications
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