# Meow Bottom Navigation
A simple & curved & material bottom navigation for Android written in kotlin

![](https://github.com/shetmobile/MeowBottomNavigation/raw/master/resources/meow-bottom-navigation-normal.gif)

## Download
build.gradle (project path)
```groovy
buildscript {
    repositories {
        jcenter()
    }
}
```
build.gradle (module path)
```groovy
dependencies {
  implementation 'com.etebarian:meow-bottom-navigation:1.1.0'
}
```
use androidx by adding this lines to gradle.properties. if you want more info, just google **AndroidX**
```properties
android.useAndroidX=true
android.enableJetifier=true
```
if you want to add this library to a JAVA Project, you must add kotlin library to build.gradle
```groovy
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61"
}
```

## Usage
add Meow Bottom Navigation in xml
```xml
    <com.etebarian.meowbottomnavigation.MeowBottomNavigation
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

add menu items in code.
```kotlin
val bottomNavigation = findView(R.id.bottomNavigation)
bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_home))
bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_explore))
bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_message))
```
remember that icons must be vector drawable. 
add vectorDrawables.useSupportLibrary = true to your build.gradle inside defaultConfig{ ... }

## Customization
```xml
    <com.etebarian.meowbottomnavigation.MeowBottomNavigation
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:mbn_circleColor="#ffffff"
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

kotlin
```kotlin
bottomNavigation.setOnShowListener {
    // YOUR CODES
}
       
bottomNavigation.setOnClickMenuListener {
    // YOUR CODES
}
```
java
```java
bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES
                return null;
            }
        });

bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES
                return null;
            }
        });
```

## Counter Badge

Setting One Tab
```kotlin
bottomNavigation.setCount(TAB_ID, STRING)
```

Clearing One Tab
```kotlin
bottomNavigation.clearCount(TAB_ID)
```

Clearing All Tabs
```kotlin
bottomNavigation.clearAllCounts(TAB_ID)
```

## Set Default Tab

use this function
```kotlin
bottomNavigation.show(TAB_ID)
```