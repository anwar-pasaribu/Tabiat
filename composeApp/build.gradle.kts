import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.spotless)
}

kotlin {

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.unwur.tabiatmu.composeApp")
        }
    }

    android {
        namespace = "com.unwur.tabiatmu.composeApp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {

        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("androidx.compose.material.ExperimentalMaterialApi")
            languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)

//            implementation(libs.androidx.core.splashscreen)

            implementation(libs.sqldelight.androidDriver)

            implementation("org.slf4j:slf4j-api:2.0.7")
            implementation("com.github.tony19:logback-android:3.0.0")

            // Import the BoM for the Firebase platform
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.1.2"))

            // Add the dependencies for the Crashlytics and Analytics libraries
            // When using the BoM, you don't specify versions in Firebase library dependencies
            implementation("com.google.firebase:firebase-crashlytics")
            implementation("com.google.firebase:firebase-analytics")
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.nativeDriver)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(compose.animation)
            implementation(compose.animationGraphics)

            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.androidx.lifecycle.viewmodel.nav3)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.nav3.ui)
            implementation(libs.androidx.material3.adaptive)
            implementation(libs.androidx.material3.adaptive.nav3)

            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel.navigation)

            implementation(libs.haze)
            implementation(libs.haze.materials)

            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitiveAdapters)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.json)

            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.kotlinx.atomicfu)

            implementation("com.soywiz.korge:korge-core:6.0.0")

            implementation("io.github.thechance101:chart:Beta-0.0.5")

            implementation("com.mohamedrejeb.calf:calf-permissions:0.9.0")

            // Handle Error
            // Task :composeApp:compileKotlinIosX64 FAILED
            // e: Could not find "co.touchlab:stately-concurrent-collections"
            implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
            implementation("co.touchlab:stately-concurrent-collections:2.0.6")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

sqldelight {
    databases {
        create("TabiatDatabase") {
            packageName.set("com.unwur.tabiatmu.database")
        }
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        targetExclude("${layout.buildDirectory}/**/*.kt")
        targetExclude("bin/**/*.kt")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_standard_filename" to "disabled",
                "ktlint_standard_function-naming" to "disabled",
            ),
        )
        licenseHeaderFile(rootProject.file("licenses/MIT"))
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
    }
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}
