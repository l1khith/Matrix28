# Production ProGuard / R8 Rules for Matrix28

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keepclassmembers class * extends androidx.compose.ui.node.Owner { *; }
-dontwarn androidx.compose.**

# Keep Data Models & Schemas
-keep class com.l1khith.matrix28.data.** { *; }
-keepclassmembers class com.l1khith.matrix28.data.** { *; }

# Keep Receivers & Entry Points
-keep class com.l1khith.matrix28.receiver.** { *; }
-keep class com.l1khith.matrix28.utils.** { *; }
-keep class com.l1khith.matrix28.MainActivity { *; }

# Keep SQLite & System Services
-keep class android.database.sqlite.** { *; }

# Keep Attributes for Reflection & Debugging
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Optimization Settings
-optimizationpasses 5
-repackageclasses ''
-allowaccessmodification