package com.viogull.hybbon.data.models

import java.util.*

/**
 * Introductive format for data packing
 */
class HybboMessage constructor(private val id: String, private val user_id:String ,
                               private var text: String?, private var createdAt: Date = Date())
{
    fun getId() : String {
        return id
    }

    fun getUserId() : String {
        return user_id
    }

    fun getText(): String? {
        return text
    }

    fun getCreationTime() : Date {
        return createdAt
    }
}