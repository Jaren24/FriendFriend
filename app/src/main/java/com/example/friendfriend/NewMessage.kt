package com.example.friendfriend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row.view.*

class NewMessage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select Friend"
//        val adapter = GroupAdapter<GroupieViewHolder>()
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//
//        recycleViewMsg.adapter = adapter
        retrieveFriend()
    }
    companion object{
        val USER_KEY = "USER_KEY"
    }
    private fun retrieveFriend(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach{
                    val user = it.getValue(User::class.java)
                    if(user!=null)
                    adapter.add(UserItem(user))
                }
                adapter.setOnItemClickListener{ item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLog::class.java)
                   // intent.putExtra(USER_KEY, item.user.username)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                }
                recycleViewMsg.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class UserItem(val user:User): Item<GroupieViewHolder>(){
    override fun bind(GroupieViewHolder: GroupieViewHolder, position: Int) {
        GroupieViewHolder.itemView.username_tv.text = user.username
        Picasso.get().load(user.profileImageUrl).into(GroupieViewHolder.itemView.imageView_f)
    }

    override fun getLayout(): Int {
        return R.layout.user_row
    }
}

