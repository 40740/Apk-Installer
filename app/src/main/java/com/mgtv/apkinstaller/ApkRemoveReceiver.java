package com.mgtv.apkinstaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.mgtv.apkinstaller.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;

import static com.mgtv.apkinstaller.constants.ApkInstallConstants.BROADCAST_ADD_APP;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.BROADCAST_NET_CHANGE_1;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.BROADCAST_NET_CHANGE_2;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.BROADCAST_NET_CHANGE_3;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.BROADCAST_NET_CHANGE_4;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.BROADCAST_NET_CHANGE_5;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.BROADCAST_REMOVE_APP;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.INSTALL_TOAST;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.installPackageName;

/**
 * @author: xiawei
 * @date: 2021/1/12
 */
public class ApkRemoveReceiver extends BroadcastReceiver {

    private Context mContext;

    private Handler mHandler = new Handler();

    private Runnable startSelf = new Runnable() {

        @Override
        public void run() {
            startSelfApp(mContext);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().unregister(this);
        String intentData = String.valueOf(intent.getData());
        MLog.i("onReceive : " + intent);

        if (intent != null && intent.getAction().equals(BROADCAST_REMOVE_APP)) {
            if (mContext == null) {
                mContext = context;
            }
        }


        if (intent != null && enableStartSelf(intent.getAction())) {
            if (mContext == null) {
                mContext = context;
            }
            mHandler.postDelayed(startSelf, 3000);
        }

        if (GlobalProvider.getInstance().isSpecialInstall()) {
            if (intent != null && intent.getAction().equals(BROADCAST_ADD_APP)) {
                if (intentData.contains(installPackageName)) {
                    Toast toast = Toast.makeText(MyApplication.getInstance()
                            .getApplicationContext(), INSTALL_TOAST, Toast.LENGTH_LONG);
                    toast.show();
                    Intent LaunchIntent = MyApplication.getInstance().getApplicationContext()
                            .getPackageManager().getLaunchIntentForPackage(installPackageName);
                    MyApplication.getInstance().getApplicationContext().startActivity(LaunchIntent);
                }
            }
        }
    }

    private void startSelfApp(Context context) {

        MLog.i("startSelfApp");
        Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);

    }

    private boolean enableStartSelf(String action) {

        return (action.equals(BROADCAST_NET_CHANGE_1) || action.equals(BROADCAST_NET_CHANGE_2)
                || action.equals(BROADCAST_NET_CHANGE_3) || action.equals(BROADCAST_NET_CHANGE_4)
                || action.equals(BROADCAST_NET_CHANGE_5));
    }

}
