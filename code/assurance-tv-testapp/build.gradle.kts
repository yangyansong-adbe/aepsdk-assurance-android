import com.adobe.marketing.mobile.gradle.BuildConstants

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.adobe.assurance_tvos_testapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adobe.assurance_tvos_testapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = BuildConstants.Versions.COMPOSE_COMPILER
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-runtime-compose")

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.tv:tv-foundation:1.0.0-alpha11")
    implementation("androidx.tv:tv-material:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    // Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Compose Navigation, Activity, and Lifecycle dependencies
    implementation("androidx.appcompat:appcompat:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.5.0")
    implementation("androidx.navigation:navigation-compose:2.4.0")

    // AEP SDK dependencies
    implementation(project(":assurance"))
    implementation("androidx.compose.material3:material3-android:1.3.1")
//    implementation("com.adobe.marketing.mobile:core:3.0.0")
//    implementation("com.adobe.marketing.mobile:signal:3.0.0")
//    implementation("com.adobe.marketing.mobile:messaging:3.3.0-beta2-SNAPSHOT")
//    implementation("com.adobe.marketing.mobile:lifecycle:3.0.0")
    // Messaging, Edge, and EdgeIdentity will be available after Core, Assurance release.
    // Use Snapshot version for initial Assurance release.
    //implementation("com.adobe.marketing.mobile:messaging:3.0.0")
//    implementation("com.adobe.marketing.mobile:edge:3.0.0")
//    implementation("com.adobe.marketing.mobile:edgeidentity:3.0.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}