plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
}

rootProject.name = "WhoMine"
include("MSBlock", "MSCore", "MSDecor", "MSItem", "MSEssentials")
