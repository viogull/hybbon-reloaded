package com.viogull.hybbon.p2p.tasks;

import android.app.ProgressDialog;

import com.viogull.hybbon.R;
import com.viogull.hybbon.Application;
import com.viogull.hybbon.p2p.utils.AndroidFileAgent;
import com.viogull.hybbon.p2p.utils.ISuccessFailListener;
import com.viogull.hybbon.util.Logg;

import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.api.interfaces.IUserManager;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;

/**
 * Created by ghost on 27.12.2017.
 */

public class UserLoginTask extends BaseProgressTask {


    static Logg LOG = new Logg("UserLoginTask");
    private final String username;
    private final String password;
    private final String pin;

    public UserLoginTask(Application context, String username, String password, String pin, ISuccessFailListener listener, ProgressDialog progressDialog) {
        super(context, listener, progressDialog);
        this.username = username;
        this.password = password;
        this.pin = pin;
    }

    @Override
    protected String[] getProgressMessages() {
        String[] progressMessages = new String[4];
        progressMessages[0] = context.getString(R.string.progress_login_encrypt);
        progressMessages[1] = context.getString(R.string.progress_login_register_check);
        progressMessages[2] = context.getString(R.string.progress_login_register);
        progressMessages[3] = context.getString(R.string.progress_login_login);
        return progressMessages;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        IH2HNode node = context.getH2HNode();
        if (node == null || node.isConnected()) {
            LOG.log("H2HNode is null or not connected (anymore)");
            // TODO head back to the connection activity
            return false;
        }

        IUserManager userManager = node.getUserManager();

        // create credentials here (takes some time)
        publishProgress(0);
        UserCredentials credentials = new UserCredentials(username, password, pin);

        try {
            LOG.log("Check if user {} is already registered." + username);
            publishProgress(1);
            if (!userManager.isRegistered(username)) {
                LOG.log("Start registering user {}." +username);
                publishProgress(2);
                userManager.createRegisterProcess(credentials).execute();
                LOG.log("User {} successfully registered." + username);
            }
        } catch (NoPeerConnectionException | ProcessExecutionException | InvalidProcessStateException e) {
            LOG.log("Cannot check if user registered or cannot register user {}" +  username + e);
            return false;
        }

        try {
            LOG.log("Start logging in user {}" + username);
            publishProgress(3);
            AndroidFileAgent fileAgent = new AndroidFileAgent(context, username);
            userManager.createLoginProcess(credentials, fileAgent).execute();
            LOG.log("User {} successfully logged in" + username);
            context.setUserID(username);
            // Starting publish and update locations
            /*
            H2HUserManager h2HUserManager = (H2HUserManager) userManager;
            NetworkManager manager = h2HUserManager.getNetworkManager();
            LocationsManager locationsManager = manager.getSession().getLocationsManager();

            manager.getDataManager().put(new Parameters().setContentKey(H2HConstants.USER_LOCATIONS)
                    .setLocationKey(manager.getUserId());
            */

            return true;
        } catch (NoPeerConnectionException | ProcessExecutionException | InvalidProcessStateException e) {
            LOG.log("Cannot login user {}" + credentials.getUserId() +  e);
            return false;
        }
        /*
        catch (NoSessionException ex)
        {
            LOG.equals("Cannot work with locations...Err --> " + ex.getMessage());
            return false;
        }*/
    }
}