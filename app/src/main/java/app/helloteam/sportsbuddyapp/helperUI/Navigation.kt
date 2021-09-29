package app.helloteam.sportsbuddyapp.helperUI

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.views.LandingPage

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.LandingPage.route) {



        // Landing Page
        composable(route = Screen.LandingPage.route) {
            LandingPage(navController = navController)
        }


        // Event Page
        composable(
            route = Screen.DetailScreen.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "Phillip"
                    nullable = true
                }
            )
        ) { entry ->
            DetailScreen(name = entry.arguments?.getString("name"))
        }
    }
}

//
//@Composable
//fun MainScreen(navController: NavController) {
//    var text by remember {
//        mutableStateOf("")
//    }
//
//    Column(
//        verticalArrangement = Arrangement.Center,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 50.dp)
//    ) {
//        TextField(
//            value = text, onValueChange = {
//                text = it
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Button(
//            onClick = {
//                navController.navigate(Screen.DetailScreen.withArgs(text))
//            },
//            modifier = Modifier.align(Alignment.End)
//        ) {
//            Text(text = "To Detail Screen")
//        }
//    }
//}
//
//
//@Composable
//fun DetailScreen(name: String?) {
//    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
//        Text(text = "Hello, $name")
//    }
//}