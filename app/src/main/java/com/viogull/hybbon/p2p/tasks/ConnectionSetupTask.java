package com.viogull.hybbon.p2p.tasks;

/**
 * Created by ghost on 27.12.2017.
 */

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.viogull.hybbon.R;
import com.viogull.hybbon.Application;
import com.viogull.hybbon.system.crypt.SCSecurityClassProvider;
import com.viogull.hybbon.system.crypt.SpongyCastleEncryption;
import com.viogull.hybbon.p2p.utils.ISuccessFailListener;
import com.viogull.hybbon.p2p.utils.RelayMode;
import com.viogull.hybbon.util.Logg;

import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.nat.FutureRelayNAT;
import net.tomp2p.nat.PeerBuilderNAT;
import net.tomp2p.nat.PeerNAT;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.relay.RelayClientConfig;
import net.tomp2p.relay.buffer.BufferRequestListener;
import net.tomp2p.relay.tcp.TCPRelayClientConfig;

import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.serializer.FSTSerializer;
import org.hive2hive.core.serializer.IH2HSerialize;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.UUID;

/**
 * Created by rsv on 14.05.2017.
 */

public class ConnectionSetupTask extends BaseProgressTask {



    static Logg LOG = new Logg("ConnectionSetupTask");
    private final String bootstrapAddressString;
    private final int bootstrapPort;
    private final long gcmSenderId;

    private final SharedPreferences prefs;

    private String registrationId;
    /**
     * variables that will be stored in the context
     */
    private final RelayMode relayMode;
    private NetworkConfiguration networkConfig;
    private BufferRequestListener bufferListener;
    private IH2HNode h2hNode;
    private Collection<PeerAddress> boostrapAddresses;

    public ConnectionSetupTask(String bootstrapAddress, int bootstrapPort, RelayMode relayMode, long gcmSenderId,
                               Application context, ISuccessFailListener listener, ProgressDialog progressDialog) {
        super(context, listener, progressDialog);
        this.bootstrapAddressString = bootstrapAddress;
        this.bootstrapPort = bootstrapPort;
        this.relayMode = relayMode;
        this.gcmSenderId = gcmSenderId;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected String[] getProgressMessages() {
        String[] progressMessages = new String[4];
        progressMessages[0] = context.getString(R.string.progress_connect_resolve_address);
        progressMessages[1] = context.getString(R.string.progress_connect_bootstrapping);
        progressMessages[2] = context.getString(R.string.progress_connect_register_gcm);
        progressMessages[3] = context.getString(R.string.progress_connect_relay);
        return progressMessages;

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        publishProgress(0);
        try {
            InetAddress bootstrapAddress = InetAddress.getByName(bootstrapAddressString);
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (deviceId == null || deviceId.isEmpty()) {
                deviceId = UUID.randomUUID().toString();
            }
            LOG.log("Using node ID: {}" + deviceId);

            int bindPort = prefs.getInt(context.getString(R.string.pref_port_key), H2HConstants.H2H_PORT);
            LOG.log("Binding port {}" + bindPort);

            networkConfig = new NetworkConfiguration().setBootstrap(bootstrapAddress, bootstrapPort)
                    .setPort(bindPort).setNodeId(deviceId);

        } catch (UnknownHostException e) {
            LOG.log("Cannot resolve host {}" +  bootstrapAddressString +  e);
            return false;
        }

        publishProgress(1);
        if (!createPeer()) {
            return false;
        }

        publishProgress(2);

        publishProgress(3);
        if (!connectNAT()) {
            return false;
        }

        // store the peerNAT in the application context
        context.setH2HNode(h2hNode);
        context.setNetworkConfiguration(networkConfig);
        context.setBufferListener(bufferListener);
        context.setRelayMode(relayMode);

        LOG.log("Peer setup finished successfully");
        publishProgress(4);
        return true;
    }

    /**
     * Creates a peer and returns true if successful
     */
    private boolean createPeer() {
        IH2HSerialize serializer = new FSTSerializer(false, new SCSecurityClassProvider());
        h2hNode = H2HNode.createNode(FileConfiguration.createDefault(), new SpongyCastleEncryption(serializer), serializer);
        if (h2hNode.connect(networkConfig)) {
            LOG.log("H2HNode successfully created.");
            FutureBootstrap bootstrap = h2hNode.getPeer().peer().bootstrap().inetAddress(networkConfig.getBootstrapAddress()).ports(networkConfig.getBootstrapPort()).start();
            bootstrap.awaitUninterruptibly();
            boostrapAddresses = bootstrap.bootstrapTo();
            return true;
        } else {
            LOG.log("H2HNode cannot connect.");
            return false;
        }


    }

    /**
     * Connects to the relay node and returns true if successful
     */
    private boolean connectNAT() {
        int mapUpdateInterval = 120;
        String mapUpdateIntervalString = prefs.getString(context.getString(R.string.pref_map_update_interval_key), String.valueOf(mapUpdateInterval));
        try {
            mapUpdateInterval = Integer.valueOf(mapUpdateIntervalString);
            LOG.log("Use configured map update interval of {}s" +  mapUpdateInterval);
        } catch (NumberFormatException e) {
            LOG.log("Cannot parse the invalid map update interval string '{}'. Use default value {}s"+
                    mapUpdateIntervalString + mapUpdateInterval);
        }

        RelayClientConfig config;
        switch (relayMode) {
            case FULL:
                // don't set up any NAT
                LOG.log("Don't use relay functionality. Make sure to not be behind a firewall!");
                return true;
            case TCP:
                config = new TCPRelayClientConfig().manualRelays(boostrapAddresses).peerMapUpdateInterval(mapUpdateInterval);
                break;
            default:
                LOG.log("Invalid relay mode {}" +  relayMode);
                return false;
        }
        LOG.log("Use {} as relay type. Map updateView interval is {}s " + relayMode +  mapUpdateInterval);

        PeerNAT peerNat = new PeerBuilderNAT(h2hNode.getPeer().peer()).start();
        FutureRelayNAT futureRelayNAT = peerNat.startRelay(config, boostrapAddresses.iterator().next()).awaitUninterruptibly();
        if (futureRelayNAT.isSuccess()) {
            LOG.log("Successfully connected to Relay.");
            bufferListener = futureRelayNAT.bufferRequestListener();
            return true;
        } else {
            LOG.log("Cannot connect to Relay. Reason: {}" + futureRelayNAT.failedReason());
            return false;
        }



    }



    @Override
    protected void onPostExecute(Boolean success) {
        if (!success && h2hNode != null) {
            h2hNode.disconnect();
        }
        super.onPostExecute(success);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        if (h2hNode != null) {
            h2hNode.disconnect();
        }

        super.onCancelled(aBoolean);
    }




}