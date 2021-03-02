package app.helloteam.sportsbuddyapp

import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.parse.ParseUser
import java.util.*


class CreateEvent : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    var hour:Int =0
    var min:Int=0
    var yearPicked:Int=Calendar.getInstance().get(Calendar.YEAR)
    var monthPicked:Int=(Calendar.getInstance().get(Calendar.MONTH))+1
    var dayPicked:Int=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    private val context:Context=this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val sportType= findViewById<RadioGroup>(R.id.SportType)
        val address = findViewById<EditText>(R.id.AddressTxt)
        val createBtn = findViewById<Button>(R.id.CreateBtn)
        var sportSelection : SportTypes =SportTypes.NONE
        sportType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==R.id.SoccerBtn){
                sportSelection= SportTypes.SOCCER
            }
            if (checkedId==R.id.BasketballBtn){
                sportSelection=SportTypes.BASKETBALL
            }
            if(checkedId==R.id.HockeyBtn){
                sportSelection=SportTypes.BallHockey
            }
        }

        val timeBtn = findViewById<Button>(R.id.TimeBtn)
        timeBtn.setOnClickListener {
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }
        if (hour!=0){
            timeBtn.setText("$hour:$min")
        }

        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)

        ) { view, year, month, day ->
            val month = month + 1
            dayPicked=day
            yearPicked=year
            monthPicked=month
        }

        createBtn.setOnClickListener {
            if (!address.text.toString().equals("")&& sportSelection!=SportTypes.NONE&&hour!=0){
                var se = SportEvents(
                    sportSelection, address.text.toString(), ParseUser.getCurrentUser().username,
                    hour, min, yearPicked, monthPicked, dayPicked
                );
                ParseCode.EventCreation(se)
            }else{
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val timeBtn = findViewById<Button>(R.id.TimeBtn)
        hour = hourOfDay
        min=minute
        timeBtn.setText("$hour:$min")
    }
}
