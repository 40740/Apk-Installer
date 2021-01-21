package net.dlb.aidl;

//3.  卸载回调接口： 名称： UnInstallCallback
interface UnInstallCallback {
//   调用方： 服务框架
//   接收方： 移动商店（或其他第三方应用）
//   函数：onUnInstallResult( Int result, String msg)
//        参数说明：
//             result： 卸载结果： 0：成功， 1:失败
//             msg： 结果描述（如，失败原因）
   void onUnInstallResult( int result, String msg);
}
