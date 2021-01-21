package com.mgtv.apkinstaller.bean;

import java.util.List;

/**
 * @author Mr.xw
 * @description Apklist下载格式
 * @time 2021/1/11 15:51
 */
public class ApkListBean {

    public List<AppListBean> appList;

    public List<AppListBean> getAppList() {
        return appList;
    }

    public void setAppList(List<AppListBean> appList) {
        this.appList = appList;
    }

    public static class AppListBean {
        /**
         * appCode : 1201356306265082
         * apkName : YYS.4.9.7.266.2.HNDX.0.0_Release.apk
         * apkDownUrl : http://otthnydapk.yys.mgtv.com:6600/picture/apk/YYS.4.9.7.266.2.HNDX.0.0_Release.apk
         */
        private String appCode;
        private String apkName;
        private String apkDownUrl;

        public String getAppCode() {
            return appCode;
        }

        public void setAppCode(String appCode) {
            this.appCode = appCode;
        }

        public String getApkName() {
            return apkName;
        }

        public void setApkName(String apkName) {
            this.apkName = apkName;
        }

        public String getApkDownUrl() {
            return apkDownUrl;
        }

        public void setApkDownUrl(String apkDownUrl) {
            this.apkDownUrl = apkDownUrl;
        }

        @Override
        public String toString() {
            return "AppListBean{" +
                    "appCode='" + appCode + '\'' +
                    ", apkName='" + apkName + '\'' +
                    ", apkDownUrl='" + apkDownUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ApkListBean{" +
                "appList=" + appList +
                '}';
    }
}
