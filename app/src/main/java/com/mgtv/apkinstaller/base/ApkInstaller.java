package com.mgtv.apkinstaller.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.FileProvider;

import com.mgtv.apkinstaller.GlobalProvider;
import com.mgtv.apkinstaller.MLog;
import com.mgtv.apkinstaller.MyApplication;
import com.mgtv.apkinstaller.bean.ApkListBean;

import net.dlb.aidl.InstallCallback;
import net.dlb.aidl.InstallService;

import java.io.File;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.INSTALL_SERVICE_ACTION;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.PACKAGE;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.installFileName;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.installPackageName;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.installType;


/**
 * @author Mr.xw
 * @description Apk安装器
 * @time 2021/1/11 15:51
 */

public class ApkInstaller implements IApkInstaller {

    private String lastAppPath = "";
    private ApkInstallerServiceConnection mConnection;

    public void installApk(Context context, String packageName, String appPath) {
        normalInstall(context, packageName, appPath);
    }

    public void installApk(ApkListBean.AppListBean appListBean, String appPath,
                           AppInstallCallback appInstallCallback, IApkInstaller iApkInstaller) {
        specialInstall(appListBean, appPath, appInstallCallback, iApkInstaller);
    }

    private void normalInstall(Context context, String packageName, String appPath) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String lastPath = appPath + installFileName;
        File file = new File(lastPath);
        MLog.i("安装路径==" + lastPath);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, packageName + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, installType);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, installType);
        }
        context.startActivity(intent);
    }

    public void uninstallApk(Context context, String packageName) {

        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        MLog.d("uninstallApk===");
    }

    @Override
    public void specialInstall(ApkListBean.AppListBean appListBean, String appPath,
                               AppInstallCallback appInstallCallback, IApkInstaller iApkInstaller) {

        String lastPath = appPath + installFileName;
        lastAppPath = lastPath;
        MLog.d("specialInstall lastAppPath == ===");
        Intent intent = new Intent();
        intent.setAction(INSTALL_SERVICE_ACTION);
        //Android5.0以后需要指定包名
        intent.setPackage(PACKAGE);

        mConnection = new ApkInstallerServiceConnection();
        mConnection.setApkInstallerInfo(GlobalProvider.getInstance().getAppBean(), appInstallCallback);
        try {
            MyApplication.getInstance().getApplicationContext().bindService(intent, mConnection, BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {

    }

    class ApkInstallerServiceConnection implements ServiceConnection {
        private AppInstallCallback apkInstallCallback;
        private InstallService installService;
        private ApkListBean.AppListBean listinfo;
        final InstallCallback mInstallCallback = new InstallCallback.Stub() {
            @Override
            public void onInstallResult(final int result, final String msg) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (apkInstallCallback != null) {
                            apkInstallCallback.onInstallResult(result, msg);
                        }
                    }
                });
                try {
                    //不管安装成功还是失败，都要解绑服务
                    MyApplication.getInstance().getApplicationContext().unbindService(
                            ApkInstallerServiceConnection.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        public void setApkInstallerInfo(ApkListBean.AppListBean listinfo, AppInstallCallback callback) {
            this.listinfo = listinfo;
            this.apkInstallCallback = callback;
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MLog.i("ServiceConnection onServiceConnected listinfo = " + listinfo);
            if (this.listinfo == null) {
                return;
            }
            installService = InstallService.Stub.asInterface(iBinder);
            try {
                installService.install(lastAppPath, installPackageName, listinfo.getAppCode(), mInstallCallback);
                MLog.d("onServiceConnected lastAppPath == ===" + lastAppPath);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            MLog.i("ServiceConnection onServiceDisconnected");
            installService = null;
        }

        public void release() {
            apkInstallCallback = null;
        }
    }
}

