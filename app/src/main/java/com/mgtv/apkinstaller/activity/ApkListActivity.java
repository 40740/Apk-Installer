package com.mgtv.apkinstaller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;

import com.mgtv.apkinstaller.GlobalProvider;
import com.mgtv.apkinstaller.GsonUtil;
import com.mgtv.apkinstaller.MLog;
import com.mgtv.apkinstaller.MessageEvent;
import com.mgtv.apkinstaller.MyApplication;
import com.mgtv.apkinstaller.R;
import com.mgtv.apkinstaller.adapter.ApkListAdapter;
import com.mgtv.apkinstaller.adapter.BaseRecycleViewAdapter;
import com.mgtv.apkinstaller.base.ApkInstaller;
import com.mgtv.apkinstaller.base.AppInstallCallback;
import com.mgtv.apkinstaller.base.IApkInstaller;
import com.mgtv.apkinstaller.base.InstallListenerCallBack;
import com.mgtv.apkinstaller.bean.ApkListBean;
import com.mgtv.apkinstaller.down.ApkDownLoad;
import com.mgtv.apkinstaller.http.ApkListHttpItem;
import com.mgtv.apkinstaller.http.HttpClientManager;
import com.mgtv.apkinstaller.util.ApkListUtil;
import com.mgtv.apkinstaller.util.StringUtil;
import com.mgtv.apkinstaller.view.ApkListRecyclerView;
import com.mgtv.apkinstaller.view.DownLoadProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import okhttp3.Request;

import static com.mgtv.apkinstaller.constants.ApkInstallConstants.DELAY_SHOW_DATA_TIME;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.INSTALL_DELAY_TIME;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.SPECAIL_REMOVE_MESSAGE;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.apkListUrl;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.installPackageName;

/**
 * @author Mr.xw
 * @description 打开运营商apk安装列表界面
 * @time 2021/1/11 12:47
 */
public class ApkListActivity extends Activity {

    private ApkListAdapter apkListAdapter;
    private ApkListRecyclerView apkListRecyclerView;
    private boolean isFirst = false;
    private int lastPosition;
    private FrameLayout relativeLayout;
    private ApkDownLoad apkDownLoad;
    private ApkInstaller apkInstaller;
    private String doRequestUrl = "";
    private Context mContext;

    private List<ApkListBean.AppListBean> appList;
    private ApkListBean apkListBean;
    private DownLoadProgressDialog downLoadProgressDialog = null;
    private InstallListenerCallBack installListenerCallBack;
    private String mDownLoadUrl = "";
    private boolean isFinish = false;
    private IApkInstaller mInstaller;
    private Handler mHandler = new Handler();

    /**
     * 走咪咕框架
     */
    private AppInstallCallback mAppInstallCallback;

    private Runnable initShowData = new Runnable() {

        @Override
        public void run() {
            if (apkListRecyclerView != null) {
                if (isFirst) {
                    lastPosition = 0;
                } else {
                    View childView = apkListRecyclerView.getChildAt(lastPosition);
                    if (childView != null) {
                        if (childView.isFocused()) {
                            apkListAdapter.updateItemView(childView, true, lastPosition);
                        }
                    }
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        MLog.i(" onCreate->");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mContext = MyApplication.getInstance().getBaseContext();
        /**注册事件*/
        EventBus.getDefault().register(this);
        isFirst = true;

        if (apkListBean != null) {
            apkListBean = null;
        }
        if (apkDownLoad != null) {
            apkDownLoad = null;
        }
        if (apkInstaller != null) {
            apkInstaller = null;
        }
        apkDownLoad = new ApkDownLoad();
        apkInstaller = new ApkInstaller();
        apkListBean = new ApkListBean();

        /** 走咪咕框架下载安装*/
        mInstaller = new IApkInstaller() {
            @Override
            public void specialInstall(ApkListBean.AppListBean apkListBean, String appPath, AppInstallCallback appInstallCallback, IApkInstaller iApkInstaller) {
                mAppInstallCallback = appInstallCallback;
                apkInstaller.installApk(GlobalProvider.getInstance().getAppBean(), appPath, appInstallCallback, mInstaller);
            }

            @Override
            public void release() {

            }
        };
        initIntent();
        initView();
        /** 通用的下载安装*/
        installListenerCallBack = new InstallListenerCallBack() {
            @Override
            public void beginInstall(String appPath) {
                if (!GlobalProvider.getInstance().isSpecialInstall()) {
                    apkInstaller.installApk(mContext, installPackageName, appPath);
                } else {
                    apkInstaller.installApk(GlobalProvider.getInstance().getAppBean(), appPath, mAppInstallCallback, mInstaller);
                }
            }
        };

        /**防止异常更崩溃，无法重置状态*/
        isFinish = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealMesg(MessageEvent messageEvent) {
        String dealMesg = messageEvent.getMessage();
        if (messageEvent != null && !StringUtil.isEmpty(messageEvent.getMessage())) {
            if (dealMesg.equals(SPECAIL_REMOVE_MESSAGE)) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        apkDownLoad.downLoadApk(mDownLoadUrl, downLoadProgressDialog, installListenerCallBack);
                        showDownDialog();
                    }
                }, INSTALL_DELAY_TIME);
            }
        }
    }

