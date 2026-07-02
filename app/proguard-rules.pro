# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Firebase recommended rules
-keep class com.google.firebase.** { *; }
-keep class * implements com.google.firebase.firestore.EventListener { *; }

# Hilt/Dagger rules
-keep class dagger.** { *; }
-keep class * extends dagger.internal.Factory { *; }
-keep class * extends dagger.hilt.internal.ComponentProvider { *; }

# Domain models need to be preserved for Firestore serialization
-keep class com.sportclubai.domain.model.** { *; }
