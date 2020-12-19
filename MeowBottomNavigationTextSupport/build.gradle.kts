import com.jfrog.bintray.gradle.BintrayExtension
import meow.AppConfig
import meow.AppConfig.Dependencies
import meow.AppConfig.Publishing
import meow.AppConfig.Versions
import meow.getPropertyAny
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
}

group = Publishing.groupId
version = AppConfig.generateVersionName()

android {
    compileSdkVersion(Versions.SDK_COMPILE)

    defaultConfig {
        minSdkVersion(Versions.SDK_MIN)
        targetSdkVersion(Versions.SDK_TARGET)

        versionCode = AppConfig.generateVersionCode()
        versionName = AppConfig.generateVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    lintOptions {
        isAbortOnError = false
        isIgnoreWarnings = true
        disable("MissingDefaultResource")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("main") {
            setRoot(meow.AppConfig.Build.SRC_MAIN)
            manifest.srcFile("${meow.AppConfig.Build.SRC_MAIN}AndroidManifest.xml")

            java.srcDirs("${meow.AppConfig.Build.SRC_MAIN}kotlin")
            java.includes.add("/${meow.AppConfig.Build.SRC_MAIN}**")
            java.excludes.add("**/build/**")

            println((res == null))
            res.srcDirs(meow.getAllResourcesSrcDirs(project, true))
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Implementation Dependencies
    Dependencies.implementationItems.forEach {
        implementation(it)
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

val publicationName = "maven"

/*
Publishing a Kotlin library to your Bintray repo using Gradle Kotlin DSL
https://medium.com/@sergio.igwt/publishing-a-kotlin-library-to-your-bintray-repo-using-gradle-kotlin-dsl-bdeaed54571a
https://github.com/nwillc/kretry/blob/master/build.gradle.kts
*/
sourceSets.create("main") {
    java.srcDirs("${meow.AppConfig.Build.SRC_MAIN}kotlin")
    java.includes.add("/${meow.AppConfig.Build.SRC_MAIN}**")
    java.excludes.add("**/build/**")
    resources.srcDirs("${meow.AppConfig.Build.SRC_MAIN}res")
}

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            groupId = project.group.toString()
            artifactId = Publishing.artifactId
            version = project.version.toString()

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                val configurationNames = arrayOf("implementation", "api")
                configurationNames.forEach { c ->
                    configurations[c].allDependencies.forEach {
                        if (it.group != null) {
                            val dependencyNode = dependenciesNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", it.group)
                            dependencyNode.appendNode("artifactId", it.name)
                            dependencyNode.appendNode("version", it.version)
                        }
                    }
                }
            }

            val sourcesJar by tasks.registering(Jar::class) {
                archiveClassifier.convention("sources")
                from(project.sourceSets["main"].allSource)
            }

            artifact("$buildDir/outputs/aar/MeowBottomNavigation-release.aar")
            artifact(sourcesJar.get())
        }
    }
}

bintray {
    user = getPropertyAny("bintray.user")
    key = getPropertyAny("bintray.key")

    dryRun = false
    publish = true
    setPublications(publicationName)
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        name = Publishing.name
        repo = Publishing.repo
        userOrg = Publishing.userOrg
        desc = Publishing.libraryDesc
        websiteUrl = "https://github.com/${Publishing.developerId}/${Publishing.name}"
        issueTrackerUrl = "https://github.com/${Publishing.developerId}/${Publishing.name}/issues"
        vcsUrl = "https://github.com/${Publishing.developerId}/${Publishing.name}.git"
        version.vcsTag = "v${project.version}"
        setLicenses("Apache-2.0")
        setLabels(
            "kotlin",
            "meow-bottom-navigation",
            "android",
            "library",
            "bottom-navigation",
            "material"
        )
        publicDownloadNumbers = true
    })
}