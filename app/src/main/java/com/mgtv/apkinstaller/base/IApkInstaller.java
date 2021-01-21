package com.mgtv.apkinstaller.base;

import com.mgtv.apkinstaller.bean.ApkListBean;

public interface IApkInstaller {
    /**
     * 安装apk
     * @param
     */
    void specialInstall(ApkListBean.AppListBean appListBean, String appPath,
                        AppInstallCallback appInstallCallback, IApkInstaller iApkInstaller);

    /**
     * 释放资源
     */
    void release();
}
