plugins {
    java
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "com.github.minersstudios"
version = "2.0.0"
val apiVersion = "'1.20'"
val website = "https://minersstudios.github.io"
val authors = listOf("MinersStudios", "p0loskun")

allprojects {
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
    }
}

subprojects {

    val lowercaseName = project.name.lowercase()
    val description = when (project.name) {
        "msblock" -> "A Minecraft plugin with custom blocks for WhoMine"
        "msessentials" -> "A Minecraft plugin with custom features for WhoMine"
        "msdecor" -> "A Minecraft plugin with Decorations for WhoMine"
        else -> "A Minecraft plugin for WhoMine"
    }

    dependencies {
        implementation(rootProject)
    }

    if (project.name != "src") {
        sourceSets {
            main {
                java {
                    srcDir("../src/main/java")
                    include("**/$lowercaseName/**")
                }
                resources {
                    srcDir("${project.name}/src/main/resources")
                }
            }
        }
    }

    tasks {
        reobfJar {
            if (project.name != "src") {
                outputJar.set(file("$rootDir/builds/${project.name}-v${rootProject.version}.jar"))
            }
        }

        clean {
            delete(".gradle")
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
            val props = mapOf(
                    "name" to project.name,
                    "version" to rootProject.version,
                    "description" to description,
                    "authors" to authors.joinToString(", "),
                    "website" to website,
                    "apiVersion" to apiVersion,
                    "main" to "${rootProject.group}.$lowercaseName.${project.name}",
            )

            inputs.properties(props)
            filesMatching("plugin.yml") {
                expand(props)
            }
        }
    }
}

tasks {
    build {
        doLast {
            file("$rootDir/builds").deleteRecursively()
        }
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}
