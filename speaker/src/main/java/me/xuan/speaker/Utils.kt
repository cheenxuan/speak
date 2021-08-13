package me.xuan.speaker

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager


/**
 * Author: xuan
 * Created on 2021/8/13 10:22.
 *
 * Describe:
 */
object Utils {
    
    
    const val  TAG = "Speak"
    var application: Application? = null
    
    /**
     * 读取application 节点  meta-data 信息
     */
    fun readMetaDataFromApplication(tag: String):String? {
        try {
            val ai: ApplicationInfo = get()!!.getPackageManager()
                .getApplicationInfo(get()!!.getPackageName(), PackageManager.GET_META_DATA)
            val bundle = ai.metaData
            return bundle.getString(tag)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        
        return null
    }
    
    fun get(): Application? {
        if (application == null) {
            try {
                application = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null) as Application
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            
        }
        return application

    }

}