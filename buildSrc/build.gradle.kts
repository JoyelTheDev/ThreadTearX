plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:8.1.1")
    implementation("org.apache.ant:ant:1.10.14")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}
