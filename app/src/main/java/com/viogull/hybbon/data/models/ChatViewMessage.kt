package com.viogull.hybbon.data.models



class ChatViewMessage(val content: String, private val myMessage: Boolean) {

    fun myMessage(): Boolean {
        return myMessage
    }
}
