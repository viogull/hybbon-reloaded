package com.viogull.hybbon

import android.app.Application
import android.content.Context
import com.viogull.hybbon.p2p.utils.ApplicationHelper
import com.viogull.hybbon.p2p.utils.ConnectionMode
import com.viogull.hybbon.p2p.utils.RelayMode
import io.realm.Realm
import net.tomp2p.connection.ConnectionBean
import net.tomp2p.dht.PeerDHT
import net.tomp2p.relay.buffer.BufferRequestListener
import org.hive2hive.core.api.interfaces.IH2HNode
import org.hive2hive.core.api.interfaces.INetworkConfiguration
import org.hive2hive.core.network.IPeerHolder
import org.hive2hive.core.serializer.JavaSerializer



class Application : Application(), IPeerHolder {

    public var bufferListener: BufferRequestListener? = null
    private var h2HNode: IH2HNode? = null
    private var relayMode: RelayMode? = null
    private var lastNode: ConnectionMode? = null
    private var networkConfiguration: INetworkConfiguration? = null
    private var userID: String? = null
    //  private var treeRoot: AndroidFile? = null
    lateinit var realm: Realm


    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        System.setProperty("java.net.preferIPv4Stack", "true")
        lastNode = ApplicationHelper.getConnectionMode(this)
        ConnectionBean.DEFAULT_CONNECTION_TIMEOUT_TCP = 20000
        ConnectionBean.DEFAULT_UDP_IDLE_SECONDS = 12
        ConnectionBean.DEFAULT_TCP_IDLE_SECONDS = 12
        val serializer = JavaSerializer()
        //        H2HUserManager userManager = (H2HUserManager) h2HNode.getUserManager();
        //    net_manager = userManager.getNetworkManager();
    }



    override fun onTerminate() {
        super.onTerminate()
        realm.close()
    }


    override  fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // enable multidex
        //		MultiDex.install(this);
    }

    fun getNetworkConf() : INetworkConfiguration? {
        return networkConfiguration
    }

    fun SetLastNode(node : ConnectionMode) {
        lastNode = node
    }
    fun logout() {
        userID = null
        //treeRoot = null
        //stopService(new Intent(getApplicationContext(), HyperService.class));
    }

    fun last_mode() : ConnectionMode? {
        return lastNode
    }

    override fun getPeer(): PeerDHT {
        return h2HNode!!.peer
    }

    fun node() : String {
        return peer!!.peerID().toString()
    }


    fun getH2HNode(): IH2HNode? {
        return h2HNode
    }

    fun setH2HNode(h2HNode: IH2HNode) {
        this.h2HNode = h2HNode
    }

    fun setRelayMode(relayMode: RelayMode) {
        this.relayMode = relayMode
    }

    fun getRelayMode() : RelayMode? {
        return relayMode
    }

    fun setNetworkConfiguration(networkConfiguration: INetworkConfiguration) {
        this.networkConfiguration = networkConfiguration
    }

    fun setUserID(userID: String) {
        this.userID = userID
    }

}