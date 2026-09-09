plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.louaykhaled.homeelectricalacademy.core.datastore"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
}

dependencies {
    implementation("androidx.datastore:datastore-preferences:1.1.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
}
