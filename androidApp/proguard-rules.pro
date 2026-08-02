# Optimized Production ProGuard / R8 Rules for Matrix28

# Keep Data Models & Serialized Objects
-keep class com.l1khith.matrix28.data.** { *; }
-keepclassmembers class com.l1khith.matrix28.data.** { *; }

# Keep Receivers & Main Activity Entry Point
-keep class com.l1khith.matrix28.receiver.** { *; }
-keep class com.l1khith.matrix28.MainActivity { *; }

# Don't warn for Compose or Kotlin internal metadata
-dontwarn androidx.compose.**
-dontwarn kotlin.**

# Keep Attributes for Stack Traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature

# R8 Optimization & Code Shrinking Passes
-optimizationpasses 5
-repackageclasses ''
-allowaccessmodification