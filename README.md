# 阿里Atlas淘宝动态组件化框架详细攻略

官方详细文档：
https://github.com/alibaba/atlas/tree/master/atlas-demo


如果自定义，DemoApplication不能丢掉。

1、 app的build.gradle的语句"version = getEnvValue("versionName", "1.0.0");"中修改想要生成的app的versionName（默认为1.0.0）

    app目录下执行../gradlew clean assembleDebug publish

    (生成apk同时将跟apk同目录的ap文件发布到仓库)

2、 手机上安装apk，同时进到动态部署界面（侧边栏里面划开点击进入),且手机连接电脑adb（确保adb devices可见）

///////////////////////////////^^^^^^^准备工作^^^^^^^^^^////////////////////////

3、 进行一些想要的修改（暂时不支持manifest的修改，会在近期上线）

4、 app工程目录下执行../gradlew clean assembleDebug -DapVersion=apVersion -DversionName=newVersion,
    其中apVersion为之前打的完整apk的版本，newVersion为此次动态部署要生成的新的版本号

需要注意的是：
1. -DapVersion=apVersion -DversionName=newVersion 大小写不能出错，否则不能生成patch文件。
2. 此方式目前只能更新app主模块的逻辑代码，不能修改app主模块的布局文件。（待验证）



5、 检查build/output/tpatch-debug 目录下文件是否生成，然后执行下面的命令(以下为mac下的命令，windows请修改文件分隔符)
    adb push build/outputs/tpatch-debug/update.json /sdcard/Android/data/com.taobao.demo/cache/update.json
    adb push build/outputs/tpatch-debug/patch-*.tpatch /sdcard/Android/data/com.taobao.demo/cache

点击动态部署页面红色按钮执行动态部署

6.单bundle调试（供线下调试使用，当只更改了单个bundle的代码时，无需对整个APP进行动态部署，可以一键进行单bundle的部署调试）

bundle工程的目录下执行`../gradlew clean assemblePatchDebug`，然后等应用重启或者应用关闭后点击重启")就可以看到代码生效。 

如果遇到下面错误：

Error:Execution failed for task ':app:mergeDebugResources'. > Error: Java.util.concurrent.ExecutionException: com.Android.ide.common.process.ProcessException:


在gradle的android{ ... } 中加入这两句就可以了

android {
    
   ......

    aaptOptions.cruncherEnabled = false
    aaptOptions.useNewCruncher = false

   ......
}

7. Execution failed for task ':app:prepareDebugAP'.

buildTypes {
        debug {
            minifyEnabled false
            zipAlignEnabled true
            signingConfig signingConfigs.debug
        }

        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }


8.bundle需要注意,gralde里面增加如下配置

apply plugin: 'com.taobao.atlas.library'

// 如果要修改资源文件动态更新Bundle需要添加
atlas {
    bundleConfig{
        awbBundle true
    }
    buildTypes {
        debug {
            baseApFile project.rootProject.file('app/build/outputs/apk/app-debug.ap')
        }
    }
}
