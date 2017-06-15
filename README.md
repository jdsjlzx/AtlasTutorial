# 阿里Atlas淘宝动态组件化框架详细攻略

官方详细文档：

https://github.com/alibaba/atlas/tree/master/atlas-demo

http://atlas.taobao.org/docs/

如果自定义，DemoApplication不能丢掉。

1、 app的build.gradle的语句"version = getEnvValue("versionName", "1.0.0");"中修改想要生成的app的versionName（默认为1.0.0）

    app目录下执行../gradlew clean assembleDebug publish

    (生成apk同时将跟apk同目录的ap文件发布到仓库)

2、 手机上安装apk，同时进到动态部署界面（侧边栏里面划开点击进入),且手机连接电脑adb（确保adb devices可见）

///////////////////////////////^^^^^^^准备工作^^^^^^^^^^////////////////////////

3、 进行一些想要的修改（暂时不支持manifest的修改，会在近期上线）

4、 app工程目录下执行../gradlew clean assembleDebug -DapVersion=apVersion -DversionName=newVersion,
    其中apVersion为之前打的完整apk的版本，newVersion为此次动态部署要生成的新的版本号

    比如： ../gradlew clean assembleDebug -DapVersion=1.0.0 -DversionName=1.0.1

需要注意的是：
```
-DapVersion=apVersion -DversionName=newVersion 大小写不能出错，否则不能生成patch文件。
```

5、 检查build/output/tpatch-debug 目录下文件是否生成，然后执行下面的命令(以下为mac下的命令，windows请修改文件分隔符)

```
    adb push build/outputs/tpatch-debug/update-1.0.0.json /sdcard/Android/data/com.xylife.trip/cache/update-1.0.0.json
    adb push build/outputs/tpatch-debug/patch-1.0.1@1.0.0.tpatch /sdcard/Android/data/com.xylife.trip/cache
```
点击动态部署按钮执行动态部署(本demo的动态部署按钮位于titlebar的左侧文字区域)

6.单bundle调试（供线下调试使用，当只更改了单个bundle的代码时，无需对整个APP进行动态部署，可以一键进行单bundle的部署调试）

bundle工程的目录下执行`../gradlew clean assemblePatchDebug`，然后等应用重启或者应用关闭后点击重启")就可以看到代码生效。 

如果遇到下面错误：

Error:Execution failed for task ':app:mergeDebugResources'. > Error: Java.util.concurrent.ExecutionException: com.Android.ide.common.process.ProcessException:


在gradle的android{ ... } 中加入这两句就可以了
```groovy
android {
    
   ......

    aaptOptions.cruncherEnabled = false
    aaptOptions.useNewCruncher = false

   ......
}
```groovy

7. Execution failed for task ':app:prepareDebugAP'.
```groovy
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
```java

8.bundle需要注意,gralde里面增加如下配置

```java
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
```



部分 Atlas FAQ:

Q1: 关于bundle中的application类，它什么时候被调用，其中的自定义方法怎么在bundle中调用？在bundle中调用getApplication方法，得到的是主应用的application对象，不是bundle中定义的。

A: 首先要明确一点，bundle中的application不是最终在android系统上执行的application。我们之所以保留bundle application的设计是为了尽最大可能保留大家android的开发习惯。

bundle中的application类，是在bundle第一次安装的时候，atlas会调用application的onCreate()函数，可以在里面写一些自己bundle需要用到的初始化代码。注意不要bundle application里用this这种代码，因为是不生效的。

bundle中的application类的自定义方法，我们建议不要这么使用。可以抽离出公共的方法，放在其他地方供调用。 目前是有这个限制。如果非得在里面调用，建议改为static方法。

Q2: 集成框架后就只有uses-permission，小米推送需要permission就提示缺少权限声明。

A: 这是因为打包插件默认开启了去除自定义权限。 可通过属性开关关闭。 属性开关是atlas.manifestOptions.removeCustomPermission = false

Q3: proguard-rules.pro直接定义在宿主app里面就可以了吗，不需要每个bundle都使用吧？

A: 在宿主app里边定义就好了

Q4: 请问 客户端拿什么属性 跟服务器比对获取补丁？

A: 每一次补丁的发布都是相当于一次版本的升级，通过版本号的变更来决策补丁的下发

Q5: bundle中的9patch图显示有问题，不能拉伸了。是需要有特殊设置么？

A: alpha11版本发布解决了。

Q6: 对于gradle 2.3.1版本是不是支持？

A: 是的，支持2.3.x

Q7: 导入demo后，Android Studio编译按钮编译失败。

A: 关闭InstantRun可以正常工作。InstantRun支持正在开发中。

