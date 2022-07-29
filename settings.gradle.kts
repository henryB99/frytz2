pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name = "fritz2"

include(
"collab",
"shared-annotation-processor",
"core",
"lenses-annotation-processor",
"test-server",
"headless",
"headless-demo",
"examples-demo"
)
