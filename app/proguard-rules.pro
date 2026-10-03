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

# --- Project rules ---

# Retrofit reads a call's response type from the generic signature of the service method
# (e.g. Response<SessionDto>). R8 full mode removes DTO classes that no code references directly
# (DeleteSessionResponseDto is only used as a type argument) and rewrites the type argument to
# Object, so the kotlinx-serialization converter fails at runtime. Keep every DTO; allowobfuscation
# still lets R8 rename them, the serializers are kept by kotlinx-serialization's own rules.
-keep,allowobfuscation class com.mustafakocer.movieappfeaturebasedclean.feature.**.data.model.**
