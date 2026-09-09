plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android { namespace = "com.louaykhaled.homeelectricalacademy.core.standards"; compileSdk = 37
    defaultConfig { minSdk = 26 }
    kotlinOptions { jvmTarget = "17" }
}
