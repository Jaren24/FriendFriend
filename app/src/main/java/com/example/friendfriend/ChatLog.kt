package com.example.friendfriend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.from_chat.view.*
import kotlinx.android.synthetic.main.from_chat.view.textView
import kotlinx.android.synthetic.main.to_chat.view.*

class ChatLog : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        chatLogRecycle.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessage.USER_KEY)
        supportActionBar?.title = toUser?.username

//        val adapter = GroupAdapter<GroupieViewHolder>()
//
//        adapter.add(ChatFromItem("haha"))
//        adapter.add(ChatToItem("blabla"))
//        adapter.add(ChatFromItem("lalal"))
//        adapter.add(ChatFromItem("dadadad"))
//        adapter.add(ChatToItem("damnnnnnnnnnnnn"))
//        chatLogRecycle.adapter = adapter

        sendBtn.setOnClickListener {
            Log.d(TAG, "Attempt to send message.")
            sendMessage()
        }
        messageListen()}
    private fun messageListen(){
        val fromID = FirebaseAuth.getInstance().uid
        val toID = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(chatMessage::class.java)
                if(chatMessage!= null) {
                    if(chatMessage.fromID == FirebaseAuth.getInstance().uid) {
                        val currentUser = ChatRoom.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    }
                    else
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))

                    }
                chatLogRecycle.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun sendMessage(){
        val text =  messageText.text.toString()

        val fromID = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessage.USER_KEY)
        val toID = user.uid

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()

        val chatMessage = chatMessage(reference.key!!,text,fromID!!,toID,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage).addOnSuccessListener {
            Log.d(TAG, "Saved our chat message: ${reference.key}")
            messageText.text.clear()
            chatLogRecycle.scrollToPosition(adapter.itemCount - 1)
        }

        toReference.setValue(chatMessage)

        val latest = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID/$toID")
        latest.setValue(chatMessage)

        val latestTo = FirebaseDatabase.getInstance().getReference("/latest-messages/$toID/$fromID")
        latestTo.setValue(chatMessage)
    }
}

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
        val uri = user.profileImageUrl
        val target = viewHolder.itemView.imageView_from
        Picasso.get().load(uri).into(target)
    }

    override fun getLayout(): Int {
        return R.layout.from_chat
    }
}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
        val uri = user.profileImageUrl
        val target = viewHolder.itemView.imageView_to
        Picasso.get().load(uri).into(target)
    }

    override fun getLayout(): Int {
        return R.layout.to_chat
    }
}

