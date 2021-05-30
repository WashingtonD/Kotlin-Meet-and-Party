package com.example.kotlinmeat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmeat.databinding.ActivityChatLogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

private lateinit var binding: ActivityChatLogBinding




class ChatLogActivity : AppCompatActivity() {
    companion object{
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recycler_view_messages.adapter = adapter

        val userName = intent.getStringExtra(NewMessageActivity.USER_KEY)


        supportActionBar?.title = userName

        //setupDummyData()
        listenForMessages()



        binding.buttonSendMessage.setOnClickListener {
            Log.d(TAG,"Attemp to send message")
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val toId = intent.getStringExtra("id")
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                Log.d(TAG, chatMessage!!.text)

                if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                    val imagelink = MainScreenActivity.user.getImageLink()
                    adapter.add(ChatFromItem(chatMessage.text, imagelink))

                } else {
                    val link = intent.getStringExtra("image")
                    if (link != null) {
                        adapter.add(ChatToItem(chatMessage.text, link))
                    }
                }
                binding.recyclerViewMessages.scrollToPosition(adapter.itemCount - 1)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }



    private fun performSendMessage() {
        val text =binding.editTextMessage.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getStringExtra("id")
        if(fromId == null) return

            //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!,text,fromId,toId!!, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message: ${reference.key}")
                binding.editTextMessage.text.clear()
                binding.recyclerViewMessages.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }


}

class ChatFromItem(val text: String,val url: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        val targerView = viewHolder.itemView.circleImageViewFrom
        Picasso.with(targerView.context).load(url).fit().centerInside().into(targerView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val url: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text =  text

        //Loading image
        val targerView = viewHolder.itemView.circleImageViewTo
        Picasso.with(targerView.context).load(url).fit().centerInside().into(targerView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}