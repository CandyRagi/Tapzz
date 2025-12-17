plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.project.tapthehuzz.wear"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.tapthehuzz"
        minSdk = 30
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.wear.compose:compose-material:1.3.0")
    implementation("androidx.wear.compose:compose-foundation:1.3.0")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    
    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
