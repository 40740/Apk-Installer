package com.mgtv.apkinstaller.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mgtv.apkinstaller.MLog;
import com.mgtv.apkinstaller.http.HttpItem;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.mgtv.apkinstaller.constants.ApkInstallConstants.APP_DATA_DIR;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.B860AV_SPECAIL_GAO_AN_MOEDL;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.COMMAND_CEALR;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.COMMAND_FR;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.COMMAND_PM;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.COMMAND_RM;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.COMMAND_STR_CHMOD;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.COMMAND_STR_FILE_RWD;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.COMMAND_UNINSTALL;
import static com.mgtv.apkinstaller.constants.ApkInstallConstants.installPackageName;

/**
 * @author: xiawei
 * @date: 2021/1/11
 */
public class ApkListUtil {

    /**
     * @description 解析接口下发配置
     * @author Mr.xw
     * @time 2021/1/11 16:22
     */

    public static String addMap2Url(String url, Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (builder.length() == 0) {
                builder.append(url + "?" + key + "=" + value);
            } else {
                builder.append("&" + key + "=" + value);
            }
        }
        return builder.toString();
    }

    /*******************************************************************************
     ************************ 静态地址转化 ************************************
     *******************************************************************************/
    /**
     * 伪接口地址转换
     *
     * @param url 原始URL
     * @return 静态化后的地址
     */
    public static String pseudoInterfaceAddrTrans(String url) {
        StringBuffer retStr = new StringBuffer();
        String[] url_param = TruncateUrl(url);
        if (url_param == null) {
            return url;
        }
        String strUrl = url_param[0];
        retStr.append(strUrl);
        MLog.d("step 0 : " + retStr.toString());
        Map<String, String> params = getUrlParams(url);
        if (params.containsKey("nns_func")) {
            String value = params.get("nns_func");
            retStr.append("/").append(generationString(value, false));
            params.remove("nns_func");
        }
        if (params.size() > 0) {
            retStr.append("?");
        }
        MLog.d("step 1 : " + retStr.toString());
        for (String key : params.keySet()) {
            retStr.append(generationString(key, true)).append("=").append(params.get(key)).append("&");
        }
        // 去掉最后多余的一个“&”
        String str = retStr.toString();
        String lastStr = str.substring(retStr.toString().length() - 1, retStr.toString().length());
        if ("&".equals(lastStr)) {
            str = str.substring(0, retStr.toString().length() - 1);
        }
        MLog.d("step 2 : " + retStr.toString());
        return str;
    }

    /**
     * 获取url的参数和请求部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String[] TruncateUrl(String strURL) {
        String[] arrSplit = null;

        strURL = strURL.trim();

        arrSplit = strURL.split("\\?");

        return arrSplit;
    }

    /**
     * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    private static Map<String, String> getUrlParams(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit = null;

        String[] url_param = TruncateUrl(URL);
        if (url_param == null) {
            return mapRequest;
        }
        String strUrlParam = url_param[1];
        if (strUrlParam == null) {
            return mapRequest;
        }
        // 每个键值为一组 www.2cto.com
        arrSplit = strUrlParam.split("&");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("=");

            // 解析出键值
            if (arrSplitEqual.length > 1) {
                // 正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (!arrSplitEqual[0].isEmpty()) {
                    // 只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 参数名的替换(去掉每个参数名开始的”nns”和里面的下划线”_”，在每个下划线后的字母用大写，规则与驼峰命名一致)
     *
     * @param str  需要替换的字符串
     * @param flag 是否删除第一个“_”的字符(例如：参数删除“nns_”)
     * @return
     */
    private static String generationString(String str, boolean flag) {
        if (StringUtil.isEmpty(str)) {
            return "";
        }
        String[] array = str.split("_");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (flag) {
                if (i != 0) {
                    sb.append(captureName(array[i]));
                }
            } else {
                sb.append(captureName(array[i]));
            }
        }
        return sb.toString();
    }

    /**
     * 首字母大写
     *
     * @param name
     * @return
     */
    @SuppressLint("DefaultLocale")
    private static String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

    public static String getParams(Map<String, String> paramsMap, HttpItem.EncodeType type) {
        String encodeType = "";
        if (type == HttpItem.EncodeType.GB2312) {
            encodeType = "gb2312";
        } else {
            encodeType = "utf-8";
        }
        Set<String> keySets = paramsMap.keySet();
        Iterator<String> i = null;
        if (keySets != null) {
            i = keySets.iterator();

            if (keySets.size() == 1) {
                String temp = i.next();
                if (!StringUtil.isEmpty(temp)) {
                    try {
                        return temp + "=" + URLEncoder.encode(paramsMap.get(temp), encodeType);
                    } catch (UnsupportedEncodingException e) {
                        return temp + "=" + paramsMap.get(temp);
                    }
                } else {
                    return "";
                }
            } else if (keySets.size() > 1) {
                StringBuffer sb = new StringBuffer();
                String temp = i.next();
                if (!StringUtil.isEmpty(temp)) {
                    try {
                        sb.append(temp + "=" + URLEncoder.encode(paramsMap.get(temp), encodeType));
                    } catch (UnsupportedEncodingException e) {
                        sb.append(temp + "=" + paramsMap.get(temp));
                    } catch (NullPointerException nulle) {
                        nulle.printStackTrace();
                    }
                }
                while (i.hasNext()) {
                    temp = i.next();
                    if (!StringUtil.isEmpty(temp)) {
                        try {
                            sb.append("&" + temp + "=" + URLEncoder.encode(paramsMap.get(temp), encodeType));
                        } catch (UnsupportedEncodingException e) {
                            sb.append("&" + temp + "=" + paramsMap.get(temp));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return sb.toString();
            } else {
                return "";
            }
        }
        return "";
    }

    public static boolean isPkgInstalled(Context context, String packageName) {
        MLog.i("isPkgInstalled");
        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static boolean enableSpecailInstall() {

        if (getDeviceModel().equals(B860AV_SPECAIL_GAO_AN_MOEDL)) {
            return true;
        }
        return true;
    }

    /**
     * 设置文件可读写权限
     *
     * @param apkFile
     */
    public void makeFileAccess(File apkFile) {
        String[] command = {COMMAND_STR_CHMOD, COMMAND_STR_FILE_RWD, apkFile.getPath()};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除缓存
     */
    public static void clearApp() {
        String[] command = {COMMAND_PM, COMMAND_CEALR, installPackageName};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public static void uninstallApp() {
        String[] command = {COMMAND_PM, COMMAND_UNINSTALL, installPackageName};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除app安装目录
     */
    public static void rmAppData() {
        String[] command = {COMMAND_RM, COMMAND_FR, APP_DATA_DIR};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
