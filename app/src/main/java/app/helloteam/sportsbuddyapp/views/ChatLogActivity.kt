package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.models.MessageModel
import com.baoyz.widget.PullRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ChatLogActivity : AppCompatActivity() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var messagesList = ArrayList<MessageModel>()
    var messageAmount = 20
    val maxArchives = 60
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = intent.getStringExtra("userName")
        listenForMessages(this)

        Firebase.firestore.collection("User").document(intent.getStringExtra("member").toString())
            .get().addOnSuccessListener {
                if(!it.exists()){
                    findViewById<Button>(R.id.chatButton).isClickable = false
                    Toast.makeText(this, "This user no longer exists", Toast.LENGTH_SHORT).show()
                }
            }


        findViewById<Button>(R.id.chatButton).setOnClickListener {
            performSendMessage()
            findViewById<EditText>(R.id.messageText).setText("")
        }

        val layout = findViewById<PullRefreshLayout>(R.id.swipeRefreshLayout)
        layout.setOnRefreshListener {
            messagesList.clear()
            Firebase.firestore.collection("User_Messages_Archive").document(currentUser?.uid.toString())
                .collection("To").document(intent.getStringExtra("member").toString())
                .collection("archives").orderBy("date", Query.Direction.DESCENDING).limit(messageAmount.toLong())
                .get().addOnSuccessListener { messages ->
                    messageAmount = messageAmount + messageAmount
                    for(message in messages){

                        var m = MessageModel(message.get("id").toString(), message.get("message").toString(),
                        message.get("messageType").toString().toInt(), R.drawable.logoteamupsmall, message.get("date").toString().toLong())
                        messagesList.add(m)
                    }
                    FirebaseDatabase.getInstance().getReference("/user-messages/${currentUser?.uid}/${intent.getStringExtra("member").toString()}")
                        .get().addOnSuccessListener { realTimeMessages ->
                            for(message in realTimeMessages.children){
                                var m = MessageModel(message.child("id").value.toString(), message.child("text").value.toString(),
                                    if (message.child("fromId").value.toString() == currentUser?.uid) CustomAdapter.MESSAGE_TYPE_IN else CustomAdapter.MESSAGE_TYPE_OUT,
                                    R.drawable.logoteamupsmall, message.child("timestamp").value.toString().toLong())
                                    messagesList.add(m)
                            }
                            var sorted = messagesList.sortedBy { messagesList -> messagesList.date }
                            messagesList.clear()
                            for (sort in sorted){
                                messagesList.add(sort)
                            }
                            layout.setRefreshing(false);
                            var adapter = CustomAdapter(this, messagesList, intent.getStringExtra("member").toString())
                            var recyclerView = findViewById<RecyclerView>(R.id.recycleChat)
                            recyclerView.setAdapter(adapter)
                        }
                }
        }

    }

    private fun listenForMessages(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${currentUser?.uid}/${intent.getStringExtra("member").toString()}")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {

                        messagesList.add(
                            MessageModel(
                                chatMessage.id,
                                chatMessage.text,
                                if (chatMessage.fromId == currentUser?.uid) CustomAdapter.MESSAGE_TYPE_IN else CustomAdapter.MESSAGE_TYPE_OUT,
                                R.drawable.logoteamupsmall,
                                chatMessage.timestamp
                            )
                        )
                    var sorted = messagesList.sortedBy { messagesList -> messagesList.date }
                    messagesList.clear()
                    for (sort in sorted){
                        messagesList.add(sort)
                    }
                    if (messagesList.size > messageAmount) {
                        var chat = messagesList.get(0)
                        messagesList.removeAt(0)
                        Log.i("aaaaaaaa", "in if")
                        FirebaseDatabase.getInstance().getReference("/user-messages/${currentUser?.uid}/${intent.getStringExtra("member").toString()}").child(chat.id).removeValue()
                        Log.i("delete", chat.id)


                        val db = Firebase.firestore.collection("User_Messages_Archive")

                        db.document(currentUser?.uid.toString())
                            .collection("To").document(intent.getStringExtra("member").toString())
                            .collection("archives").whereEqualTo("id", chat.id).get().addOnSuccessListener { repeat ->
                                if (repeat.isEmpty){
                                    db.document(currentUser?.uid.toString())
                                        .collection("To").document(intent.getStringExtra("member").toString())
                                        .collection("archives").document(chat.id).set(chat)
                                }
                            }
                    }
                    Firebase.firestore.collection("User_Messages_Archive").document(currentUser?.uid.toString())
                        .collection("To").document(intent.getStringExtra("member").toString())
                        .collection("archives").get().addOnSuccessListener {
                            if (it.size() > maxArchives){
                                Firebase.firestore.collection("User_Messages_Archive").document(currentUser?.uid.toString())
                                    .collection("To").document(intent.getStringExtra("member").toString())
                                    .collection("archives").orderBy("date", Query.Direction.ASCENDING).limit((it.size() - maxArchives).toLong() )
                                    .get().addOnSuccessListener { removes ->
                                        for(remove in removes){
                                            Firebase.firestore.collection("User_Messages_Archive").document(currentUser?.uid.toString())
                                                .collection("To").document(intent.getStringExtra("member").toString())
                                                .collection("archives").document(remove.id).delete().addOnSuccessListener {
                                                    Log.i("deletinggg", "here")
                                                }
                                        }
                                    }
                            }
                        }


                }
                var adapter = CustomAdapter(context, messagesList, intent.getStringExtra("member").toString())
                var recyclerView = findViewById<RecyclerView>(R.id.recycleChat)
                recyclerView.setAdapter(adapter)
                findViewById<RecyclerView>(R.id.recycleChat).smoothScrollToPosition(messagesList.size);
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }

    class ChatMessage(val id: String, val text:String, val fromId: String, val toId: String, val timestamp: Long, var read: Boolean){
        constructor(): this("", "", "", "", -1, true)
    }

    private fun performSendMessage(){
        val text = findViewById<EditText>(R.id.messageText).text.toString()
        val fromId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val toId = intent.getStringExtra("member").toString()
        val dbFrom = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val chatMessage = ChatMessage(dbFrom.key.toString(), text, fromId, toId, System.currentTimeMillis() / 1000, true)
        dbFrom.setValue(chatMessage).addOnSuccessListener {
            Log.d("Messagee", dbFrom.key.toString())
        }

        val dbTo = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").child(dbFrom.key.toString())
        dbTo.setValue(chatMessage).addOnSuccessListener {
            Log.d("Messagee", dbFrom.key.toString())
        }

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        val latestMessageRefTo = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

        val chatMessageTo = ChatMessage(dbFrom.key.toString(), text, fromId, toId, System.currentTimeMillis() / 1000, false)
        latestMessageRef.setValue(chatMessage)
        latestMessageRefTo.setValue(chatMessageTo)

    }
}


