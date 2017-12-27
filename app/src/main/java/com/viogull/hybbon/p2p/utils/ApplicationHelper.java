package com.viogull.hybbon.p2p.utils;

/**
 * Created by ghost on 27.12.2017.
 */
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApplicationHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationHelper.class);
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String EXTERNAL_IP_LOOKUP_URL = "http://wtfismyip.com/text";

    /**
     * Kills the application immediately. Only use in emergency!
     */
    public static void killApplication() {
        //android.os.Process.killProcess(Process.myPid());
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */


    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Maps a URI starting with 'content:' to a real file name
     */
    public static String getFileNameContentURI(Context context, Uri contentUri) {
        Cursor returnCursor =
                context.getContentResolver().query(contentUri, null, null, null, null);
        int nameIndex = returnCursor != null ? returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) : 0;
        returnCursor.moveToFirst();
        return returnCursor.getString(nameIndex);
    }

    /**
     * @return the current connection mode
     */
    public static ConnectionMode getConnectionMode(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connManager != null ? connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) : null;
        if (wifiInfo.isConnected()) {
            return ConnectionMode.WIFI;
        }
        NetworkInfo mobileInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo.isConnected()) {
            return ConnectionMode.CELLULAR;
        }

        return ConnectionMode.OFFLINE;
    }


}