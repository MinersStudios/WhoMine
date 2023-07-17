plugins {
    java
    kotlin("jvm") version "1.9.0"
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "com.github.minersstudios"
version = "1.0.0"
val apiVersion = "'1.20'"
val website = "https://minersstudios.github.io"
val authors = listOf("MinersStudios", "p0loskun")
val contributors = listOf("PackmanDude")

allprojects {
    apply(plugin = "java")
    apply(plugin = "io.papermc.paperweight.userdev")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    repositories {
        mavenCentral()
        maven("https://maven.playpro.com")
        maven("https://m2.dv8tion.net/releases")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.dmulloy2.net/repository/public/")
    }

    dependencies {
        paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
        compileOnly("net.coreprotect:coreprotect:21.3")
        compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")
        compileOnly("fr.xephi:authme:5.6.0-SNAPSHOT")
        compileOnly("com.discordsrv:discordsrv:1.26.0")
    }

    sourceSets {
        test {
            java {
                srcDirs("src/main/test/java")
            }
        }
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
            enabled = false
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
        }
    }
}

subprojects {
    val lowercaseName = project.name.lowercase()
    val description = when (project.name) {
        "mscore" -> "A Minecraft core plugin for WhoMine plugins"
        "msessentials" -> "A Minecraft plugin with custom features for WhoMine"
        "msblock" -> "A Minecraft plugin with custom blocks for WhoMine"
        "msdecor" -> "A Minecraft plugin with decorations for WhoMine"
        "msitem" -> "A Minecraft plugin with custom items for WhoMine"
        else -> "A Minecraft plugin for WhoMine"
    }

    dependencies {
        implementation(rootProject)
    }

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

    tasks {
        reobfJar {
            outputJar.set(file("$rootDir/builds/jars/${project.name}-v${rootProject.version}.jar"))
        }

        clean {
            delete(".gradle")
        }

        processResources {
            val props = mapOf(
                    "name" to project.name,
                    "version" to rootProject.version,
                    "description" to description,
                    "authors" to authors.joinToString(", "),
                    "contributors" to contributors.joinToString(", "),
                    "website" to website,
                    "apiVersion" to apiVersion,
                    "main" to "${rootProject.group}.$lowercaseName.${project.name}"
            )

            inputs.properties(props)
            filesMatching("paper-plugin.yml") {
                expand(props)
            }
        }
    }
}

tasks {
    jar {
        doLast {
            file("builds/jars").deleteRecursively()
        }
    }

    javadoc {
        enabled = true
        destinationDir = file("$rootDir/builds/javadoc")
    }
}
