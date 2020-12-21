buildscript {
    val kotlinVersion = meow.AppConfig.Versions.KOTLIN

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath ("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}

allprojects {

    tasks.withType(Javadoc::class) {
        (options as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        options.encoding = "UTF-8"
    }
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    delete(File("buildSrc\\build"))
}