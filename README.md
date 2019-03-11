# Meow Bottom Navigation
A simple & curved & material bottom navigation for Android written in kotlin

![](https://github.com/shetmobile/MeowBottomNavigation/raw/master/resources/meow-bottom-navigation-normal.gif)

## Download
build.gradle (project path)
```groovy
buildscript {
    repositories {
        jcenter() // this line need
    }
    ....
}
```
build.gradle (module path)
```groovy
dependencies {
  implementation 'com.etebarian:meow-bottom-navigation:1.0.1'
}
```
use androidx by adding this lines to gradle.properties
```properties
android.useAndroidX=true
android.enableJetifier=true
```

## Usage
add Meow Bottom Navigation in xml
```xml
    <com.etebarian.meowbottomnavigation.MeowBottomNavigation
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

add menu items in code.

remember icons must be vector drawable
```kotlin
val bottomNavigation = findView(R.id.bottomNavigation)
bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_home))
bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_explore))
bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_message))
....
```

## Customization
```xml
    <com.etebarian.meowbottomnavigation.MeowBottomNavigation
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:mbn_backgroundBottomColor="#ffffff"
        app:mbn_countBackgroundColor="#ff6f00"
        app:mbn_countTextColor="#ffffff"
        app:mbn_countTypeface="fonts/SourceSansPro-Regular.ttf"
        app:mbn_defaultIconColor="#90a4ae"
        app:mbn_rippleColor="#2f424242"
        app:mbn_selectedIconColor="#3c415e"
        app:mbn_shadowColor="#1f212121"/>
```

## Listeners
```kotlin
bottomNavigation.setOnShowListener {
}
       
bottomNavigation.setOnClickMenuListener {
}
```
