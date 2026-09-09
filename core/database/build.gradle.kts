plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.louaykhaled.homeelectricalacademy.core.database"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
}

dependencies {
    implementation(project(":core:model"))
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
}
