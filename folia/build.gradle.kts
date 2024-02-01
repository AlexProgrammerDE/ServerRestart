plugins {
    id("sr.project-conventions")
    alias(libs.plugins.userdev)
}

dependencies {
    implementation(projects.common)

    compileOnly(libs.folia)
    paperweight.foliaDevBundle(libs.versions.foliabundle.get())

    implementation(libs.caffeine)
}
