package com.ffi.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class BaseActivity  {



    private  Context context;
    private  String pacakageName;

    public boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void requestStoragePermission(){
        ActivityCompat.requestPermissions((Activity) context.getApplicationContext(),
                new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                1);
    }
}
