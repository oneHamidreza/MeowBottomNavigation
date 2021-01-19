# Meow Bottom Navigation
A simple & curved & material bottom navigation for Android written in **Kotlin** with ♥ .

![](https://github.com/shetmobile/MeowBottomNavigation/raw/master/resources/Preview.gif)

## Download

Update your `build.gradle` (project path) like below :

```groovy
buildscript {
    repositories {
        jcenter()
    }
}
```

Update your `build.gradle` (module path) like below :

```groovy
dependencies {
  implementation 'com.etebarian:meow-bottom-navigation:1.3.1'
}
```

Use androidx by adding this lines to `gradle.properties`. If you want more info, just google **AndroidX**.

```properties
android.useAndroidX=true
android.enableJetifier=true
```

If you want to add this library to a JAVA Project, you must add kotlin library to `build.gradle`.

```groovy
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61"
}
```
## 😍 Donate & Support

We are developing this framework in open source community without financial planning but the maintenance & preparing updates at periodic times is Time-consuming.
If you like this project and you want to give us peace of mind, you can support us by clicking this button :

<p>
<a href="https://cutt.ly/9jP8U6q">
<img width="20%" src="https://raw.githubusercontent.com/oneHamidreza/Meow-Framework-MVVM/master/Resources/img_support.png"/>
</a>
</p>

## Usage

Add Meow Bottom Navigation in you layout xml file.

```xml
<meow.meowbottomnavigation.MeowBottomNavigation
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

Add menu items in code.

```kotlin
val bottomNavigation = findView(R.id.bottomNavigation)
bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_home))
bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_explore))
bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_message))
```

Add vectorDrawables.useSupportLibrary = true to your build.gradle inside `defaultConfig{ ... }` to use vector drawable icons.

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
    app:mbn_shadowColor="#1f212121"
    app:mbn_hasAnimation="true"    
/>
```

- You can change this properties in **Kotlin/Java** Realtime⌚. 

## Listeners

Use `setOnShowListener()` function to access when a cell has been shown.

```kotlin
bottomNavigation.setOnShowListener {
    // YOUR CODES
}
```

Use `setOnClickMenuListener()` function to access when a cell has been clicked.

```kotlin     
bottomNavigation.setOnClickMenuListener {
    // YOUR CODES
}
```

If you are Java Developer, use this examples :

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

Set counter badge on a specific cell by `setCount(Int,String)`.

```kotlin
bottomNavigation.setCount(CELL_ID, YOUR_STRING)
```

Clear counter badge on a specific cell by `clearCount(Int)`.

```kotlin
bottomNavigation.clearCount(CELL_ID)
```

Clear all counter badges on a specific cell by `clearCount(Int)`.

```kotlin
bottomNavigation.clearAllCounts(TAB_ID)
```

## Set Default CELL

Use this function :

```kotlin
bottomNavigation.show(CELL_ID)
```
