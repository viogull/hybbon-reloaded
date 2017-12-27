package com.viogull.hybbon.ui.fragments

/**
 * Created by ghost on 27.12.2017.
 */
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.dd.processbutton.iml.ActionProcessButton
import com.fujiyuu75.sequent.Animation
import com.fujiyuu75.sequent.Direction
import com.fujiyuu75.sequent.Sequent
import com.viogull.hybbon.ui.activities.BootLoginActivity
import com.viogull.hybbon.ui.activities.ChatViewActivity
import com.viogull.hybbon.R
import com.viogull.hybbon.Application
import com.viogull.hybbon.p2p.tasks.UserLoginTask
import com.viogull.hybbon.p2p.utils.ISuccessFailListener
import com.viogull.hybbon.util.Logg
import kotlinx.android.synthetic.main.login_fragment.*

import org.hive2hive.core.exceptions.NoPeerConnectionException







/**
 * Created by rsv on 26.07.2017.
 */
class LoginFragment : Fragment() {

    companion object LoginPeer {


        internal lateinit var application: Application


        lateinit var usernameField : EditText
        lateinit var passwordField : EditText
        lateinit var pinField : EditText
        lateinit var signBtn : ActionProcessButton


        lateinit var saveCredsToggle : LottieAnimationView
        private var toggle_status = false

        lateinit var relLayout : RelativeLayout

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }


        private val STORE_CREDENTIALS = "store"
        private val STORED_USER_ID = "userid"
        private val STORED_PASSWORD = "pin"
        private val STORED_PIN = "password"

        fun StartMainActivity(context : Context) {
            val intent = Intent(context, ChatViewActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }

        public fun storeCredentials(username: String, password: String, pin: String) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(application)
            if (toggle_status) {
                // store the credentials
                prefs.edit().putBoolean(STORE_CREDENTIALS, true).putString(STORED_USER_ID, username).putString(STORED_PASSWORD, password).putString(STORED_PIN, pin).apply()
            } else {
                // erase existing credentials
                prefs.edit().putBoolean(STORE_CREDENTIALS, false).remove(STORED_USER_ID).remove(STORED_PASSWORD).remove(STORED_PIN).apply()
            }
        }
    }


    val log = Logg("LoginFragment.kt  |  ")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view!!, savedInstanceState)

        application = activity!!.applicationContext as Application

        try {
            if (application.getH2HNode()!!.equals(null) || application.getH2HNode()!!.isConnected) {
                BootLoginActivity.Switch.openConnectScreen(fragmentManager!!)
                return
            }
        }
        catch (ex : Exception) {
            log.log("exceptino throwed: $ex")
        }
        usernameField = username1
        passwordField = password
        pinField = pin
        signBtn = sign_in_up_button
        relLayout = login_frag_rr
        saveCredsToggle = saveCredentialsSwitch
        saveCredsToggle.setAnimation("json/toggle.json")
        saveCredsToggle.loop(false)

        saveCredsToggle.setOnClickListener {
            if(toggle_status) {
                saveCredsToggle.progress = 0f
                toggle_status = true }
            else { saveCredsToggle.progress = 1f
                toggle_status = false }

            saveCredsToggle.playAnimation()
        }


        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity)
        usernameField.setText(sharedPrefs.getString(STORED_USER_ID, ""), TextView.BufferType.EDITABLE);
        passwordField.setText(sharedPrefs.getString(STORED_PASSWORD, ""), TextView.BufferType.EDITABLE);
        pinField.setText(sharedPrefs.getString(STORED_PIN, ""), TextView.BufferType.EDITABLE);

        signBtn.setOnClickListener {
            Sequent.origin(relLayout).duration(1500).offset(200).flow(Direction.BACKWARD)
                    .anim(activity, Animation.FADE_IN_UP).start()
            BootLoginActivity.openConnectScreen(activity!!.supportFragmentManager)

        }

        Sequent.origin(relLayout).duration(2000).offset(300).flow(Direction.FORWARD)
                .anim(activity, Animation.FADE_IN_DOWN).start()
    }



    /**
     *
     *
     */





    fun login(view: View) {
        if (application.getH2HNode() == null) {
            return
        }
        try {
            if (application.getH2HNode()!!.userManager.isLoggedIn) {
                // already logged in
                StartMainActivity()
                return
            }
        } catch (e: NoPeerConnectionException) {
            log.log("Cannot determine the login state $e ")
        }

        // Reset errors.
        usernameField.setError(null)
        passwordField.setError(null)

        // Store values at the time of the login attempt.
        val username = usernameField.getText().toString()
        val password = passwordField.getText().toString()
        val pin = pinField.getText().toString()

        var cancel = false
        var focusView: View? = null


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password must be valid")
            focusView = passwordField
            cancel = true
        }

        // Check for a valid pin, if the user entered one.
        if (TextUtils.isEmpty(pin)) {
            pinField.setError("Pin is required")
            focusView = pinField
            cancel = true
        }

        // Check for a valid username1 address.
        if (TextUtils.isEmpty(username)) {
            usernameField.setError("Username must be valid")
            focusView = usernameField
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            // create the progress dialog
            val dialog = ProgressDialog(activity)

            signBtn.setMode(ActionProcessButton.Mode.PROGRESS)
            signBtn.progress = 1
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            val loginTask = UserLoginTask(application, username, password,
                    pin, LoginListener(username, password, pin, this.context!!),dialog)

            loginTask.execute(null as Void?)
        }
    }

    fun StartMainActivity() {
        val intent = Intent(activity!!.applicationContext, ChatViewActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        startActivity(intent)
    }



    class LoginListener(private val username: String,
                        private val password: String,
                        private val pin: String, private val context: Context) : ISuccessFailListener {

         override fun onSuccess() {
            storeCredentials(username, password, pin)
            StartMainActivity(context)
        }

         override fun onFail() {
            passwordField.setError("False credentials")
            passwordField.requestFocus()
        }
    }



}