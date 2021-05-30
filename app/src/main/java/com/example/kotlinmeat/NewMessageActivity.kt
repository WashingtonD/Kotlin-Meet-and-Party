package com.example.kotlinmeat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityNewMessageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_row_new_message.view.*


private lateinit var binding: ActivityNewMessageBinding

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Select User"


        fetchUsers()

    }


    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref  = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

             snapshot.children.forEach{
                 Log.d("NewMessage",it.toString())
                 val user = it.getValue(User::class.java)
                 if(user != null){
                  adapter.add(UserItem(user)) }
             }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    Log.d("UserItem",userItem.user.getName())
                    val intent = Intent(this@NewMessageActivity,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user.getName())
                    intent.putExtra("id",userItem.user.getId())
                    intent.putExtra("image",userItem.user.getImageLink())
                    startActivity(intent)
                    finish()
                }
                
                
                binding.recyclerviewNewmsg.adapter = adapter
            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.usernameNew.text = user.getName()
        Picasso.with(viewHolder.itemView.usernameNew.context).load(user.getImageLink()).fit().centerInside().into(viewHolder.itemView.userPhotoMessage)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}