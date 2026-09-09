pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HomeElectricalAcademy"
include(":app")
include(":core:model")
include(":core:simulator")
include(":core:standards")
include(":core:common")
include(":core:database")
include(":core:datastore")
