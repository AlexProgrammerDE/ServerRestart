plugins {
    `java-library`
    `maven-publish`
    `kotlin-dsl`
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

group = "me.xginko"
version = "1.0.0"
description = "Simple server restarting plugin for modern server setups."
var url: String? = "github.com/xGinko"

repositories {
    gradlePluginPortal()
    mavenCentral()

    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "configmaster-repo"
        url = uri("https://ci.pluginwiki.us/plugin/repository/everything/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    api("com.github.thatsmusic99:ConfigurationMaster-API:v2.0.0-rc.1")
    paperweight.foliaDevBundle("1.20.2-R0.1-SNAPSHOT")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks {
    processResources {
        filesMatching("**/plugin.yml") {
            expand(
                "version" to project.version,
                "description" to project.description,
                "url" to url
            )
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
