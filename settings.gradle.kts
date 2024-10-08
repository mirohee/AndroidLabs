pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "Androidsensorlabs"
include(":app")
include(":lab08networking")
include(":lab10numberguessing")
include(":lab11bluetoothscan")
include(":lab13bluetoothconnect")
include(":lab11bluetoothconnect")
include(":lab14heartbeatgraph")
