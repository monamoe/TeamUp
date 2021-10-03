package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import app.helloteam.sportsbuddyapp.R
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LatestMessagesActivity : AppCompatActivity() {
    var messagesList = ArrayList<LatestAdapter.MessageDisplayer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        var adapter = LatestAdapter(messagesList, this)

        listenForLatestMessages(this, adapter)
        if(messagesList.size > 0) {
        }
        findViewById<FloatingActionButton>(R.id.newMessage).setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        var recyclerView = findViewById<RecyclerView>(R.id.myList)
        var context = this
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                val position = viewHolder.adapterPosition
                adapter.notifyDataSetChanged()
                val ref =  FirebaseDatabase.getInstance()
                ref.getReference("/latest-messages/${FirebaseAuth.getInstance().currentUser?.uid}").child(messagesList.get(position).memberId).removeValue()
                ref.getReference("/user-messages/${FirebaseAuth.getInstance().currentUser?.uid}").child(messagesList.get(position).memberId).removeValue()
                Firebase.firestore.collection("User_Messages_Archive").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("To").document(messagesList.get(position).memberId).collection("archives").get()
                    .addOnSuccessListener { messages ->
                        for (message in messages){
                            Firebase.firestore.collection("User_Messages_Archive").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                .collection("To").document(messagesList.get(position).memberId).collection("archives")
                                .document(message.id).delete()
                        }

                    }
                messagesList.removeAt(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun listenForLatestMessages(context: Context, adapter: LatestAdapter){
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$currentUser")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatLogActivity.ChatMessage::class.java)
                var otherUser = chatMessage?.fromId.toString()
                if(otherUser == currentUser.toString()){
                    otherUser = chatMessage?.toId.toString()
                }
                    Firebase.firestore.collection("User").document(otherUser)
                        .get().addOnSuccessListener { user ->
                            var position = 0
                            for (i in 0..messagesList.size-1){
                                if (user.id == messagesList.get(i).memberId){
                                    position = i
                                }
                            }
                            if (chatMessage != null) {
                                messagesList.set(position,
                                    LatestAdapter.MessageDisplayer(
                                        chatMessage.id,
                                        otherUser,
                                        chatMessage.text,
                                        chatMessage.timestamp.toString(),
                                        user.get("userName").toString()
                                    )
                                )
                                var sorted = messagesList.sortedByDescending { messagesList -> messagesList.time }
                                messagesList.clear()
                                for (sort in sorted){
                                    messagesList.add(sort)
                                }
                                var recyclerView = findViewById<RecyclerView>(R.id.myList)
                                recyclerView.setAdapter(adapter);
                            }
                        }

            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatLogActivity.ChatMessage::class.java)
                var otherUser = chatMessage?.fromId.toString()
                if(otherUser == currentUser.toString()){
                    otherUser = chatMessage?.toId.toString()
                }

                Firebase.firestore.collection("User").document(otherUser.toString())
                    .get().addOnSuccessListener { user ->
                        if (chatMessage != null) {
                            messagesList.add(
                                LatestAdapter.MessageDisplayer(
                                    chatMessage.id,
                                    otherUser.toString(),
                                    chatMessage.text,
                                    chatMessage.timestamp.toString(),
                                    user.get("userName").toString()
                                )
                            )
                            var sorted = messagesList.sortedByDescending { messagesList -> messagesList.time }
                            messagesList.clear()
                            for (sort in sorted){
                                messagesList.add(sort)
                            }
                            var recyclerView = findViewById<RecyclerView>(R.id.myList);
                            recyclerView.setAdapter(adapter);
                        }
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
}

class LatestAdapter(private var dataSet: ArrayList< MessageDisplayer>, context: Context) :
    RecyclerView.Adapter<LatestAdapter.ViewHolder>() {
    private var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val message: TextView
        val profile: ImageView

        init {
            Log.i("Hellooooo", "test")

            // Define click listener for the ViewHolder's View.
            userName = view.findViewById(R.id.message_text)
            message = view.findViewById(R.id.time_text)
            profile = view.findViewById(R.id.profilepic)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.latest_message_row, viewGroup, false)
        Log.i("Hellooooo", "again")

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.userName.text = dataSet.get(position).name
        viewHolder.message.text = dataSet.get(position).text
        Firebase.firestore.collection("User").document(dataSet.get(position).memberId)
            .get().addOnSuccessListener { photo ->
                if(photo.get("photoUrl") != null) {
                    Glide.with(context).load(photo.get("photoUrl")).into(viewHolder.profile)
                }
                }

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, ChatLogActivity::class.java)
            intent.putExtra("member", dataSet.get(position).memberId)
            intent.putExtra("userName", dataSet.get(position).name)
            context.startActivity(intent)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
    class MessageDisplayer {
        var id: String = ""
        var memberId: String = ""
        var text: String = ""
        var time: String = ""
        var name: String = ""

        fun getID(): String {
            return this.id
        }

        fun getMemberID(): String {
            return this.memberId
        }

        // main constuctor
        constructor(id: String, memberId: String, text: String, favSport: String, name: String) {
            this.id = id
            this.memberId = memberId
            this.text = text
            this.time = favSport
            this.name = name
        }
    }
    init { // you can pass other parameters in constructor
        this.context = context
    }
    }
