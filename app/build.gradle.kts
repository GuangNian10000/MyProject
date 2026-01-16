import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

apply(from = "configs.gradle.kts")
val LOG_ENABLE: Boolean by extra
val HOST_URL: String by extra

android {
    namespace = "com.guangnian.demo"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.guangnian.demo"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 日志打印开关
        buildConfigField("boolean", "LOG_ENABLE", "$LOG_ENABLE")
        buildConfigField("String", "HOST_URL", "\"$HOST_URL\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget("21"))
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":mvvm"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.gsonfactory)
    implementation(libs.material)

    //dialog
    implementation("com.afollestad.material-dialogs:lifecycle:3.3.0")
    implementation("com.afollestad.material-dialogs:core:3.3.0")
    implementation("com.afollestad.material-dialogs:color:3.3.0")
    implementation("com.afollestad.material-dialogs:datetime:3.3.0")
    implementation("com.afollestad.material-dialogs:bottomsheets:3.3.0")

    implementation("com.geyifeng.immersionbar:immersionbar:3.2.2") //状态栏导航栏 https://github.com/gyf-dev/ImmersionBar
    implementation("com.geyifeng.immersionbar:immersionbar-ktx:3.2.2")


    implementation("com.tencent:mmkv:2.3.0") //微信开源 https://github.com/Tencent/MMKV/blob/master/README_CN.md

    implementation("com.github.liangjingkanji:BRV:1.6.1") //rv https://github.com/liangjingkanji/BRV

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2") //net https://github.com/liangjingkanji/Net
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.github.liangjingkanji:Net:3.7.0")

    implementation("com.github.getActivity:EasyWindow:10.6") //悬浮窗框架：https://github.com/getActivity/EasyWindow

    implementation("com.jakewharton.timber:timber:5.0.1") //日志打印框架：https://github.com/JakeWharton/timber

    implementation("com.github.getActivity:Toaster:13.8") //吐司框架：https://github.com/getActivity/Toaster

    implementation("com.github.getActivity:DeviceCompat:2.5") // 设备兼容框架：https://github.com/getActivity/DeviceCompat

    implementation("com.github.getActivity:XXPermissions:28.0") // 权限请求框架：https://github.com/getActivity/XXPermissions

    implementation("io.github.lucksiege:pictureselector:v3.11.2") //图片选择器 https://github.com/LuckSiege/PictureSelector/blob/version_component/README_CN.md

    implementation ("com.github.bumptech.glide:glide:5.0.5") // 图片加载框架：https://github.com/bumptech/glide
    kapt("com.github.bumptech.glide:compiler:5.0.5") //官方使用文档：https://github.com/Muyangmin/glide-docs-cn

    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.21") //反射

    implementation("com.alibaba:fastjson:1.2.83") //gson解析
}