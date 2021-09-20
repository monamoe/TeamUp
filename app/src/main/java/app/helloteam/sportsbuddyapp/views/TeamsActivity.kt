package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import app.helloteam.sportsbuddyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

lateinit private var memberList: ArrayList<TeamsActivity.TeamDisplayer>

class TeamsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)

        val listview = findViewById<ListView>(R.id.listView)

        memberList = ArrayList()

        // populate array list with events that match the location ID of the marker selected
        Log.i("Teamssss", "i just wanna test")

        // FIREBASE MIGRATION //
        val db = Firebase.firestore
        db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("Team") //creates team inside user
            .get().addOnSuccessListener { members ->
                for (member in members){
                    Log.i("Teamssss", member.toString())
                    db.collection("User").document(member.get("userId").toString())
                        .get().addOnSuccessListener { user ->
                            val eventObj = TeamDisplayer(
                                user.id,
                                user.get("userName").toString(),
                                user.get("favouriteSport").toString()
                            )
                            memberList.add(eventObj)

                            // list view adapter
                            listview.adapter = TeamListAdapter(this)
                        }
                }
            }


        listview.setOnItemClickListener { parent, view, position, id ->
            val memberID = memberList.get(position).getID()
            val intent = Intent(this, event::class.java)
            intent.putExtra("member", memberID)
            startActivity(intent)
        }

        findViewById<Button>(R.id.addMemberButton).setOnClickListener {
            finish()
            val intent = Intent(this, TeamSearchActivity::class.java)
            startActivity(intent)
        }
    }


    // Event Array List Adapter
    internal class TeamListAdapter(context: Context) : BaseAdapter() {

        private val mContext: Context = context

        // overrides
        override fun getCount(): Int {
            return memberList.size;
        }

        override fun getItem(position: Int): Any {
            return "return override"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // render each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val lI = LayoutInflater.from(mContext)
            val rowMain = lI.inflate(R.layout.team_list_adapter_view, viewGroup, false);

            val name = rowMain.findViewById<TextView>(R.id.eventTitle)
            val sport = rowMain.findViewById<TextView>(R.id.memberSport)

            name.text = (memberList.get(position).name)
            sport.text = (memberList.get(position).favSport)

            return rowMain;
        }
    }

    // Event Displayer class ( for array list)
    class TeamDisplayer {
        var id: String = ""
        var name: String = ""
        var favSport: String = ""

        fun getID(): String {
            return this.id
        }

        // main constuctor
        constructor(id: String, name: String, favSport: String) {
            this.id = id
            this.name = name
            this.favSport = favSport
        }
    }
}

