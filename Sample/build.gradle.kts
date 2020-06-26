import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import meow.AppConfig
import meow.AppConfig.Build
import meow.AppConfig.Dependencies
import meow.AppConfig.Versions
import meow.getAllResourcesSrcDirs

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Versions.SDK_COMPILE)

    defaultConfig {
        applicationId = Build.APPLICATION_ID + "_sample"
        minSdkVersion(Versions.SDK_MIN)
        targetSdkVersion(Versions.SDK_TARGET)

        versionCode = AppConfig.generateVersionCode()
        versionName = AppConfig.generateVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        setConsumerProguardFiles(kotlin.arrayOf("consumer-rules.pro"))

        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        dataBinding = true
    }

    applicationVariants.all {
        outputs.all {
            val fileName = "Meow-BottomNavigation-Sample-v${AppConfig.generateVersionName()}.apk"
            (this as BaseVariantOutputImpl).outputFileName = fileName
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    lintOptions {
        isAbortOnError = false
        isIgnoreWarnings = true
        disable("MissingDefaultResource")
    }

    sourceSets {
        getByName("main") {
            setRoot(Build.SRC_MAIN)
            manifest.srcFile("${Build.SRC_MAIN}AndroidManifest.xml")

            java.srcDirs("${Build.SRC_MAIN}kotlin")
            java.includes.add("/${Build.SRC_MAIN}**")
            java.excludes.add("**/build/**")

            res.srcDirs(getAllResourcesSrcDirs(project))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":MeowBottomNavigation"))

    // Implementation Dependencies
    Dependencies.implementationItems.forEach {
        implementation(it)
    }

}
