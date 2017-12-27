package com.viogull.hybbon.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.viogull.hybbon.R
import com.viogull.hybbon.data.models.ChatViewMessage
import com.viogull.hybbon.ui.adapters.ChatVIewAdapter
import kotlinx.android.synthetic.main.activity_chat.*

class ChatViewActivity : AppCompatActivity() {

    lateinit var  listView: ListView
    lateinit var btnSend: View
    lateinit var  editText: EditText
    var myMessage = true
    lateinit var chatBubbles: ArrayList<ChatViewMessage>
    lateinit var  adapter: ArrayAdapter<ChatViewMessage>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        chatBubbles =  ArrayList<ChatViewMessage>();


        listView = list_msg
        btnSend = btn_chat_send
        editText = msg_type


        //set ListView adapter first
        adapter = ChatVIewAdapter(this, R.layout.drw_received_msg, chatBubbles);
        listView.setAdapter(adapter);

        //event for button SEND
        btnSend.setOnClickListener {
            if (editText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Input  text...", Toast.LENGTH_SHORT).show();
            } else {
                //add message to list
                val ChatBubble = ChatViewMessage(editText.getText().toString(), myMessage);
                chatBubbles.add(ChatBubble)
                adapter.notifyDataSetChanged();
                editText.setText("");
                if (myMessage) {
                    myMessage = false;
                } else {
                    myMessage = true;
                }
            }

        }

    }
}
