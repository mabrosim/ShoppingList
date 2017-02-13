# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/extmabro/Library/Android/sdk/tools/proguard/proguard-android.txt
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
#}

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

-keep class fi.mabrosim.shoppinglist.data.records.** { *; }
-keep class android.support.v7.widget.SearchView { *; }

-dontwarn com.google.protobuf.**
-dontwarn java.beans.**
-dontwarn org.apache.**

-keep class * extends com.google.protobuf.GeneratedMessage { *; }
-keepclassmembernames class * extends com.google.protobuf.GeneratedMessage { *; }

-keep class fi.mabrosim.shoppinglist.protobuf.** {public private protected *;}

