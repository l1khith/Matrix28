# Production ProGuard / R8 Rules for Matrix 28

# Keep Data Models & Serialized Objects
-keep class com.l1khith.matrix28.data.** { *; }
-keepclassmembers class com.l1khith.matrix28.data.** { *; }

# Keep Receivers & Main Activity Entry Point
-keep class com.l1khith.matrix28.receiver.** { *; }
-keep class com.l1khith.matrix28.MainActivity { *; }

# Keep AdMob / Google Play Services Ads
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# Don't warn for Compose or Kotlin internal metadata
-dontwarn androidx.compose.**
-dontwarn kotlin.**

# Keep Attributes for Stack Traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature