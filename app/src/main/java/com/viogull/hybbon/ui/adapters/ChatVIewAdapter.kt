package com.viogull.hybbon.ui.adapters

import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.viogull.hybbon.R
import android.app.Activity
import android.widget.ArrayAdapter
import com.viogull.hybbon.data.models.ChatViewMessage



class ChatVIewAdapter(private val activity: Activity, resource: Int,
                      private val messages:
                            List<ChatViewMessage>) : ArrayAdapter<ChatViewMessage>(activity, resource, messages) {



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        val inflater = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var layoutResource = 0 // determined by view type
        val ChatBubble = getItem(position)
        val viewType = getItemViewType(position)

        if (ChatBubble!!.myMessage()) {
            layoutResource = R.layout.drw_sended_msg
        } else {
            layoutResource = R.layout.drw_received_msg
        }

        if (convertView != null) {
            holder = convertView.tag as ViewHolder
        } else {
            convertView = inflater.inflate(layoutResource, parent, false)
            holder = ViewHolder(convertView)
            convertView!!.tag = holder
        }

        //set message content
        holder.text().setText(ChatBubble!!.content)

        return convertView
    }




    override fun getViewTypeCount(): Int {
        // return the total number of view types. this value should never change
        // at runtime. Value 2 is returned because of left and right views.
        return 2
    }



    override fun getItemViewType(position: Int): Int {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2
    }






    private inner class ViewHolder(v: View) {
        private lateinit var msg: TextView

        init {
            msg = v.findViewById<View>(R.id.text_message_body) as TextView
        }

        public fun text() : TextView {
            return msg
        }
    }
}