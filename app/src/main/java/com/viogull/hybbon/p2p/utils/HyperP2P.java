package com.viogull.hybbon.p2p.utils;

/**
 * Created by ghost on 27.12.2017.
 */

import com.viogull.hybbon.util.Logg;

import net.tomp2p.peers.PeerAddress;

import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.H2HUserManager;
import org.hive2hive.core.exceptions.GetFailedException;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.model.UserPublicKey;
import org.hive2hive.core.model.versioned.Locations;
import org.hive2hive.core.network.NetworkManager;
import org.hive2hive.core.network.data.DataManager;
import org.hive2hive.core.network.data.PublicKeyManager;
import org.hive2hive.core.network.data.parameters.Parameters;
import org.spongycastle.util.encoders.Base64;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;




public class HyperP2P  {

    private H2HNode node;

    private Logg hyperLog = new Logg("HyperP2P");


    public H2HNode getNode() {
        return node;
    }

    public void setNode(H2HNode node) {
        this.node = node;
    }

    public HyperP2P(H2HNode node)
    {
        this.node = node;
        hyperLog.log("HyperP2P Created...");
    }

    public KeyPair getLocalUserKeyPair() throws NoSessionException
    {
        if(node==null) {
            hyperLog.log("Node == null. Keys didnt getted.");
            return null;
        }
        else {
            hyperLog.log("Node != null. Start getting LocalUserKeyPair");
            NetworkManager networkManager = ((H2HUserManager) node.getUserManager()).getNetworkManager();
            PublicKeyManager keyManager = networkManager.getSession().getKeyManager();
            return keyManager.getOwnKeyPair();
        }
    }


    public void loc() throws NoSessionException, NoPeerConnectionException
    {

    }



    public boolean putPublicKeyIntoNetwork() throws NoSessionException, NoPeerConnectionException
    {
        if(node == null) {
            hyperLog.log("Node == null. Putting of public key failed ");
            return false;
        }
        else {
            hyperLog.log("Node != null. Starting putting...");
            NetworkManager networkManager = ((H2HUserManager) node.getUserManager()).getNetworkManager();
            String userID = networkManager.getUserId();
            hyperLog.log("UserID == " + userID);
            PublicKeyManager keyManager = networkManager.getSession().getKeyManager();

            Parameters parameters = new Parameters().setLocationKey(userID).setContentKey(H2HConstants.USER_PUBLIC_KEY)
                    .setNetworkContent(new UserPublicKey(keyManager.getOwnPublicKey()));

            networkManager.getDataManager().put(parameters);

            hyperLog.log(DataManager.H2HPutStatus.OK + " ---< Key was putted. \n UserID = " + userID
                    + "\nUserPublicKey: " + Base64.toBase64String(keyManager.getOwnPublicKey().getEncoded()) +"\n");
            return true;
        }
    }


    public Locations getUserLocations(String userID)
    {
        hyperLog.log("Starting getting user <" + userID + "> locations");
        if(node==null)
        {
            hyperLog.log("Node is null. False exit.");
            return null;
        } else {
            try {
                NetworkManager networkManager = ((H2HUserManager) node.getUserManager()).getNetworkManager();
                Locations locations = (Locations) networkManager.getDataManager().get(new Parameters()
                        .setContentKey(H2HConstants.USER_LOCATIONS).setLocationKey(userID));

                Set<PeerAddress> locationsAddr = locations.getPeerAddresses();
                Iterator<PeerAddress> it = locationsAddr.iterator();
                while(it.hasNext())
                {
                    hyperLog.log("Parsed locations:  --< " + it.next().toString());
                }
                return locations;
            }
            catch (Exception ex)
            {
                hyperLog.log(Arrays.toString(ex.getStackTrace()));
            }
            return null;
        }
    }


    public PublicKey getUserPublicKeyFromNetwork(String userID) throws NoSessionException, GetFailedException,
            NoPeerConnectionException
    {
        if(node == null)
        {
            hyperLog.log("Node is null. getUserPublicKey is failed");
            return null;
        } else {
            hyperLog.log("Node != null. Starting getting...");
            NetworkManager networkManager = ((H2HUserManager) node.getUserManager()).getNetworkManager();
            hyperLog.log("UserID == " + userID);
            PublicKeyManager keyManager = networkManager.getSession().getKeyManager();
            Parameters pams = new Parameters().setLocationKey(userID).setContentKey(H2HConstants.USER_PUBLIC_KEY);
            UserPublicKey publicKey = (UserPublicKey)
                    networkManager.getDataManager().get(pams);
            if(publicKey==null)
            {
                hyperLog.log("KeyStorage didnt found public key for id <" + userID + '>');
                return null;
            }
            hyperLog.log("UserID exists. Key is getted ----------< " + Base64.toBase64String(publicKey.
                    getPublicKey().getEncoded()));
            return publicKey.getPublicKey();





        }
    }


}