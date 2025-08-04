import org.gradle.kotlin.dsl.invoke

plugins {
    id("whomine.shadow")
}

dependencies {
    runtimeClasspath(libs.adventure.api)
    runtimeClasspath(libs.adventure.ansi)
    runtimeClasspath(libs.adventure.gson)
    runtimeClasspath(libs.adventure.legacy)
    runtimeClasspath(libs.adventure.plain)
    runtimeClasspath(libs.adventure.minimessage)
    runtimeClasspath(libs.asm.api)
    runtimeClasspath(libs.asm.commons)
    runtimeClasspath(libs.fastutil)
    runtimeClasspath(libs.google.guava)
    runtimeClasspath(libs.google.gson)
    runtimeClasspath(libs.google.jsr305)
    runtimeClasspath(libs.jackson.annotations)
    runtimeClasspath(libs.jda)
    runtimeClasspath(libs.jetbrains.annotations)
    runtimeClasspath(libs.netty.buffer)
    runtimeClasspath(libs.yaml.snakeyaml)
}

tasks {
    jar {
        destinationDirectory.set(file("$rootDir/build/lib"))
    }

    shadowJar {
        destinationDirectory.set(file("$rootDir/build/lib"))
    }
}
