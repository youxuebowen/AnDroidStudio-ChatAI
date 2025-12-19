plugins {
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//    id("com.google.devtools.ksp")
//    kotlin("plugin.compose")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    

    namespace = "com.bytedance.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bytedance.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.8"
//    }


    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources.excludes.add("META-INF/atomicfu.kotlin_module")
    }

    // 自定义APK文件名
    applicationVariants.all {
        val variantName = name
        val versionName = versionName
        val versionCode = versionCode
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                this.outputFileName = "万博闻-Chatbot.apk"
            }
        }
    }
}

dependencies {
    val room_version = "2.6.1"
    implementation("com.google.mlkit:translate:17.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")


    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("androidx.navigation:navigation-compose:2.7.5") // 检查最新的稳定版本
    // ViewModel for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Room Database
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Network - Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.5.0")
    // implementation("io.coil-kt:coil:2.4.0") // 移除重复/旧版本，coil-compose 已经包含了 coil

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // 1. CameraX 相机库 (Kotlin DSL 写法修正)
    val camerax_version = "1.3.0-beta01"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")

    // 2. Google ML Kit - 图片标签识别 (离线版)
    implementation("com.google.mlkit:image-labeling:17.0.7")
    
    // 权限管理 (推荐使用 Accompanist 库)
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
    // 移除底部重复的依赖声明，因为上面已经声明了更新的版本 (如 Coroutines 1.7.3 > 1.7.1, OkHttp 4.12.0 > 4.11.0)
}