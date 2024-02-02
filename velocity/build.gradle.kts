plugins {
    id("sr.project-conventions")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(projects.serverrestartCommon)
    implementation(libs.configmaster)

    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    implementation(libs.caffeine)
    implementation(libs.minimessage)
}

tasks.build.configure {
    dependsOn("shadowJar")
}

tasks.shadowJar {
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