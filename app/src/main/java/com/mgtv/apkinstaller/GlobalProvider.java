package com.mgtv.apkinstaller;

import android.content.Context;

import com.mgtv.apkinstaller.bean.ApkListBean;


/**
 * @author Mr.xw
 * @description 全局变量存储公共类
 * @time 2021/1/13 11:41
 */
public class GlobalProvider {

    private static GlobalProvider mGlobalProvider = null;
    private static Context mContext = null;

    private boolean isSpecialInstall = false;

    private ApkListBean apkListBean;

    private ApkListBean.AppListBean appBean;

    public static GlobalProvider getInstance() {
        if (mGlobalProvider == null) {
            mGlobalProvider = new GlobalProvider();
        }
        return mGlobalProvider;
    }

    public GlobalProvider() {

    }

    public boolean isSpecialInstall() {
        return isSpecialInstall;
    }

    public void setSpecialInstall(boolean specialInstall) {
        isSpecialInstall = specialInstall;
    }

    public ApkListBean getApkListBean() {
        return apkListBean;
    }

    public void setApkListBean(ApkListBean apkListBean) {
        this.apkListBean = apkListBean;
    }

    public ApkListBean.AppListBean getAppBean() {
        return appBean;
    }

    public void setAppBean(ApkListBean.AppListBean appBean) {
        this.appBean = appBean;
    }
}
