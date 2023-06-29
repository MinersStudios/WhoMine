group = "com.github.minersstudios"
version = "1.7.0"
description = "A Minecraft plugin with custom blocks for WhoMine"

dependencies {
    implementation(project(":shared"))
}

sourceSets {
    main {
        java {
            srcDir("../shared/src/main/java")
            include("**/msblock/**")
        }
        resources {
            srcDir("src/main/resources")
        }
    }
}