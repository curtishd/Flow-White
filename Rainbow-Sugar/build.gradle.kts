import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "me.cdh"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.6.1")
    implementation("com.miglayout:miglayout-swing:11.4.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
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