# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#指定代码的压缩级别
-optimizationpasses 5
 #预校验
-dontpreverify
-printmapping proguardMapping.txt
# 指定混淆时采用的算法，后面的参数是一个过滤器
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*,InnerClasses
-keep class * extends java.lang.annotation.Annotation { *; }
-keep class com.iflytek.**{*;}
-keepattributes *Annotation*
-keep class cn.qqtheme.framework.entity.** { *;}
#避免混淆泛型
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-ignorewarnings     #忽略警告
 #混淆时是否记录日志
-verbose
#包明不混合大小写
-dontusemixedcaseclassnames
#优化  不优化输入的类文件
-dontoptimize
#---------------------------------默认保留区---------------------------------
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
#-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
#-----------实体类---------
-keep public class com.pharos.walker.beans.**{*;}
#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#Parcelable实现类除了不能混淆本身之外，为了确保类成员也能够被访问，类成员也不能被混淆
-keepclassmembers class * implements android.os.Parcelable {
 public <fields>;
 private <fields>;
}
#不混淆资源类
-keep class **.R$* {
 *;
}

-keepclassmembers class * {
    void *(*Event);
}


#// natvie 方法不混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
#GreenDao
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
#socket
-keep class **$Properties{*;}
-dontwarn com.xuhao.didi.socket.client.**
-dontwarn com.xuhao.didi.socket.common.**
-dontwarn com.xuhao.didi.socket.server.**
-dontwarn com.xuhao.didi.core.**

-keep class com.xuhao.didi.socket.client.** { *; }
-keep class com.xuhao.didi.socket.common.** { *; }
-keep class com.xuhao.didi.socket.server.** { *; }
-keep class com.xuhao.didi.core.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.xuhao.didi.socket.client.sdk.client.OkSocketOptions$* {
    *;
}
-keep class com.xuhao.didi.socket.server.impl.OkServerOptions$* {
    *;
}
-assumenosideeffects class android.util.Log {

public static int v(...);

public static int i(...);

public static int w(...);

public static int d(...);

public static int e(...);

}
