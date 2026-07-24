import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
dependencies {
    implementation(projects.composeApp)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.unwur.tabiatmu"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

//    val keyProperties =
//        Properties().apply {
//            val propsFile = rootProject.file("keystore.properties")
//            if (propsFile.exists()) {
//                load(propsFile.inputStream())
//            }
//        }
//
//    signingConfigs {
//        create("release") {
//            keyAlias = keyProperties["keyAlias"].toString()
//            keyPassword = keyProperties["keyPassword"].toString()
//            storeFile = file(keyProperties["storeFile"].toString())
//            storePassword = keyProperties["storePassword"].toString()
//        }
//    }

    defaultConfig {
        applicationId = "com.unwur.tabiatmu"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 11
        versionName = "1.8.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
            excludes += "META-INF/versions/**"
            excludes += "META-INF/INDEX.LIST"
        }
    }
    buildTypes {
        debug {
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".debug"
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
//            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    lint {
        quiet = true
        abortOnError = false
    }
}
