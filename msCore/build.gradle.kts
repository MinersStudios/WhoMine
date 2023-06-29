group = "com.github.minersstudios"
version = "1.2.0"
description = "A Minecraft core plugin for WhoMine plugins"

dependencies {
    implementation(project(":shared"))
}

sourceSets {
    main {
        java {
            srcDir("../shared/src/main/java")
            include("**/mscore/**")
        }
        resources {
            srcDir("src/main/resources")
        }
    }
}
