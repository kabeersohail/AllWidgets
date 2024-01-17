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

rootProject.name = "AllWidgets"
include(":app")

include(":iconloaderlib")
project(":iconloaderlib").projectDir = File(rootDir, "platform_frameworks_libs_systemui/iconloaderlib")

include(":searchuilib")
project(":searchuilib").projectDir = File(rootDir, "platform_frameworks_libs_systemui/searchuilib")

include(":animationlib")
project(":animationlib").projectDir = File(rootDir, "platform_frameworks_libs_systemui/animationlib")

include(":hidden-api")
include(":systemUIShared")
include(":systemUIPlugin")
include(":systemUIPluginCore")
include(":systemUICommon")
include(":systemUILog")
include(":systemUIAnim")
include(":systemUnFold")
include(":systemUIViewCapture")
include(":compatLib")
include(":compatLibVR")
include(":compatLibVS")
include(":baseline-profile")

 