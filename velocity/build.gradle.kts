plugins {
    id("sr.project-conventions")
}

dependencies {
    implementation(projects.common)
    implementation(libs.configmaster)

    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    implementation(libs.caffeine)
}
