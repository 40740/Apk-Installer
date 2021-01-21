package com.mgtv.apkinstaller.down;

import android.content.Context;
import android.os.Environment;

import com.mgtv.apkinstaller.MLog;
import com.mgtv.apkinstaller.MyApplication;
import com.mgtv.apkinstaller.base.InstallListenerCallBack;
import com.mgtv.apkinstaller.http.HttpClientManager;
import com.mgtv.apkinstaller.util.StringUtil;
import com.mgtv.apkinstaller.view.DownLoadProgressDialog;

import java.io.File;

import static com.mgtv.apkinstaller.constants.ApkInstallConstants.defaultApkName;


/**
 * @author: xiawei
 * @date: 2021/1/11
 */
public class ApkDownLoad {

    private Context mContext;
    private String mUrl = "";

    public ApkDownLoad() {
        mContext = MyApplication.getInstance().getBaseContext();

    }

    public void downLoadApk(String downLoadUrl, final DownLoadProgressDialog downLoadProgressDialog,
                            final InstallListenerCallBack installListenerCallBack) {
        if (StringUtil.isEmpty(downLoadUrl)) {
            return;
        }
        mUrl = downLoadUrl;
        File newAppPath = Environment.getExternalStorageDirectory();
        final String appPath = String.valueOf(newAppPath);
        /**在子线程中处理Apk下载*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClientManager.getInstance().downloadApk(mUrl, appPath, defaultApkName,
                        new HttpClientManager.OnDownloadListener() {

                            @Override
                            public void onDownloadSuccess(File file) {
                                MLog.i("onDownloadSuccess: downLoadApk ==" + file);
                                /**下载完毕后，影藏下载弹框，弹出安装提示，是否进行安装*/
                                downLoadProgressDialog.dismiss();
                                if (installListenerCallBack != null) {
                                    installListenerCallBack.beginInstall(appPath);
                                }
                            }

                            @Override
                            public void onDownloading(int progress) {
                                /**下载中更新进度条*/
                                MLog.d("onDownloading : progress is  " + progress);
                                downLoadProgressDialog.setDialogProgress(progress);
                            }

                            @Override
                            public void onDownloadFailed(Exception e) {
                                MLog.e("onDownloadFailed  " + e);
                            }
                        });
            }
        }).start();
    }

}
