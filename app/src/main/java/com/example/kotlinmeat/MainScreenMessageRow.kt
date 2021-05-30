package com.example.kotlinmeat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.mainscreen_row.view.*


class MainScreenMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_textView_MainScreen.text = chatMessage.text

        val chatPartnerId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid)
        {
            chatPartnerId = chatMessage.toId
        }
        else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                //Log.d("USERSSS",user!!.getName())
                viewHolder.itemView.username_message_MainScreen.text = chatPartnerUser?.getName() + " " + chatPartnerUser?.getSurname()


                Picasso.with(viewHolder.itemView.circleImageView_MainScreen.context).load(chatPartnerUser?.getImageLink()).centerInside().fit().into(viewHolder.itemView.circleImageView_MainScreen)
            }
        })

    }
    override fun getLayout(): Int {
        return R.layout.mainscreen_row
    }
}