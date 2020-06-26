/*
 * Copyright (C) 2020 Hamidreza Etebarian.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package meow

import org.gradle.api.Project
import java.io.File

/**
 * Build Application Configure object containing build info, dependencies, versioning.
 *
 * @author  Hamidreza Etebarian
 * @version 1.0.0
 * @since   2020-06-10
 */

object AppConfig {

    object Build {
        const val APPLICATION_ID = "meow.bottomnavigation"
        const val APP_MODULE = "Sample"
        const val APP_PACKAGE = "meow/bottomnavigation_sample"
        const val LIBRARY_MODULE = "MeowBottomNavigation"
        const val LIBRARY_PACKAGE = "meow.bottomnavigation"
        const val SRC_MAIN = "src/main/"

        enum class PHASE(var alias: String) {
            ALPHA("alpha"),
            BETA("beta"),
            CANARY("canary"),
            RC("rc"),
            STABLE("")
        }
    }

    object Versions {
        /*
             Semantic Versioning
                 by https://medium.com/@maxirosson/d6ec171cfd82
             and customized for supporting build type.
         */
        const val API = 1
        const val MAJOR = 1
        const val MINOR = 3
        const val PATCH = 1
        val BUILD_PHASE = Build.PHASE.STABLE

        const val SDK_COMPILE = 29
        const val SDK_MIN = 17
        const val SDK_TARGET = 29

        const val APPCOMPAT = "1.1.0"
        const val KTX = "1.1.0"
        const val KOTLIN = "1.3.72"
    }

    object Dependencies {
        val implementationItems = arrayOf(
            // Kotlin
            kotlin("stdlib-jdk8", Versions.KOTLIN),
            "androidx.appcompat:appcompat:${Versions.APPCOMPAT}",
            "androidx.core:core-ktx:${Versions.KTX}"
        )

        val kaptItems = arrayListOf<String>()
    }

    fun generateVersionCode(): Int {
        return Versions.API * 10000000 + Versions.BUILD_PHASE.ordinal * 1000000 + Versions.MAJOR * 10000 + Versions.MINOR * 100 + Versions.PATCH
    }

    fun generateVersionName(): String {
        val type = if (Versions.BUILD_PHASE.alias == "") "" else "-${Versions.BUILD_PHASE.alias}"
        return "${Versions.MAJOR}.${Versions.MINOR}.${Versions.PATCH}$type"
    }

    object Publishing {
        const val name = "meow-bottom-navigation"
        const val repo = "meow"
        const val developerId = "oneHamidreza"
        const val userOrg = "infinitydesign"
        const val groupId = "com.etebarian"
        const val artifactId = "meow-bottom-navigation"
        const val libraryDesc =
            "A simple & curved & material bottom navigation for Android written in Kotlin with ♥"
    }
}

fun <T> Project.getPropertyAny(key: String): T {
    val properties = java.util.Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }
    @Suppress("UNCHECKED_CAST")
    return properties.getProperty(key) as T
}

fun kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

fun kotlin(module: String, version: String? = null): Any =
    "org.jetbrains.kotlin:kotlin-$module${version?.let { ":$version" } ?: ""}"

fun getAllResourcesSrcDirs(project: Project, isLibrary: Boolean = false): ArrayList<String> {
    val moduleName = if (isLibrary) AppConfig.Build.LIBRARY_MODULE else AppConfig.Build.APP_MODULE
    val packageName =
        if (isLibrary) AppConfig.Build.LIBRARY_PACKAGE else AppConfig.Build.APP_PACKAGE
    val list = arrayListOf<String>()
    val path =
        project.rootDir.absolutePath + "\\" + moduleName + "\\src\\main\\kotlin\\" + packageName
    val root = File(path)

    list.add(project.rootDir.absolutePath + "\\" + moduleName + "\\src\\main\\res")
    println(root.absolutePath)
    root.listDirectoriesWithChild().forEach { directory ->
        if (directory.isRes())
            list.add(directory.path)
    }
    println(list.size)
    return list
}

fun File.listDirectories() = listFiles()?.filter { it.isDirectory } ?: arrayListOf()
fun File.listDirectoriesWithChild(): List<File> {
    val list = ArrayList<File>()

    fun File.findAllDirectories(list: ArrayList<File>) {
        listDirectories().forEach {
            it.findAllDirectories(list)
            list.add(it)
        }
    }

    findAllDirectories(list)
    return list
}

fun File.isRes() = name == "res"