Q8: Unable to delete file E:\GitHub\atlas\atlas-demo\AtlasDemo\app\build\intermediates\exploded-aar\com.android.support\animated-vector-drawable\25.3.0\jars\classes.jar

A: 1. 杀掉java.exe进程重新编译（Windows 系统进程出现）cmd:taskkill /F /IM java.exe 2. 关闭掉as run on demand 开关

Q9. atlas 的能力问题， 及与 andfix 的关系

A: 1. atlas支持所有的代码及资源更新，暂时不支持新增4大组件，下个大版本支持 2. andfix只要用于修复java代码，不重启生效。 atlas能力更强，修复只是其中一部分能力

Q10. AS 上重新编译安装不生效的问题

A: atlas 在覆盖安装的时候，由于bundle的版本号没变化，没做清理， 下个版本会清理bundle

Q11. 动态部署不生效的问题

A: 不生效要看提示log再查，目前已知的已经修复，之前是由于windows句柄没关闭，导致tpatch中包含一些空文件，在dexmerge的时候失败了

Q12. 混淆后atlas进度条消失

A： keep 规则少了

Q13. 自启动的bundle是在哪个线程中啊，不是主线程吗？

A: 自启动bundle可以在任何线程中执行，不过bundle的application onCreate方法、Activity的onCreate方法等都是在主线程回调的

Q14. 在其他进程里运行的插件，需要在什么时候安装？可不可手动控制安装的时机？

A: 在需要的时候按需安装，例如一个Service是在单独的进程中，只要需要的时候去bind就会触发这个bundle的安装。可以手动控制安装时机，按需就好。

Q15. 组件中有jar包和so依赖，awb的产物中也有，为什么在宿主中生成的so产物解压就没有了呢？

A: 在打整包的时候需要配置过滤的参数，把so的架构类型指定清楚，要不然就会被裁减掉。具体可以参考 ： https://github.com/alibaba/atlas/issues/68

Q16. app有多个进程，在一个进程中出现了host中的一个类被pathClassloader加载了，之后在该类中调用Class.forName加载插件里的一个类，因为classloader是PathClassLoader,所以报了classnotfound,理论上host中除了atlas相关的类外所有的class都应该是被DelegateClassLoader加载吧？

A: 这种问题属于宿主中的class引用了bundle中的class，理论上是可以支持的，但是在Atlas的框架中是不推荐(也没不支持)这种反向的依赖关系。

Q17. altas是不是不建议bundle之间相互调用资源或调用宿主的资源?

A: Atlas框架建议bundle调用宿主的资源，而不建议bundle之间资源的相互调用。

Q18. 宿主和bundle的关系？

A: 宿主就相当于Android Framework，正常的Android代码中我们可以调用Framework中的接口以及使用其包含的资源，宿主对于bundle而言也是这个样子的，假如多个bundle使用同一个资源，那么这个资源可以放在宿主里边，多个bundle同时依赖这个宿主，这样避免了资源的重复。(甚至可以把宿主看成一个AAR，这个AAR的内容是打包在主dex中的)

Q19. Atlas项目中：AtlasDemo和基于版本打包的demo这两个项目有什么区别？

A: 一个是基于源码项目，一个是基于仓库版本依赖的。前期研究可以基于源码来，后续工程化实践推荐使用后者，手淘内部就是使用后者的。

Q20.  awb是什么？

A: awb是我们发明的格式，其内部结构和aar一样。 awb对应于bundle，编译最后的产物是生成到最后整包的so中。具体请参照 http://atlas.taobao.org/docs/principle-intro/Project_architectured.html


Q21. 出错提示：What went wrong:
org.gradle.api.UncheckedIOException: Could not read entry ':app:CreateDebugTPatch' from cache taskArtifacts.bin (xxxx\.gradle\3.3\tas
kArtifacts\taskArtifacts.bin).
> Could not read entry ':app:CreateDebugTPatch' from cache taskArtifacts.bin (xxxx\.gradle\3.3\taskArtifacts\taskArtifacts.bin).

A：这是因为gradle本身会做cache优化，当升级新的打包插件之后，可能会增加某些task，这时候老的gradle cache没有这个task，就会出错。
解法是，删除自己工程目录下的 .gradle 文件夹，这是个隐藏文件。

Q22.  运行就报FrameworkProperties这个类classNotFound？

A：FrameworkProperties这个类是承载动态部署信息的关键类，以前在老的atlas-core里定义，atlasplugin去修改里面的field。
新版atlas-core和atlasplugin我们做了改造，直接由atlasplugin打包时注入了，atlas-core不在包含 。如果发现这个问题，临时过度解决方案如下，配置打包参数atlas.tBuildConfig.classInject=false。后续我们会兼容新老版本交替的情况，请关注。
