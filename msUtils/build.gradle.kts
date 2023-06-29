group = "com.github.minersstudios"
version = "1.4.0"
description = "A Minecraft plugin with custom features for WhoMine"

dependencies {
    implementation(project(":shared"))
}

sourceSets {
    main {
        java {
            srcDir("../shared/src/main/java")
            include("**/msutils/**")
        }
        resources {
            srcDir("src/main/resources")
        }
    }
}
