import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

include(":feature-splash")



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
    repositoriesMode.set(FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MovieAppFeatureBasedClean"
include(":app")
include(":core-domain")
include(":core-ui")
include(":core-network")
include(":feature-movies")
include(":core-database")
include(":navigation-contracts")
include(":core-preferences")
include(":feature-auth")
include(":core-android")