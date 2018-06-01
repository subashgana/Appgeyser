package com.appsgeyser.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by subash.b on 24-Feb-17.
 */

public final class Util {
    public static boolean isNetworkAvailable(final Context context) {

        if (context == null) {
            return false;
        }
        final ConnectivityManager mConnManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnManager != null) {
            final NetworkInfo[] netInfo = mConnManager.getAllNetworkInfo();
            for (NetworkInfo mNetInfo : netInfo) {
                if ((mNetInfo.getTypeName().equalsIgnoreCase("WIFI") || mNetInfo
                        .getTypeName().equalsIgnoreCase("MOBILE"))
                        && mNetInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
