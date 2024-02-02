import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("sr.project-conventions")
    alias(libs.plugins.shadow) apply(false)
}

val platforms = setOf(
    rootProject.projects.serverrestartVelocity,
    rootProject.projects.serverrestartPaper
).map { it.dependencyProject }

tasks {
    jar {
        archiveClassifier.set("")
        archiveFileName.set("ServerRestart-unshaded.jar")
        destinationDirectory.set(rootProject.projectDir.resolve("build/libs"))
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        platforms.forEach { platform ->
            val shadowJarTask = platform.tasks.named<ShadowJar>("shadowJar").get()
            dependsOn(shadowJarTask)
            dependsOn(platform.tasks.withType<Jar>())
            from(zipTree(shadowJarTask.archiveFile))
        }
    }
}