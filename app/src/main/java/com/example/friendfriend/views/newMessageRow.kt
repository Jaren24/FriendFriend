package com.example.friendfriend.views

import com.example.friendfriend.R
import com.example.friendfriend.User
import com.example.friendfriend.chatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.new_msg_row.view.*

class newMessageRow(val msg: chatMessage): Item<GroupieViewHolder>(){
    var userFriend: User? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.newMsh_msg.text = msg.text
        val friendID: String
        if(msg.fromID == FirebaseAuth.getInstance().uid)
            friendID = msg.toID
        else
            friendID = msg.fromID
        val ref = FirebaseDatabase.getInstance().getReference("/users/$friendID")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                userFriend = p0.getValue(User::class.java)
                viewHolder.itemView.newMsg_user.text = userFriend?.username
                val targetImg = viewHolder.itemView.newMsg_img
                Picasso.get().load(userFriend?.profileImageUrl).into(targetImg)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.new_msg_row
    }
}