group = "com.github.minersstudios"
version = "1.4.0"
description = "A Minecraft plugin with Decorations for WhoMine"

dependencies {
    implementation(project(":shared"))
}


sourceSets {
    main {
        java {
            srcDir("../shared/src/main/java")
            include("**/msdecor/**")
        }
        resources {
            srcDir("src/main/resources")
        }
    }
}
