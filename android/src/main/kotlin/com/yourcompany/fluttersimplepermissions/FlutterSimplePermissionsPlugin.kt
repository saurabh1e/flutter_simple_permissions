package com.yourcompany.fluttersimplepermissions

import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall

import android.Manifest
import android.util.Log
import android.content.pm.PackageManager
import android.app.Activity
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.READ_SMS
import android.Manifest.permission.SEND_SMS
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener


class FlutterSimplePermissionsPlugin : MethodCallHandler, RequestPermissionsResultListener {
    private var registrar: Registrar? = null
    private var result: Result? = null

    constructor(registrar: Registrar) : super() {
        this.registrar = registrar;
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar): Unit {
            val channel = MethodChannel(registrar.messenger(), "flutter_simple_permissions")
            val fsp: FlutterSimplePermissionsPlugin = FlutterSimplePermissionsPlugin(registrar);
            channel.setMethodCallHandler(fsp);
            registrar.addRequestPermissionsResultListener(fsp);

        }
    }

    override fun onMethodCall(call: MethodCall, res: Result): Unit {
        when {
            call.method.equals("getPlatformVersion") -> res.success("Android ${android.os.Build.VERSION.RELEASE}")
            call.method.equals("checkPermission") -> {
                val permission: String = call.argument("permission");
                res.success(checkPermission(permission));
            }
            call.method.equals("requestPermission") -> {
                val permission: String = call.argument("permission");
                result = res;
                requestPermission(permission);
            }
            call.method.equals("openSettings") -> {
                openSettings();
                res.success(true);
            }
            else -> res.notImplemented()
        }

    }

    private fun openSettings() {
        val activity: Activity? = registrar?.activity();
        val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity?.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (activity != null) {
            activity.startActivity(intent);
        }
    }


    private fun getManifestPermission(permission: String): String {
        val res: String
        when (permission) {
            "RECORD_AUDIO" -> res = Manifest.permission.RECORD_AUDIO
            "CAMERA" -> res = Manifest.permission.CAMERA
            "WRITE_EXTERNAL_STORAGE" -> res = Manifest.permission.WRITE_EXTERNAL_STORAGE
            "ACCESS_FINE_LOCATION" -> res = Manifest.permission.ACCESS_FINE_LOCATION
            "ACCESS_COARSE_LOCATION" -> res = Manifest.permission.ACCESS_COARSE_LOCATION
            "WHEN_IN_USE_LOCATION" -> res = Manifest.permission.ACCESS_FINE_LOCATION
            "ALWAYS_LOCATION" -> res = Manifest.permission.ACCESS_FINE_LOCATION
            "READ_SMS" -> res = Manifest.permission.READ_SMS
            "SEND_SMS" -> res = Manifest.permission.SEND_SMS
            else -> res = "ERROR"
        }
        return res
    }

    private fun requestPermission(permission: String) {
        var permission = permission
        val activity = registrar?.activity();
        permission = getManifestPermission(permission)
        val perm = arrayOf(permission)
        if (activity != null) {
            ActivityCompat.requestPermissions(activity, perm, 0)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        var permission = permission
        val activity = registrar?.activity()
        permission = getManifestPermission(permission)
        if (activity != null) {
            return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, permission)
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, strings: Array<String>, grantResults: IntArray): Boolean {
        var res = false
        if (requestCode == 0 && grantResults.size > 0) {
            res = grantResults[0] == PackageManager.PERMISSION_GRANTED
            result?.success(res)
        }
        return res
    }

}
