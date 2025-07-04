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
    }
}

rootProject.name = "MovieAppFeatureBasedClean"
include(":app")
include(":core-common")
include(":core-ui")
include(":core-network")
include(":feature-movies")
include(":core-database")
include(":core-config")
include(":navigation-contracts")
include(":database-contracts")
