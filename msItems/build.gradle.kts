group = "com.github.minersstudios"
version = "1.1.0"
description = "A Minecraft plugin with custom items for WhoMine"

dependencies {
    implementation(project(":shared"))
}

sourceSets {
    main {
        java {
            srcDir("../shared/src/main/java")
            include("**/msitems/**")
        }
        resources {
            srcDir("src/main/resources")
        }
    }
}
