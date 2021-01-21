package com.mgtv.apkinstaller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.mgtv.apkinstaller.GlobalProvider;
import com.mgtv.apkinstaller.MLog;
import com.mgtv.apkinstaller.MyApplication;
import com.mgtv.apkinstaller.R;

import static com.mgtv.apkinstaller.constants.ApkInstallConstants.hndxIptv;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.hnltOtt;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.hnydIptv;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.hnydOtt;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.ynydOtt;

/**
 * @author Mr.xw
 * @time 2021/1/11 11:26
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    /**
     * 湖南移动IPTV
     */
    private Button mButtonHnydIptv;
    /**
     * 湖南移动OTT
     */
    private Button mButtonHnydOtt;
    /**
     * 湖南电信IPTV
     */
    private Button mButtonHndxIptv;
    /**
     * 湖南联通OTT
     */
    private Button mButtonHnltOtt;
    /**
     * 云南移动OTT
     */
    private Button mButtonynydOtt;

    /**
     * exit
     */
    private Button mExit;

    private Intent intent;

    private boolean isFinish = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.init((MyApplication) getApplication());
        mContext = MyApplication.getInstance().getApplicationContext();
        setContentView(R.layout.activity_main);

        mButtonHnydIptv = findViewById(R.id.mgtv_btn1);
        mButtonHnydOtt = findViewById(R.id.mgtv_btn2);
        mButtonHndxIptv = findViewById(R.id.mgtv_btn3);
        mButtonHnltOtt = findViewById(R.id.mgtv_btn4);
        mButtonynydOtt = findViewById(R.id.mgtv_btn5);
        mExit = findViewById(R.id.mgtv_btn6);

        mButtonHnydIptv.setOnClickListener(this);
        mButtonHnydOtt.setOnClickListener(this);
        mButtonHndxIptv.setOnClickListener(this);
        mButtonHnltOtt.setOnClickListener(this);
        mButtonynydOtt.setOnClickListener(this);
        mExit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        String common = "";
        if (view == mExit) {
            finish();
//            System.exit(0);
        } else {
            if (view == mButtonHnydIptv) {
                common = hnydIptv;
                GlobalProvider.getInstance().setSpecialInstall(false);
            }
            if (view == mButtonHnydOtt) {
                common = hnydOtt;
                GlobalProvider.getInstance().setSpecialInstall(true);
            }
            if (view == mButtonHndxIptv) {
                GlobalProvider.getInstance().setSpecialInstall(false);
                common = hndxIptv;
            }
            if (view == mButtonHnltOtt) {
                GlobalProvider.getInstance().setSpecialInstall(false);
                common = hnltOtt;
            }
            if (view == mButtonynydOtt) {
                GlobalProvider.getInstance().setSpecialInstall(false);
                common = ynydOtt;
            }
            intent = new Intent(mContext, ApkListActivity.class);
            intent.putExtra("apkListUrl", common);
            startActivity(intent);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
                default:
                    return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause() {
        MLog.i(" onPause->");
        super.onPause();
        if (isFinishing()) {
            isFinish = true;
            dataRelease();
        }
    }

    @Override
    protected void onStop() {
        MLog.i(" onStop->");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MLog.i(" onDestroy->");
        super.onDestroy();
        if (isFinishing()) {
            if (isFinish) {
                MLog.i(" onDestroy isFinishing ->");
            } else {
                dataRelease();
                isFinish = false;
            }
        }
    }

    private void dataRelease() {
        mContext = null;

    }


}
