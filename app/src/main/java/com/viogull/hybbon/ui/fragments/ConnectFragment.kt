package com.viogull.hybbon.ui.fragments

/**
 * Created by ghost on 27.12.2017.
 */
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.lottie.LottieAnimationView
import com.dd.processbutton.iml.ActionProcessButton
import com.fujiyuu75.sequent.Animation
import com.fujiyuu75.sequent.Direction
import com.fujiyuu75.sequent.Sequent
import com.viogull.hybbon.ui.activities.BootLoginActivity
import com.viogull.hybbon.R
import com.viogull.hybbon.Application
import com.viogull.hybbon.p2p.tasks.ConnectionSetupTask
import com.viogull.hybbon.p2p.tasks.DisconnectTask
import com.viogull.hybbon.p2p.utils.ApplicationHelper
import com.viogull.hybbon.p2p.utils.ConnectionMode
import com.viogull.hybbon.p2p.utils.ISuccessFailListener
import com.viogull.hybbon.p2p.utils.RelayMode
import com.viogull.hybbon.util.Const
import com.viogull.hybbon.util.Logg
import com.viogull.hybbon.util.Toaster

import kotlinx.android.synthetic.main.connect_fragment.*


/**
 * Created by rsv on 26.07.2017.
 */
class ConnectFragment : Fragment() {


    lateinit var loaderAnim : LottieAnimationView
    lateinit var connectBtn : ActionProcessButton
    lateinit var titleText : TextView
    lateinit var relLayout : RelativeLayout
    lateinit var textAdditional : TextView
    lateinit var application: Application
    val log = Logg("Bootstrap[ConnectFragment.kt] ")

    var address = "ion.viogull.info"
    var port = 4662






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        return inflater.inflate(R.layout.connect_fragment, container, false)
    }







    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        application = context as Application

        loaderAnim = connectfragment_loader
        connectBtn = connect_button
        titleText = textView3
        relLayout = connect_frag_rr
        textAdditional = text145

        loaderAnim.setAnimation("json/load_space.json")
        loaderAnim.loop(true)
        loaderAnim.setSpeed(0.5f)
        loaderAnim.playAnimation()




        titleText.setOnClickListener{
            // hidden tips to connect
            MaterialDialog.Builder(application.applicationContext)
                    .title("Enter address of bootstrap-node: ")
                    .content("Address:")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("", "ion.viogull.info",
                            MaterialDialog.InputCallback { dialog, input ->
                                if (!input.isNullOrEmpty())
                                    address = input.toString()
                                dialog.cancel()
                            })
                    .show()
        }

        connectBtn.setOnClickListener {
            connect_disconnect()
            Sequent.origin(relLayout).duration(1500).offset(150).flow(Direction.BACKWARD)
                    .anim(activity, Animation.FADE_IN_UP).start()
            BootLoginActivity.openLoginScreen(fragmentManager!!)
        }



        Sequent.origin(relLayout).duration(2000).offset(300).flow(Direction.FORWARD)
                .anim(activity, Animation.FADE_IN_DOWN).start()



    }

    fun isConnected(): Boolean {
        return (application.getH2HNode()) != null && application.getH2HNode()!!.isConnected()
    }


    fun connect_disconnect()
    {
        log.log(" start_bootstrap_connection() started...")


        if(isConnected()) {
            val mode = ApplicationHelper.getConnectionMode(activity)
            if (mode == ConnectionMode.OFFLINE) {
                Toaster(activity!!).showBaseToast("No network")
                return
            }


            connectBtn.progress = 1
            textAdditional.text = "Bootstraping..."
            log.log("Connection on the peer...")
            val connectionTask = ConnectionSetupTask(address, port, RelayMode.TCP, 11, application,
                    ConnectListener(), ProgressDialog(activity))

            loaderAnim.setAnimation("json/loading.json")
            loaderAnim.loop(true)
            loaderAnim.playAnimation()

            loaderAnim.setOnClickListener {
                if (connectionTask != null)
                    connectionTask.cancel(true)
            }

            connectionTask.execute()

        }
        else
        {
            textAdditional.text = "Can`t to connect..."
            log.log("disconnectiong the peer")
            val di = ProgressDialog(activity)
            di.hide()
            val task = DisconnectTask(application, DisconnectListener(), di)
            task.execute()
        }

    }












    private inner class ConnectListener : ISuccessFailListener {

        override fun onSuccess() {
            log.log("Successfully connected this node")
            var prefs = activity!!.getPreferences(Context.MODE_PRIVATE)
            Toaster(activity!!).showBaseToast("Connected" )
            prefs.edit().putString(Const.PREF_BOOTSTRAP_IP, address)
                    .putString(Const.PREF_BOOTSTRAP_PORT, Integer.toString(port))
                    .apply()
            connect_button.progress = 100

            BootLoginActivity.Switch.openLoginScreen(fragmentManager!!)
        }

        override fun onFail() {
            Toaster(activity!!).showBaseToast("Disconnected")
            connect_button.progress = -1
        }
    }

    private inner class DisconnectListener : ISuccessFailListener {
        override fun onSuccess() {
        }

        override fun onFail() {
        }
    }


    inner class ConnectionChangeListener(private val application: Application) : BroadcastReceiver() {


        override  fun onReceive(context: Context, intent: Intent) {
            val mode = ApplicationHelper.getConnectionMode(context)
            if (this@ConnectFragment.application.last_mode() === mode) {
                log.log("Mode did not change, is still {}" + mode)
                return
            } else if (application.getH2HNode() == null || !application.getH2HNode()!!.isConnected()) {
                log.log("Connectivity mode did change to {} but node is not connected" + mode)
                return
            }

            log.log("Network connection changed to {}" + mode)

            // TODO take actions (reconnect node)

            application.setRelayMode(relayMode = RelayMode.TCP)
        }
    }
    companion object {

        fun newInstance(): ConnectFragment {
            return  ConnectFragment()

        }
    }
}