plugins {
    `java-library`
}

group       = projectGroup
version     = projectVersion
description = projectDescription

java {
    toolchain {
        languageVersion.set(javaLanguageVersion)
    }

    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://maven.playpro.com")
}

dependencies {
    if (project.name != Libs.Common.projectName) {
        compileOnlyApi(Libs.Common.asProject(rootProject))
    }

    compileOnlyApi(libs.adventure.api)
    compileOnlyApi(libs.adventure.ansi)
    compileOnlyApi(libs.adventure.gson)
    compileOnlyApi(libs.adventure.legacy)
    compileOnlyApi(libs.adventure.plain)
    compileOnlyApi(libs.adventure.minimessage)
    compileOnlyApi(libs.asm.api)
    compileOnlyApi(libs.asm.commons)
    compileOnlyApi(libs.fastutil)
    compileOnlyApi(libs.google.guava)
    compileOnlyApi(libs.google.gson)
    compileOnlyApi(libs.google.jsr305)
    compileOnlyApi(libs.jackson.annotations)
    compileOnlyApi(libs.jda)
    compileOnlyApi(libs.jetbrains.annotations)
    compileOnlyApi(libs.netty.buffer)
    compileOnlyApi(libs.yaml.snakeyaml)
}

sourceSets {
    main {
        java.srcDir(Libs.Common.asProject(rootProject).sourceSets.main.get().java.srcDirs)
    }
}

tasks {
    compileJava {
        options.encoding = utf8

        options.release.set(javaVersionInt)
        options.compilerArgs.add(javaCompilerArgs)
    }

    javadoc {
        options.encoding = utf8
        setDestinationDir(file("$rootDir/build/javadoc"))
    }

    processResources {
        filteringCharset = utf8
    }
}