class CustomAdapter(context: Context, list: ArrayList<MessageModel>, reciver: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val context: Context
    var list: ArrayList<MessageModel>
    private val reciver: String

    private inner class MessageInViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var image: ImageView
        fun bind(position: Int) {
            val messageModel: MessageModel = list[position]
            messageTV.setText(messageModel.message)

            if(FirebaseAuth.getInstance().currentUser?.photoUrl != null) {
                Glide.with(context).load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                    .into(image)
            }
        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            image = itemView.findViewById(R.id.profilepic)

        }
    }

    private inner class MessageOutViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var image: ImageView
        fun bind(position: Int) {
            Firebase.firestore.collection("User").document(reciver)
                .get().addOnSuccessListener { user ->
                    val messageModel: MessageModel = list[position]
                    messageTV.setText(messageModel.message)
                    if(user.get("photoUrl") != null) {
                        Glide.with(context).load(user.get("photoUrl"))
                            .into(image)
                    }
                }
        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            image = itemView.findViewById(R.id.profilepic)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MESSAGE_TYPE_IN) {
            MessageInViewHolder(
                LayoutInflater.from(context).inflate(R.layout.chat_from_row, parent, false)
            )
        } else MessageOutViewHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_to_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].messageType === MESSAGE_TYPE_IN) {
            (holder as MessageInViewHolder).bind(position)
        } else {
            (holder as MessageOutViewHolder).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].messageType
    }

    companion object {
        const val MESSAGE_TYPE_IN = 1
        const val MESSAGE_TYPE_OUT = 2
    }

    init { // you can pass other parameters in constructor
        this.context = context
        this.list = list
        this.reciver = reciver
    }
}