import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.androidx.core.splashscreen)

            implementation(libs.sqldelight.androidDriver)

            implementation(libs.ktor.client.okhttp)
            implementation("org.slf4j:slf4j-simple:2.0.7")
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.nativeDriver)

            implementation(libs.ktor.client.darwin)

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.haze)
            implementation(libs.haze.materials)

            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitiveAdapters)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.json)

            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)

//            implementation("io.coil-kt.coil3:coil:3.0.0-alpha06")
            implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha06")
            implementation("io.coil-kt.coil3:coil-network-ktor:3.0.0-alpha06")

            // Handle Error
            // Task :composeApp:compileKotlinIosX64 FAILED
            // e: Could not find "co.touchlab:stately-concurrent-collections"
            implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
            implementation("co.touchlab:stately-concurrent-collections:2.0.6")
        }

    }
}

android {
    namespace = "com.unwur.tabiatmu"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    val keyProperties =
        Properties().apply {
            val propsFile = rootProject.file("keystore.properties")
            if (propsFile.exists()) {
                load(propsFile.inputStream())
            }
        }

    signingConfigs {
        create("release") {
            keyAlias = keyProperties["keyAlias"].toString()
            keyPassword = keyProperties["keyPassword"].toString()
            storeFile = file(keyProperties["storeFile"].toString())
            storePassword = keyProperties["storePassword"].toString()
        }
    }

    defaultConfig {
        applicationId = "com.unwur.tabiatmu"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    lint {
        quiet = true
        abortOnError = false
    }
}

sqldelight {
    databases {
        create("TabiatDatabase") {
            packageName.set("com.unwur.tabiatmu.database")
        }
    }
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}
