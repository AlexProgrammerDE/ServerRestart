plugins {
    id("sr.project-conventions")
}

dependencies {
    implementation(projects.serverrestartCommon)
    implementation(libs.configmaster)

    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    implementation(libs.caffeine)
    implementation(libs.minimessage)
}