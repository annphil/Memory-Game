package com.hanna.mymemory.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// To interact with Android Permissions API
fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED
}

fun requestPermission(activity: Activity?, permission: String, requestCode: Int){
    // Asks user to give permission
    ActivityCompat.requestPermissions(activity!!, arrayOf(permission), requestCode)
}