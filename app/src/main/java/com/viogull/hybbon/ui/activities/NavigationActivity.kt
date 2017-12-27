package com.viogull.hybbon.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badge
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.badgeable.secondaryItem
import co.zsmb.materialdrawerkt.draweritems.sectionHeader
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.Badgeable
import com.mikepenz.materialdrawer.model.interfaces.Nameable

import android.widget.*
import com.viogull.hybbon.R


class NavigationActivity : AppCompatActivity() {



    private lateinit var drawer: Drawer
    private lateinit var drawerAddon: Drawer



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

               // supportActionBar?.setDisplayHomeAsUpEnabled(true)
               supportActionBar?.setHomeButtonEnabled(false)


        drawer = drawer {

            savedInstance = savedInstanceState

            primaryItem(R.string.drawer_chats) {
                icon = R.drawable.drw_chats
                badge("99")
                onClick  {  _ ->
                    startActivity(Intent(this@NavigationActivity, ChatViewActivity::class.java))
                    true
                }
            }
            primaryItem(R.string.drawer_contacts) { icon = R.drawable.drw_contacts }


            primaryItem(R.string.drawer_files) {
                icon = R.drawable.drw_files
                badge("6")
            }


            sectionHeader(R.string.drawer_section_title)
            secondaryItem(R.string.drawer_about) { icon = R.drawable.drw_about }
            secondaryItem(R.string.drawer_faq) { icon = R.drawable.drw_faq }
            secondaryItem(R.string.drawer_help) {
                icon = R.drawable.drv_help
                badge("12")
            }

            fun logAndShow(message: String) {
                android.util.Log.d("DRAWER_EVENT", message)
            }

            onOpened { drawerView ->
                when (drawerView) {
                    drawer.slider -> logAndShow("opened left")
                    drawerAddon.slider -> logAndShow("opened right")
                }
            }
            onClosed { drawerView ->
                when (drawerView) {
                    drawer.slider -> logAndShow("closed left")
                    drawerAddon.slider -> logAndShow("closed right")
                }
            }

            onItemClick { _, _, drawerItem ->
                if (drawerItem is Nameable<*>) {
                    toast(drawerItem.name.getText(this@NavigationActivity))
                }
                if (drawerItem is Badgeable<*>) {
                    drawerItem.badge?.let {
                        drawerItem.withBadge("${it.toString().toInt() + 1}")
                        drawer.updateItem(drawerItem)
                    }
                }
                false
            }

            onItemLongClick { _, _, drawerItem ->
                if (drawerItem is SecondaryDrawerItem) {
                    toast(drawerItem.name.getText(this@NavigationActivity))
                }
                false
            }
        }




        drawerAddon = drawer {
            savedInstance = savedInstanceState
            footerViewRes = R.layout.footer
            displayBelowStatusBar = true

            // Specific things that are important for setting up multi drawer
            primaryDrawer = drawer
            gravity = Gravity.END

            primaryItem(R.string.drawer_ad_netconf) { icon = R.drawable.drw_network }
            primaryItem(R.string.drawer_ad_encryption) { icon = R.drawable.drw_encryptions }
            primaryItem(R.string.drawer_ad_addons) { icon = R.drawable.drw_addons }
            primaryItem(R.string.drawer_ad_themes) { icon = R.drawable.drw_themes }


            onItemClick { _, _, drawerItem ->
                if (drawerItem is Nameable<*>) {
                    toast(drawerItem.name.getText(this@NavigationActivity))
                }
                false
            }
        }




    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                android.R.id.home -> {
                    onBackPressed(); true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onSaveInstanceState(outState: Bundle?) {
        drawer.saveInstanceState(outState)
        drawerAddon.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen)
            drawer.closeDrawer()
        else
            super.onBackPressed()
    }


    fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }




}
