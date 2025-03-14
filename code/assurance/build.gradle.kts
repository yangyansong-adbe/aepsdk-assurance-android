import com.adobe.marketing.mobile.gradle.BuildConstants

plugins {
    id("aep-library")
}

val mavenCoreVersion: String by project
val navigationComposeVersion = "2.4.0"
val viewModelComposeVersion = "2.5.1"

aepLibrary {
    namespace = "com.adobe.marketing.mobile.assurance"
    compose = true
    enableSpotless = true
    enableCheckStyle = true
    enableDokkaDoc = true

    publishing {
        gitRepoName = "aepsdk-assurance-android"
        addCoreDependency(mavenCoreVersion)

        addMavenDependency("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", BuildConstants.Versions.KOTLIN)
        addMavenDependency("androidx.appcompat", "appcompat", BuildConstants.Versions.ANDROIDX_APPCOMPAT)
        addMavenDependency("androidx.compose.runtime", "runtime", BuildConstants.Versions.COMPOSE)
        addMavenDependency("androidx.compose.material", "material", BuildConstants.Versions.COMPOSE_MATERIAL)
        addMavenDependency("androidx.activity", "activity-compose", BuildConstants.Versions.ANDROIDX_ACTIVITY_COMPOSE)
        addMavenDependency("androidx.navigation", "navigation-compose", navigationComposeVersion)
        addMavenDependency("androidx.lifecycle", "lifecycle-viewmodel-compose", viewModelComposeVersion)
    }
}

android {

    sourceSets {
        getByName("main").java.srcDirs(
            "src/main/java",
            "../../core/code/core/src/main/java",
            "../../core/code/core/src/phone/java"
        )
    }
}

dependencies {
    // Stop using SNAPSHOT after Core release.
//    implementation("com.adobe.marketing.mobile:core:$mavenCoreVersion")
    implementation("androidx.lifecycle:lifecycle-process:2.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.compose.runtime:runtime:1.4.3")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.compose.animation:animation:1.4.3")
    implementation("androidx.activity:activity-compose:1.5.0")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:$navigationComposeVersion")
    // Compose ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$viewModelComposeVersion")

    testImplementation("org.mockito:mockito-inline:4.5.1")
    testImplementation("net.sf.kxml:kxml2:2.3.0@jar")
    testImplementation("org.json:json:20171018")
    testImplementation("org.robolectric:robolectric:4.7")
}