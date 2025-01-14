rootProject.name = "crs-service"

pluginManagement {
    repositories {
        gradlePluginPortal()
        // Required for Spring-Boot convention plugin and Bill Of Material (automatic dependency version management)
        maven("https://nexus.geomatys.com/repository/maven-public")
    }
}
