    package net.dlb.aidl;

    import net.dlb.aidl.InstallCallback;
    import net.dlb.aidl.UnInstallCallback;

    //1.  安装服务名称：InstallService
    interface InstallService {
    //   调用方： 移动应用商店（或其他第三方应用）
    //   接收方： 服务框架
    //        参数说明：
    //             apkPath: 要安装的apk文件路径
    //             appName: 调用方的应用名称（由移动平台端分配）
    //             appCode: 调用方的应用编码（由移动平台端分配）
    //             cb: 回调通知对象
       void install(String apkPath, String appName, String appCode, InstallCallback cb);


    //   调用方： 移动应用商店（或其他第三方应用）
    //   接收方： 服务框架
    //        参数说明：
    //             packageName:  要卸载的应用包名
    //             appName: 调用方的应用名称（由移动平台端分配）
    //             appCode: 调用方的应用编码（由移动平台端分配）
    //             cb: 回调通知对象
       void uninstall(String packageName, String appName, String appCode, UnInstallCallback cb);
    }
