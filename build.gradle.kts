plugins {
    java
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

val apiVersion = "'1.20'"
val website = "https://minersstudios.github.io"
val authors = "[ MinersStudios, p0loskun ]"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.papermc.paperweight.userdev")
    apply(plugin = "xyz.jpenilla.run-paper")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    repositories {
        mavenCentral()
        maven {
            name = "playpro-repo"
            url = uri("https://maven.playpro.com")
        }
        maven {
            name = "m2-dv8tion"
            url = uri("https://m2.dv8tion.net/releases")
        }
        maven {
            name = "codemc-repo"
            url = uri("https://repo.codemc.org/repository/maven-public/")
        }
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.dmulloy2.net/repository/public/")
    }

    dependencies {
        paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
        compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
        compileOnly("net.coreprotect:coreprotect:21.3")
        compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")
        compileOnly("fr.xephi:authme:5.6.0-SNAPSHOT")
        compileOnly("com.discordsrv:discordsrv:1.26.0")
    }

    tasks {
        reobfJar {
            if (project.name != "shared") {
                outputJar.set(file("$rootDir/build/${project.name}-$version.jar"))
            }
        }

        assemble {
            dependsOn(reobfJar)
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }

        javadoc {
            options.encoding = Charsets.UTF_8.name()
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            val props = mapOf(
                    "version" to project.version,
                    "description" to project.description,
                    "authors" to authors,
                    "url" to website,
                    "apiVersion" to apiVersion
            )
            inputs.properties(props)
            filesMatching("plugin.yml") {
                expand(props)
            }
        }
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}