    /**
     * 获取页面传递参数
     */
    private boolean initIntent() {
        Intent intent = this.getIntent();
        doRequestUrl = intent.getStringExtra(apkListUrl);
        if (doRequestUrl == null) {
            return false;
        }
        if (doRequestUrl != null) {
            getHttpData(doRequestUrl);
        }
        return true;
    }

    private void getHttpData(String json) {
        ApkListHttpItem apkListHttpItem = new ApkListHttpItem(json);
        HttpClientManager.getAsyn(apkListHttpItem, new HttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        ApkListBean mApkListBean = GsonUtil.GsonToBean(response, ApkListBean.class);
                        if (mApkListBean != null) {
                            appList = mApkListBean.getAppList();
                            apkListBean = mApkListBean;
                            GlobalProvider.getInstance().setApkListBean(apkListBean);
                            apkListAdapter.setNewData(appList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void initView() {

        apkListRecyclerView = findViewById(R.id.apk_list_recycler);
        relativeLayout = findViewById(R.id.apk_list);
        apkListAdapter = new ApkListAdapter();
        apkListAdapter.setHasStableIds(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        apkListRecyclerView.setLayoutManager(linearLayoutManager);

        apkListRecyclerView.setAdapter(apkListAdapter);
        downLoadProgressDialog = new DownLoadProgressDialog(this);

        setApkListFocusListener();
        setApkListOnclickListener();
    }

    @Override
    protected void onResume() {
        MLog.i(" onResume->");
        super.onResume();
        if (isFirst) {
            MLog.i(" fist onResume->");
        } else {
            MLog.i(" other onResume->");
            mHandler.postDelayed(initShowData, DELAY_SHOW_DATA_TIME);
        }
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
        GlobalProvider.getInstance().setSpecialInstall(false);
        GlobalProvider.getInstance().setAppBean(null);
        EventBus.getDefault().unregister(this);
        mHandler = null;
    }


    /**
     * @description 设置下载界面RecyclerView按键监听
     * @author Mr.xw
     * @time 2021/1/12 9:30
     */
    private void setApkListFocusListener() {
        apkListAdapter.setOnItemFocusChangeListener(new BaseRecycleViewAdapter
                .OnItemFocusChangeListener() {
            @Override
            public void OnItemFocus(BaseRecycleViewAdapter adapter, View view, int position) {
                apkListAdapter.updateItemView(view, true, position);
            }

            @Override
            public void OnItemUnFocused(BaseRecycleViewAdapter adapter, View view, int position) {
                apkListAdapter.updateItemView(view, false, position);
            }

        });
    }

    /**
     * @description 点击处理下载安装等
     * @author Mr.xw
     * @time 2021/1/12 9:53
     */
    private void setApkListOnclickListener() {
        apkListAdapter.setOnItemClickListener(new BaseRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecycleViewAdapter adapter, View view, int position) {
                GlobalProvider.getInstance().setAppBean(apkListAdapter.getItem(position));
                prepareInstall(apkListAdapter.getItem(position), downLoadProgressDialog, null, null);
            }
        });

    }

    private void showDownDialog() {
        if (downLoadProgressDialog != null) {
            downLoadProgressDialog.show();
        }
    }

    /**
     * @description 获取下载地址，下载apk到指定目录
     * @author Mr.xw
     * @time 2021/1/12 9:56
     */
    private void prepareInstall(ApkListBean.AppListBean item, DownLoadProgressDialog downLoadProgressDialog,
                                AppInstallCallback appInstallCallback, IApkInstaller iApkInstaller) {
        if (item == null) {
            return;
        }
        String downLoadUrl = item.getApkDownUrl();
        mDownLoadUrl = downLoadUrl;
        /**需要走咪咕框架，安装流程*/
        if (GlobalProvider.getInstance().isSpecialInstall()) {
            if (ApkListUtil.isPkgInstalled(mContext, installPackageName)) {
                apkInstaller.uninstallApk(mContext, installPackageName);
            } else {
                apkDownLoad.downLoadApk(downLoadUrl, downLoadProgressDialog, installListenerCallBack);
                showDownDialog();
            }
        } else {
            /**不需要走私有接口可以直接用原生的卸载，安装流程*/
            if (!StringUtil.isEmpty(downLoadUrl)) {
                if (ApkListUtil.isPkgInstalled(mContext, installPackageName)) {
                    /**兼容处理高安加密盒子采用覆盖安装，只能支持安装高版本*/
                    ApkListUtil.clearApp();
                    ApkListUtil.uninstallApp();
                    ApkListUtil.rmAppData();
                    apkInstaller.uninstallApk(mContext, installPackageName);
                    EventBus.getDefault().post(new MessageEvent(SPECAIL_REMOVE_MESSAGE));
                    /**收到卸载成功的回调后再心在apk*/
                } else {
                    apkDownLoad.downLoadApk(downLoadUrl, downLoadProgressDialog, installListenerCallBack);
                    showDownDialog();
                }
            }
        }

    }

}
