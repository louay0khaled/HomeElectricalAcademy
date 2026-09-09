buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.56.1")
    }
}

plugins {
    id("com.android.application") version "9.3.1" apply false
    id("com.android.library") version "9.3.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20" apply false
    id("com.google.devtools.ksp") version "2.4.20-1.0.29" apply false
}
