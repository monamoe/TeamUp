package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.models.MessageModel
import java.text.DateFormat


class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = intent.getStringExtra("userName")
        val messagesList: ArrayList<MessageModel> = ArrayList()
        for (i in 0..9) {
            messagesList.add(
                MessageModel(
                    "Hi",
                    if (i % 2 == 0) CustomAdapter.MESSAGE_TYPE_IN else CustomAdapter.MESSAGE_TYPE_OUT
                )
            )
        }
        val adapter = CustomAdapter(this, messagesList)

        var recyclerView = findViewById<RecyclerView>(R.id.recycleChat);
        recyclerView.setAdapter(adapter);

    }
}


internal class CustomAdapter(context: Context, list: ArrayList<MessageModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val context: Context
    var list: ArrayList<MessageModel>

    private inner class MessageInViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView
        fun bind(position: Int) {
            val messageModel: MessageModel = list[position]
            messageTV.setText(messageModel.message)
            dateTV.setText(
                DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime)
            )
        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            dateTV = itemView.findViewById(R.id.date_text)
        }
    }

    private inner class MessageOutViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView
        fun bind(position: Int) {
            val messageModel: MessageModel = list[position]
            messageTV.setText(messageModel.message)
            dateTV.setText(
                DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime)
            )
        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            dateTV = itemView.findViewById(R.id.date_text)
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
    }
}