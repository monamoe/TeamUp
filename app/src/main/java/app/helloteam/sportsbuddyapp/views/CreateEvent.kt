package app.helloteam.sportsbuddyapp.views

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.views.ui.theme.Purple200
import app.helloteam.sportsbuddyapp.views.ui.theme.Purple500
import com.google.android.libraries.places.api.Places
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class CreateEvent : ComponentActivity() {

    private lateinit var context: Context

    //time attributes
    private var hour: Int = 0
    private var min: Int = 0
    private var endHour: Int = 0
    private var endMin: Int = 0
    private var endTimeBool: Boolean = false
    private var yearPicked: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var monthPicked: Int = (Calendar.getInstance().get(Calendar.MONTH))
    private var dayPicked: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // sports attributes
    private var activitySelection: String? = ""

    // location attributes
    private var locationPlaceId: String = "" // place id for the location
    private var locationname: String = "" // the name of the park, (not the address to the park)
    private var address: String = ""
    private var lat: Double = 0.0
    private var long: Double = 0.0

    //additional information
    private var addionalInformation = ""
    private var eventTitle = ""
    private var eventSpace = 0

    // api
    private var api = ""

    // places client
//    val placesClient = Places.createClient(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        api = getString(R.string.google_key)


        super.onCreate(savedInstanceState)
        setContent {
            context = LocalContext.current
            CreateEventView()
        }
    }


    @Composable
    @Preview
    fun CreateEventView() {
        Scaffold(
            topBar = { TopBar() },
            content = {
                datePicker()
            },
            backgroundColor = Purple200
        )
    }

    @Composable
    fun TopBar() {
        TopAppBar(
            title = { Text(text = "Create Event", fontSize = 20.sp) },
            backgroundColor = Purple200,
            contentColor = Color.White
        )
    }

    @Composable
    fun CNavigation() {
        val navController = rememberNavController()
        NavHost(
            navController,
            startDestination = "CList"
        ) {
            composable("CountryList") {
                CListScreen(navController)
            }
        }
    }

    @Composable
    fun CListScreen(navController: NavHostController) {
        val textVal = remember { mutableStateOf(TextFieldValue("")) }
        Column() {
            SearchCountryList(textVal)
            CList(textVal)
        }
    }

    @Composable
    fun SearchCountryList(textVal: MutableState<TextFieldValue>) {
        TextField(
            value = textVal.value,
            onValueChange = { textVal.value = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(Color.White, fontSize = 18.sp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .padding(15.dp)
                        .size(24.dp)
                )
            },
            trailingIcon = {
                if (textVal.value != TextFieldValue("")) {
                    IconButton(
                        onClick = {
                            textVal.value = TextFieldValue("")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .padding(15.dp)
                                .size(24.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RectangleShape,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                cursorColor = Color.White,
                leadingIconColor = Color.White, trailingIconColor = Color.White,
                backgroundColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

    @Composable
    fun CList(textVal: MutableState<TextFieldValue>) {
        val countries = getListOfCountries()
        var filteredCountries: ArrayList<String>
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            val searchText = textVal.value.text
            filteredCountries = if (searchText.isEmpty()) {
                countries
            } else {
                val resultList = ArrayList<String>()
                for (country in countries) {
                    if (country.lowercase(Locale.getDefault())
                            .contains(searchText.lowercase(Locale.getDefault()))
                    ) {
                        resultList.add(country)
                    }
                }
                resultList
            }
//            items(5, filteredCountries) { filteredCountries ->
//                CountryLisItem(
//                    countryText = filteredCountries,
//                    onItemClick = { selectedCountry ->
//                        Toast.makeText(currentContext, "$selectedCountry")
//                    }
//                )
//            }
        }
    }

    @Composable
    fun CountryLisItem(
        countryText: String,
        onItemClick: (String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .clickable { onItemClick(countryText) }
                .background(Purple500)
                .height(60.dp)
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text(
                text = countryText,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }

    @Composable
    fun getListOfCountries(): ArrayList<String> {
        val icoCountryCodes = Locale.getISOCountries()
        val countryListWithEmoji = ArrayList<String>()
        for (countryCode in icoCountryCodes) {
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry
            val flagoffset = 0x1F1E6
            val asciiOffset = 0x41
            val firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagoffset
            val secoundChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagoffset
            val flag =
                (String(Character.toChars(firstChar)) + String(Character.toChars(secoundChar)))
            countryListWithEmoji.add("$countryName (${locale.country}  $flag")
        }
        return countryListWithEmoji
    }

    // date picker
    @Composable
    fun datePicker() {
        val mYear: Int
        val mMonth: Int
        val mDay: Int
        val now = Calendar.getInstance()
        mYear = now.get(Calendar.YEAR)
        mMonth = now.get(Calendar.MONTH)
        mDay = now.get(Calendar.DAY_OF_MONTH)
        now.time = Date()
        val date = remember { mutableStateOf("") }
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
//                date.value = LocalDate.now().toString()
            }, mYear, mMonth, mDay
        )

        val day1 = Calendar.getInstance()
        day1.set(Calendar.DAY_OF_MONTH, 1)
        datePickerDialog.datePicker.minDate = day1.timeInMillis
        datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                datePickerDialog.show()
            }) {
                Text(text = "Click To Open Date Picker")
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "Selected date: ${date.value}")
        }
    }
}


