package com.viogull.hybbon.p2p.tasks;

import android.app.ProgressDialog;

import com.viogull.hybbon.R;
import com.viogull.hybbon.Application;
import com.viogull.hybbon.p2p.utils.ISuccessFailListener;
import com.viogull.hybbon.util.Logg;

import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;

/**
 * Created by ghost on 27.12.2017.
 */

public class UserLogoutTask extends BaseProgressTask{


    static Logg LOG = new Logg("UserLogoutTask");
    public UserLogoutTask(Application context, ISuccessFailListener listener, ProgressDialog progressDialog) {
        super(context, listener, progressDialog);
    }

    @Override
    protected String[] getProgressMessages() {
        String[] progressMessages = new String[1];
        progressMessages[0] = context.getString(R.string.progress_logout_msg);
        return progressMessages;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        IH2HNode node = context.getH2HNode();
        if (node == null || !node.isConnected()) {
            LOG.log("H2HNode is null or not connected (anymore)");
            // TODO head back to the connection activity
            return false;
        }

        try {
            if (!node.getUserManager().isLoggedIn()) {
                LOG.log("Not logged in");
                return true;
            }
            LOG.log("Start logging out...");
            node.getUserManager().createLogoutProcess().execute();
            LOG.log("Successfully logged out");
            return true;
        } catch (InvalidProcessStateException | ProcessExecutionException | NoPeerConnectionException | NoSessionException e) {
            LOG.log("Cannot logout properly" + e);
            return false;
        }
    }
}