plugins {
    id("com.android.library")
}

android {
    namespace = "com.louaykhaled.homeelectricalacademy.core.standards"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    kotlinOptions { jvmTarget = "17" }
}
