package app.helloteam.sportsbuddyapp.helperUI

import android.os.Handler
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LandingPageViewModel : ViewModel() {

    val eventList: MutableList<EventCard> = mutableListOf<EventCard>()

    private val db = Firebase.firestore

    init {
        viewModelScope.run {



        }
    }
}


