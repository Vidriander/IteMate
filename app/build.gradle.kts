
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "iteMate.project"
    compileSdk = 34

    // Doppelte Dateien ignorieren
    packaging {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }

    defaultConfig {
        applicationId = "iteMate.project"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.material.v100)
    implementation(libs.material.v120alpha05)

    // Import Firebase BoM
    implementation(platform(libs.firebase.bom))
    // Firebase-Dependencies for Android
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)

    // Glide dependencies
    implementation(libs.glide)
    implementation(libs.swiperefreshlayout)
    androidTestImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.ext.junit)
    annotationProcessor(libs.compiler)

    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.espresso.core)

    // Test dependencies for Firestore and Mockito
    testImplementation(libs.google.firebase.firestore)

//    testImplementation(libs.mockito.mockito.core)
//    testImplementation(libs.mockito.inline)
//    implementation(libs.mockito.mockito.core)
//    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation(libs.mockito.android)
    implementation(libs.mockito.android)

    // Test dependencies for Robolectric
    dependencies {
        testImplementation(libs.robolectric)
        testImplementation(libs.core)
        testImplementation(libs.junit.v113)
        testImplementation(libs.runner)
        //testImplementation(libs.rules)
    }
}
