package app.helloteam.sportsbuddyapp.views.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.helloteam.sportsbuddyapp.R

val gothicA1 = FontFamily(
    listOf(
        Font(R.font.gothica1_regular, FontWeight.Normal),
        Font(R.font.gothica1_medium, FontWeight.Medium),
        Font(R.font.gothica1_semibold, FontWeight.SemiBold),
        Font(R.font.gothica1_bold, FontWeight.Bold),
        Font(R.font.gothica1_black, FontWeight.Black),
    )
)

val Typography = Typography(
    body1 = TextStyle(
        color = AquaBlue,
        fontFamily = gothicA1,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    h1 = TextStyle(
        color = Color.Black,
        fontFamily = gothicA1,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp
    ),
    h2 = TextStyle(
        color = Color.Black,
        fontFamily = gothicA1,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    h3 = TextStyle(
        color = Color.Black,
        fontFamily = gothicA1,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),

    // see more
    h4 = TextStyle(
        color = Color.Black,
        fontFamily = gothicA1,
        fontSize = 14.sp
    )
)