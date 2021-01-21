package com.mgtv.apkinstaller.constants;

/**
 * @author Mr.xw
 * @description 常量池
 * @time 2021/1/11 13:06
 */

public class ApkInstallConstants {
    /**
     * 移动IPTV
     */
    public static final String hnydIptv = "http://hnydiptvimg.yys.mgtv.com:6600/picture/apk/appList4HnydIptv.json";
    /**
     * 移动OTT
     */
    public static final String hnydOtt = "http://otthnydapk.yys.mgtv.com:6600/picture/apk/appList4HnydOtt.json";
    /**
     * 电信IPTV
     */
    public static final String hndxIptv = "http://10.255.1.107:81/picture/apk/appList4Hndx.json";
    /**
     * 联通OTT
     */
    public static final String hnltOtt = "http://starcorupdate.hifuntv.com/openapk/dbei/appList4Hnlt.json";
    /**
     * 云南移动OTT
     */
    public static final String ynydOtt = "http://starcorupdate.hifuntv.com/openapk/dbei/appList4Ynyd.json";

    public static final String apkListUrl = "apkListUrl";

    public static final String appCodeKey = "appCode";

    public static final String apkNameKey = "apkName";

    public static final String apkDownUrlKey = "apkDownUrl";

    public static final int DELAY_SHOW_DATA_TIME = 200;//ms

    public static final String downLoadInfo = "芒果APP下载";

    public static final String laoding = "下载中……";

    public static final String defaultApkName = "mgtv.apk";

    public static final String installPackageName = "com.hunantv.operator";

    public static final String installFileName = "/mgtv.apk";

    public static final String BROADCAST_REMOVE_APP = "android.intent.action.PACKAGE_REMOVED";

    public static final String BROADCAST_START_APP = "android.intent.action.BOOT_COMPLETED";


    public static final String BROADCAST_ADD_APP = "android.intent.action.PACKAGE_ADDED";

    public static final String BROADCAST_NET_CHANGE_1 = "android.net.ethernet.ETHERNET_STATE_CHANGE";

    public static final String BROADCAST_NET_CHANGE_2 = "android.net.ethernet.STATE_CHANGE";

    public static final String BROADCAST_NET_CHANGE_3 = "android.net.conn.CONNECTIVITY_CHANGED";

    public static final String BROADCAST_NET_CHANGE_4 = "android.net.wifi.WIFI_STATE_CHANGED";

    public static final String BROADCAST_NET_CHANGE_5 = "android.net.wifi.STATE_CHANGE";

    public static final String REMOVE_MESSAGE = "remove";

    public static final String SPECAIL_REMOVE_MESSAGE = "special_remove";

    public static final String INSTALL_SERVICE_ACTION = "net.dlb.shcmcc.installservice";

    public static final String PACKAGE = "net.dlb.shcmcc.installservice";

    public static final String installType = "application/vnd.android.package-archive";

    public static final String INSTALL_TOAST = "请稍等，正在静默安装中。。。。。。";

    public static final String B860AV_SPECAIL_GAO_AN_MOEDL = "B860AV2.1-T";

    public static final String COMMAND_STR_CHMOD = "chmod";

    public static final String COMMAND_STR_FILE_RWD = "777";

    public static final String COMMAND_PM = "pm";

    public static final String COMMAND_RM = "rm";

    public static final String COMMAND_CEALR = "clear";

    public static final String COMMAND_UNINSTALL = "uninstall";

    public static final String COMMAND_FR = "-fr";

    public static final String APP_DATA_DIR = "/data/data/com.hunantv.operator";

    public static final int INSTALL_DELAY_TIME = 2000;


}
