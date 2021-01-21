package com.mgtv.apkinstaller.base;

public interface AppInstallCallback {
    int INSTALL_SUCCESS = 0;
    int INSTALL_FAIL = 1;
    int INSTALL_UNKOWN = -1;
    /**
     *
     * @param result 安装结果： 0：成功， 1:失败
     * @param msg 结果描述（如，失败原因）
     */
    void onInstallResult(int result, String msg);
}
