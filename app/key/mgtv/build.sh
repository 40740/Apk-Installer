#!/bin/bash
export LANG="en_US.UTF-8"

#发送编译成功邮件
function sendFailureMail(){
    python /home/ubuntu/data/nunaios/jenkins_script/othertools/sendmail.py "【TV APP ${APP_VERSION} ${BUILD_CHANNELS}渠道版本构建邮件】" $1 "${EMAIL_TO}" "liyang3@mgtv.com"
}

#创建目录
function mkdirFun(){
    if test -e $1;  
    then 
        echo $1 exists  
    else 
        echo $1 does not exist
        mkdir -p $1
    fi 
}

EMAIL_TO="liyang3@mgtv.com"
EMAIL_CC="liyang3@mgtv.com"
#去掉渠道列表数组中的""
channels=`echo "$BUILD_CHANNELS" | sed 's/\"//g'`
#编译时间
build_time=$(date +%Y%m%d%H%M)
#文件服务器下载地址
download_path="http://10.1.201.95/ott/${PARTNER_NUMBER}/apk/tv_app/${BUILD_BRANCH}-${channels}-${build_time}"
#远端服务器存储地址
des_dir="/data/nunaios/jenkins_out/ott/${PARTNER_NUMBER}/apk/tv_app/${BUILD_BRANCH}-${channels}-${build_time}/"
workspace_dir="/home/ubuntu/data/nunaios/jenkins_script/OTT-TVApp-Portal-Partner"
#工程根目录
src_root_dir="${workspace_dir}/OTT-TVAPP-Portal"
repo_dir="${workspace_dir}/.repo"
#jenkins编译生成文件目录
src_dir="${src_root_dir}/TVApp/build/outputs"
#发布版本存储目录
release_apk_dir="/data/nunaios/jenkins_out/ott/partner/release_apk/tv_app/${APP_VERSION}"

#打印当前脚本所在绝对路径
echo current path is $0

cd ${workspace_dir}

#删除工程
if test -e ${src_root_dir} 
then 
    echo ${src_root_dir} exists  
    rm -rf ${src_root_dir}
else 
    echo ${src_root_dir} does not exist
fi 

#删除.repo文件
if test -e ${repo_dir}
then
	echo ${repo_dir} exists
    rm -rf ${repo_dir}
else
	echo ${repo_dir} does not exist
fi
        
        
#更新repo及manifest文件
if [ $PARTNER_NUMBER == "pukka" ]; then
	repo init -u ssh://guangbin@152.136.58.123:29418/pukka/manifest -m tv_app/${BUILD_BRANCH}.xml --repo-url ssh://guangbin@152.136.58.123:29418/platform/new/repo
elif [ $PARTNER_NUMBER == "archermind" ]; then
	repo init -u ssh://guangbin@152.136.58.123:29418/archermind/manifest -m tv_app/${BUILD_BRANCH}.xml --repo-url ssh://guangbin@152.136.58.123:29418/platform/new/repo
fi

if [ $? -ne 0 ]; then
    echo "repo init gerrit代码失败"
    sendFailureMail "repo init gerrit代码失败!"
    exit
fi

#同步代码
repo sync

if [ $? -ne 0 ]; then
    echo "同步gerrit代码失败"
    sendFailureMail "同步gerrit代码失败!"
    exit
fi

cd ~
#拷贝local.properties到工程目录下
cp /home/ubuntu/data/nunaios/jenkins_script/othertools/local.properties ${src_root_dir}/
#拷贝签名文件目录
cp -r /home/ubuntu/data/nunaios/jenkins_script/othertools/key ${src_root_dir}/TVApp

