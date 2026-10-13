# ==============================================================================
# PROGUARD / R8 CONFIGURATION — E-LKPD ETNOSAINS PARIJOTO (PHASE 18)
# ==============================================================================

# 1. Pertahankan model data serialisasi (Gson / Kotlinx Serialization / Retrofit)
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

-keep class com.example.domain.model.** { *; }
-keep class com.example.data.local.entity.** { *; }
-keep class com.example.data.remote.dto.** { *; }
-keep class com.example.model.** { *; }

# 2. Room Database Rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# 3. Application & Dependency Injection
-keep class com.example.ParijotoApp { *; }
-keep class com.example.di.** { *; }

# 4. WorkManager & CoroutineWorker
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# 5. Kotlin Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.flow.**

# 6. Jetpack Compose & Navigation
-keep class androidx.compose.material3.** { *; }

# 7. Hilangkan Log Debugging pada Build Produksi (Release) untuk Keamanan
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
