package com.mgtv.apkinstaller.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.mgtv.apkinstaller.MLog;
import com.mgtv.apkinstaller.MyApplication;
import com.mgtv.apkinstaller.R;

import static com.mgtv.apkinstaller.constants.ApkInstallConstants.downLoadInfo;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.laoding;

/**
 * @author Mr.xw
 * @description DownLoadProgressDialog下载安装弹窗UI
 * @time 2021/1/12 12:49
 */
public class DownLoadProgressDialog extends ProgressDialog {

    public DownLoadProgressDialog(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        MLog.i("DownLoadProgressDialog init:");

        /**设置进度条风格*/
        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        /**设置ProgressDialog 标题*/
        setTitle(downLoadInfo);

        /**设置ProgressDialog 提示信息*/
        setMessage(laoding);

        /**设置ProgressDialog 标题图标*/
        //TODO

        /**设置ProgressDialog 的进度条是否不明确*/
        setIndeterminate(false);

        /**设置ProgressDialog 是否可以按退回按键取消*/
        setCancelable(false);

        Drawable progressDrawable = MyApplication.getInstance().getResources().getDrawable(R.drawable.load_msg_progress);
        setProgressDrawable(progressDrawable);

    }

    public void setDialogProgress(int progress) {
        /**设置ProgressDialog 进度条进度*/
        setProgress(progress);
    }
}
