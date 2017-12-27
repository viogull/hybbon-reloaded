package com.viogull.hybbon.p2p.tasks;

import android.app.ProgressDialog;

import com.viogull.hybbon.R;
import com.viogull.hybbon.Application;
import com.viogull.hybbon.p2p.utils.ISuccessFailListener;
import com.viogull.hybbon.util.Logg;

import org.hive2hive.core.api.interfaces.IH2HNode;

/**
 * Created by ghost on 27.12.2017.
 */

public class DisconnectTask extends BaseProgressTask {


    static Logg LOG = new Logg("DisconnectTask");


    public DisconnectTask(Application context, ISuccessFailListener listener, ProgressDialog progressDialog) {
        super(context, listener, progressDialog);
    }

    @Override
    protected String[] getProgressMessages() {
        return new String[]{context.getString(R.string.progress_disconnect_shutdown)};
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        IH2HNode node = context.getH2HNode();
        if (node != null && context.getH2HNode().isConnected()) {
            LOG.log("Start disconnecting the peer...");
            boolean shutdown = context.getH2HNode().disconnect();

            if (shutdown) {
                LOG.log("Successfully shut down the peer.");
                context.logout();
                context.setH2HNode(null);
            } else {
                LOG.log("Could not disconnect properly");
            }

            return shutdown;
        } else {
            // no need to disconnect
            return true;
        }
    }
}