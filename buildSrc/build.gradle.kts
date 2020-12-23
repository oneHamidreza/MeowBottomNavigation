plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/chrynan/chrynan")
    }
}