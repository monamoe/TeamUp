package app.helloteam.sportsbuddyapp.helperUI

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.recyclerview.widget.RecyclerView
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.views.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
    val readChat = remember { mutableStateOf(true) }


    fun listenForLatestMessages() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$currentUser")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatLogActivity.ChatMessage::class.java)
                var otherUser = chatMessage?.fromId.toString()
                if (otherUser == currentUser.toString()) {
                    otherUser = chatMessage?.toId.toString()
                }

                ref.child(otherUser).child("read").get().addOnSuccessListener {
                    readChat.value = it.value != false
                }
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatLogActivity.ChatMessage::class.java)
                var otherUser = chatMessage?.fromId.toString()
                if (otherUser == currentUser.toString()) {
                    otherUser = chatMessage?.toId.toString()
                }

                ref.child(otherUser).child("read").get().addOnSuccessListener {
                    readChat.value = it.value != false
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }

    listenForLatestMessages()
    val a = navController.currentBackStackEntryAsState()
    val currentcontext = LocalContext.current
    BottomNavigation(
        elevation = 5.dp,
        backgroundColor = colorResource(id = R.color.primaryDarkColor),
        modifier = modifier
    ) {
//        RowScope
        var selected = "home" == a.value?.destination?.route
        BottomNavigationItem(
            selected = selected,
            onClick = {
                useIntentOnRoute(currentcontext, "home")
            },
            selectedContentColor = colorResource(id = R.color.secondaryDarkColor),
            unselectedContentColor = Color.White,
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                }
            }
        )

        selected = "chat" == a.value?.destination?.route
        BottomNavigationItem(
            selected = selected,
            onClick = {
                useIntentOnRoute(currentcontext, "chat")
                readChat.value = true
                FirebaseDatabase.getInstance()
                    .getReference("/latest-messages/${FirebaseAuth.getInstance().currentUser?.uid}")
                    .get()
                    .addOnSuccessListener { latests ->
                        latests.children.forEach { latest ->
                            FirebaseDatabase.getInstance()
                                .getReference("/latest-messages/${FirebaseAuth.getInstance().currentUser?.uid}")
                                .child(latest.key.toString()).child("read").setValue(true)
                        }
                    }

            },
            selectedContentColor = colorResource(id = R.color.secondaryDarkColor),
            unselectedContentColor = Color.White,
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!readChat.value) {
                        BadgeBox(
                            badgeContent = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Messages"
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Messages"
                        )
                    }
                }
            }
        )

        selected = "map" == a.value?.destination?.route
        BottomNavigationItem(
            selected = selected,
            onClick = {
                useIntentOnRoute(currentcontext, "map")
            },
            selectedContentColor = colorResource(id = R.color.secondaryDarkColor),
            unselectedContentColor = Color.White,
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_map_24),
                        contentDescription = "Map" // decorative element
                    )
                }
            }
        )


        selected = "teams" == a.value?.destination?.route
        BottomNavigationItem(
            selected = selected,
            onClick = {
                useIntentOnRoute(currentcontext, "teams")
            },
            selectedContentColor = colorResource(id = R.color.secondaryDarkColor),
            unselectedContentColor = Color.White,
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_people_24),
                        contentDescription = "Teams" // decorative element
                    )
                }
            }
        )


        selected = "profile" == a.value?.destination?.route
        BottomNavigationItem(
            selected = selected,
            onClick = {
                useIntentOnRoute(currentcontext, "profile")
            },
            selectedContentColor = colorResource(id = R.color.secondaryDarkColor),
            unselectedContentColor = Color.White,
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile"
                    )
                }
            }
        )
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