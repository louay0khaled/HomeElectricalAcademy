plugins {
    id("com.android.library")
}

android {
    namespace = "com.louaykhaled.homeelectricalacademy.core.simulator"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
}

dependencies {
    implementation(project(":core:model"))
    testImplementation("junit:junit:4.13.2")
}
