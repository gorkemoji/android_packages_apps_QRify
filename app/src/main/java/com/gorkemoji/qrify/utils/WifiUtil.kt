package com.gorkemoji.qrify.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.gorkemoji.qrify.R

data class WifiUtil(val ssid: String, val password: String, val encryption: String)

fun parseWifiQr(qrContent: String): WifiUtil? {
    if (!qrContent.startsWith("WIFI:")) return null

    val ssid = Regex("S:([^;]+)").find(qrContent)?.groupValues?.get(1) ?: return null
    val password = Regex("P:([^;]*)").find(qrContent)?.groupValues?.get(1) ?: ""
    val encryption = Regex("T:([^;]+)").find(qrContent)?.groupValues?.get(1) ?: "WPA"

    return WifiUtil(ssid, password, encryption)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun suggestWifiConnection(context: Context, credentials: WifiUtil) {
    val suggestion = WifiNetworkSuggestion.Builder()
        .setSsid(credentials.ssid)
        .setWpa2Passphrase(credentials.password)
        .build()

    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val suggestions = listOf(suggestion)
    val status = wifiManager.addNetworkSuggestions(suggestions)

    if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) Toast.makeText(context, context.getString(R.string.suggested_wifi), Toast.LENGTH_LONG).show()
    else  Toast.makeText(context, context.getString(R.string.suggestion_failed), Toast.LENGTH_SHORT).show()
}