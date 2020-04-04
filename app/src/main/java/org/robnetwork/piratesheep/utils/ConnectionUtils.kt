package org.robnetwork.piratesheep.utils

import android.content.Context
import android.net.ConnectivityManager

object ConnectionUtils {
    fun isConnectionMobile(context: Context) =
        (context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.isActiveNetworkMetered
            ?: false
}