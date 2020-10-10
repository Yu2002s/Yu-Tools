package com.yu.tools

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.yu.tools.util.ActivityCollector
import com.yu.tools.util.StatusBarUtil


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)

        StatusBarUtil.setStatusBar(this, true)
    }

    fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    fun setSpBoolean(name: String?, i: Boolean) {
        val sp = getSharedPreferences(name, MODE_PRIVATE).edit()
        sp.putBoolean("key", i)
        sp.apply()
    }

    fun getSpBoolean(name: String?): Boolean {
        val sp = getSharedPreferences(name, MODE_PRIVATE)
        return sp.getBoolean("key", false)
    }

    private val mPermission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    open fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(mPermission[0]) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                requestPermissions(mPermission, 0)
            }
        }
        return false
    }

    fun requestInstallPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val hasInstallPermission = packageManager.canRequestPackageInstalls()
            if (!hasInstallPermission) {
                startInstallPermissionSetting()
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // true
              //  requestRootPermission()
            } else {
                if (!shouldShowRequestPermissionRationale(permissions[0]!!)) {
                    startAppDetailSetting()
                } else {
                    requestPermissions(permissions, 0)
                }
            }
        }
    }

    private fun startAppDetailSetting() {
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("申请权限")
            .setMessage("请打开读写储存权限，此权限用于保存下载的文件。否则软件无法正常运行")
            .setCancelable(false)
            .setPositiveButton("去设置", DialogInterface.OnClickListener { dia, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivityForResult(intent, 1)
            })
            .create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startInstallPermissionSetting() {
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("需要权限")
            .setMessage("由于安卓限制，安装未知软件需要您手动进行授权。")
            .setPositiveButton("去打开", DialogInterface.OnClickListener { dia, which ->
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivityForResult(intent, 2)
            })
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            requestPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}