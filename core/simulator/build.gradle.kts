plugins {
    id("com.android.library")
}

android {
    namespace = "com.louaykhaled.homeelectricalacademy.core.simulator"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":core:model"))
}