#修改jenkins.properties文件内容
#如果jenkins.properties存在，则根据外包合作方修改jenkins打包属性值
if [[ test -e ${src_root_dir}/TVApp/jenkins.properties ]]; then
    if [[ $PARTNER_NUMBER == "pukka" ]]; then
        #帕科partnerCode为5
        echo "partnerCode=5" > ${src_root_dir}/TVApp/jenkins.properties
    elif [[ $PARTNER_NUMBER == "archermind" ]]; then
        #帕科partnerCode为6
        echo "partnerCode=6" > ${src_root_dir}/TVApp/jenkins.properties
    fi
fi

#进入工程目录，编译apk
cd ${src_root_dir}

echo "channels="$channels
for var in `echo "$channels" | sed 's/,/\n/g'` ;  
do  
    if [ $var = "ALL" ] 
    then
        ./gradlew assemble${BUILD_TYPE_NAME}
    else 
        ./gradlew assemble${var}${BUILD_TYPE_NAME}
    fi  
done   

#加固,渠道数必须等于1才能加固（目前不支持批量加固）
if [[ ${IS_JIAGU} == true && ${#channels[*]} -eq 1 ]]; then
    #解析apk名称获取版本名称 HunanOTT_V5.9.201.389.3.DBSN_TVAPP.0.0_Release.apk   
    sh /home/ubuntu/data/nunaios/jenkins_script/othertools/app_reinforce.sh $src_dir ${channels[0]} $BUILD_TYPE_NAME $build_time
    
    #apk重新签名
    build_type_name=release
    if [[ $BUILD_TYPE_NAME == Release ]]; then
        build_type_name=release
    elif [[ $BUILD_TYPE_NAME == PreRelease ]]; then
        build_type_name=preRelease
    fi
    
    sh /home/ubuntu/data/nunaios/jenkins_script/othertools/app_signer.sh $src_dir/apk/${channels[0]}/$build_type_name/*.apk $src_dir/apk/${channels[0]}/$build_type_name/*.apk $Build_Sign_Type
fi


if [ $? -ne 0 ]; then
    echo "${channels}渠道apk编译失败"
    sendFailureMail "${channels}渠道apk编译失败!"
    exit
fi

#判断远程服务器目录是否存在，不存在则创建目录
mkdirFun ${des_dir}
#回到jenkins根目录
cd ~
#拷贝jenkins编译apk目录下所有文件到远端服务器
count=`ls ${src_dir}/apk | wc -w`
echo "==========apk count : ${count}"
cp -r ${src_dir}/apk ${des_dir}
#拷贝jenkins编译mapping文件到远端服务器
cp -r ${src_dir}/mapping ${des_dir}

if [ $? -ne 0 ]; then
    echo "${channels}渠道apk拷贝失败"
    sendFailureMail "${channels}渠道apk拷贝失败!"
    exit
fi

echo "IS_SAVE_RELEASE_APK="${IS_SAVE_RELEASE_APK}",BUILD_BRANCH="${BUILD_BRANCH}",BUILD_TYPE_NAME="${BUILD_TYPE_NAME}
if [[ ${IS_SAVE_RELEASE_APK} == true && ${BUILD_BRANCH} =~ "release" && ${BUILD_TYPE_NAME} == "Release" ]];then
    for var in `echo "$channels" | sed 's/,/\n/g'` ;  
    do
        echo "save release apk,branch:"${var}
        mkdirFun ${release_apk_dir}/${var}
        cp -f ${src_dir}/apk/${var}/$(echo $BUILD_TYPE_NAME | tr '[A-Z]' '[a-z]')/*.apk ${release_apk_dir}/${var}
        mkdirFun ${release_apk_dir}/${var}/
        cp -f ${src_dir}/mapping/${var}/$(echo $BUILD_TYPE_NAME | tr '[A-Z]' '[a-z]')/mapping.txt ${release_apk_dir}/${var}  
    done
fi

#发送编译成功邮件
python /home/ubuntu/data/nunaios/jenkins_script/othertools/sendmail.py "【TV APP ${channels}渠道版本构建邮件】" "jenkins打包输出，下载地址：${download_path}" "${EMAIL_TO}" "${EMAIL_CC}"