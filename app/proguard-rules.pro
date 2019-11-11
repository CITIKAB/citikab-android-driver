# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/bowshulsheikrahaman/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
-keep class android.support.v7.widget.** { *; }

-keepclassmembers enum * {
   public static **[] values();
   public static ** valueOf(java.lang.String);
}


-ignorewarnings

#-keep class * {
#    public private *;
#}

#-keep public class

# Butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewInjector { *; }
-keep class DriverLocation
-keep class com.trioangle.goferdriver.map.DriverLocation**

-keepclasseswithmembernames class * {
    @butterknife.InjectView <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.OnClick <methods>;
    @butterknife.OnEditorAction <methods>;
    @butterknife.OnItemClick <methods>;
    @butterknife.OnItemLongClick <methods>;
    @butterknife.OnLongClick <methods>;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#For retrofit
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepattributes *Annotation*,SourceFile,LineNumberTable


-dontwarn com.squareup.okhttp.**

-keep class com.trioangle.goferdriver.firebaseChat.FirebaseChatModelClass{ *; }
-keep class com.trioangle.goferdriver.datamodel.NumberValidationModel{ *; }



-keep class com.trioangle.goferdriver.datamodel.**{ *; }
-keep class com.trioangle.goferdriver.home.**{ *; }
-keep class com.trioangle.goferdriver.map.**{ *; }


# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
#}
