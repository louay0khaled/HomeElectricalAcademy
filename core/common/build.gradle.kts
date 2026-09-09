plugins {
    id("com.android.library")
}

android {
    namespace = "com.louaykhaled.homeelectricalacademy.core.common"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
}
