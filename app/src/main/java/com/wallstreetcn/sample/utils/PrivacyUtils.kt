package com.wallstreetcn.sample.utils

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.provider.Settings
import android.telephony.TelephonyManager

/**
 *
 *  @Author LiABao
 *  @Since 2021/8/9
 *
 */
object PrivacyUtils {
    @JvmStatic
    fun getImei(manager: TelephonyManager): String {
        manager.deviceId
        return ""
    }


    @JvmStatic
    fun getWifiName(info: WifiInfo): String {
        try {
            /*  val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE)
                      as WifiManager
              val info = wifiMgr.connectionInfo
              return info?.ssid ?: ""*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun getBSSID(info: WifiInfo): String {
        try {
            /*  val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE)
                      as WifiManager
              val info = wifiMgr.connectionInfo
              return info?.ssid ?: ""*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun getString(resolver: ContentResolver?, name: String?): String {
        resolver ?: return ""
        name ?: return ""
        return Settings.Secure.getString(resolver, name)
    }


    @JvmStatic
    fun getAppPackageInfo(pm: PackageManager, arg: Int = PackageManager.GET_META_DATA): MutableList<PackageInfo> {
        val list: MutableList<PackageInfo> = mutableListOf()
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
        //  pm.getApplicationIcon()
        return list
    }
}