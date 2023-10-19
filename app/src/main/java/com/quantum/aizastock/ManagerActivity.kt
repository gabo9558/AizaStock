package com.quantum.aizastock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class ManagerActivity : AppCompatActivity(), MultiplePermissionsListener, CameraXConfig.Provider {

    private val permissions = listOf(
        android.Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onResume() {
        super.onResume()

        Dexter.withContext(this)
            .withPermissions(permissions)
            .withListener(this)
            .check()
    }

    override fun onPermissionsChecked(permissionsResport: MultiplePermissionsReport?) {
        if(permissionsResport?.deniedPermissionResponses?.isNotEmpty() == true){
            Toast.makeText(this, getString(R.string.permisos_necesarios), Toast.LENGTH_LONG)
                .show()
            finishAffinity()
        }
    }

    override fun onPermissionRationaleShouldBeShown(
        permissionsList: MutableList<PermissionRequest>?,
        permissionToken: PermissionToken?
    ) {
        permissionToken?.continuePermissionRequest()
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}