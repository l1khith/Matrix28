# Optimized Production ProGuard / R8 Rules for Matrix28

# Keep Data Models & Serialized Objects
-keep class com.l1khith.matrix28.data.** { *; }
-keepclassmembers class com.l1khith.matrix28.data.** { *; }

# Keep Receivers & Main Activity Entry Point
-keep class com.l1khith.matrix28.receiver.** { *; }
-keep class com.l1khith.matrix28.MainActivity { *; }

# Keep WorkManager & Room Database Reflection Classes
-keep class androidx.work.impl.WorkDatabase_Impl {
    public <init>();
}
-keep class * extends androidx.work.impl.WorkDatabase {
    public <init>();
}
-keep class * extends androidx.work.Worker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Keep AdMob / Google Play Services Ads
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# Don't warn for Compose or Kotlin internal metadata
-dontwarn androidx.compose.**
-dontwarn kotlin.**

# Keep Attributes for Stack Traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature