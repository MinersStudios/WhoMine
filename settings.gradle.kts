plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.5.0")
}

rootProject.name = "WhoMine"
include("shared", "msBlock", "msCore", "msDecor", "msItems", "msUtils")