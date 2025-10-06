import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.20"
    application
}

application {
    mainClass = "me.cdh.Main"
}

group = "me.cdh"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Rainbow-Sugar"))
    implementation("com.formdev:flatlaf:3.6.1")
    implementation("com.miglayout:miglayout-swing:11.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
}

kotlin {
    jvmToolchain(21)
}

tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs/lib"))
}

val buildPath = "classes/kotlin/main"
tasks.withType<KotlinCompile> {
    compilerOptions {
        destinationDirectory.set(layout.buildDirectory.dir(buildPath))
    }
}

tasks.withType<JavaCompile> {
    destinationDirectory.set(layout.buildDirectory.dir(buildPath))
}

tasks.named("compileJava", JavaCompile::class.java) {
    options.compilerArgumentProviders.add(CommandLineArgumentProvider {
        listOf("--patch-module", "me.cdh=${sourceSets["main"].output.asPath}")
    })
}

tasks.jar {
    manifest.attributes["Main-Class"] = "me.cdh.Main"
    manifest.attributes["Main-Module"] = "me.cdh"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree) // OR .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}