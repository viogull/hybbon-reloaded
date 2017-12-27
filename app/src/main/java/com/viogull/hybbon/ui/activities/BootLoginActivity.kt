package com.viogull.hybbon.ui.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.transition.Slide
import android.view.Gravity
import com.viogull.hybbon.R
import com.viogull.hybbon.ui.fragments.ConnectFragment
import com.viogull.hybbon.ui.fragments.LoginFragment


open class BootLoginActivity : FragmentActivity() {


    companion object Switch {

        internal fun openConnectScreen(fm : FragmentManager) {
            val ft = fm.beginTransaction()
            val fragment = ConnectFragment.newInstance()
            fragment.enterTransition = Slide(Gravity.RIGHT).setDuration(1500)
            fragment.exitTransition = Slide(Gravity.LEFT).setDuration(1500)
            ft.replace(R.id.connect_login_holder, fragment).commit()
        }

        internal fun openLoginScreen(fm : FragmentManager) {
            val ft = fm.beginTransaction()
            val fragment = LoginFragment.newInstance()
            fragment.enterTransition = Slide(Gravity.LEFT).setDuration(1500)
            fragment.exitTransition = Slide(Gravity.RIGHT).setDuration(1500)
            ft.replace(R.id.connect_login_holder, fragment).commit()
        }
    }








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot_login)
        openConnectScreen(supportFragmentManager)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }



}