package net.dlb.aidl;

//2. 安装回调接口： 名称： InstallCallback
interface InstallCallback {

//   调用方： 服务框架
//   接收方： 移动商店（或其他第三方应用）
//        参数说明：
//             result： 安装结果： 0：成功， 1:失败
//             msg： 结果描述（如，失败原因）

   void onInstallResult( int result, String msg);
}
