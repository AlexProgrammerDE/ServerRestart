plugins {
    id("sr.project-conventions")
    alias(libs.plugins.shadow)
}

val platforms = setOf(
    rootProject.projects.serverrestartVelocity,
    rootProject.projects.serverrestartPaper
).map { it.dependencyProject }


// HELP ME NO UNDERSTAND
tasks {
    jar {
        archiveFileName = "${rootProject.name}-${project.version}-unshaded.jar"
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${project.version}.jar"
        exclude(
            "LICENSE",
            "META-INF/maven/**",
            "META-INF/**/module-info.class",
            "META-INF/MANIFEST.MF",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
            "META-INF/NOTICE.txt"
        )
        relocate("com.github.benmanes.caffeine", "me.xginko.serverrestart.libs.caffeine")
        relocate("org.bstats", "me.xginko.serverrestart.libs.bstats")
    }
}