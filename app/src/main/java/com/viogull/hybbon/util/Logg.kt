package com.viogull.hybbon.util

/**
 * Created by ghost on 27.12.2017.
 */
class Logg(tag: String) {
    val ttag: String = "DefaultTag"
    init {
        val ttag = tag
    }
    fun log(msg: String) {
        android.util.Log.d(ttag, msg)
    }
}