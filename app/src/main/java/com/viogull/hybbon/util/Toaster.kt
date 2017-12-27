package com.viogull.hybbon.util





class Toaster(private var context: android.content.Context) {
    fun showBaseToast(message: String): Unit {
        com.muddzdev.styleabletoastlibrary.StyleableToast.Builder(context)
                .text(message)
                .cornerRadius(5)
                .duration(android.widget.Toast.LENGTH_LONG)
                .spinIcon()
                .textColor(android.graphics.Color.WHITE)
                .backgroundColor(android.graphics.Color.parseColor("#33109c"))
                .build().show()
    }
}