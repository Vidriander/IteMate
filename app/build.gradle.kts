import com.android.build.api.dsl.Packaging


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

    // Exclude LICENSE and NOTICE files from APK - Fixed our build problem
    packaging {
        resources {
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/NOTICE.md"
        }
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
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Firebase-Dependencies for Android
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-analytics")

    // Glide dependencies
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.swiperefreshlayout)
    androidTestImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.rules)
    androidTestImplementation(libs.espresso.contrib)
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.espresso.core)

    // Test dependencies for Firestore and Mockito
    testImplementation("com.google.firebase:firebase-firestore:24.0.0")
    testImplementation("org.mockito:mockito-core:3.11.2")
    testImplementation("org.mockito:mockito-inline:3.11.2")

    // Test dependencies for Robolectric
    dependencies {
        testImplementation("org.robolectric:robolectric:4.9")
        testImplementation("androidx.test:core:1.4.0")
        testImplementation("androidx.test.ext:junit:1.1.3")
        testImplementation("androidx.test:runner:1.4.0")
        testImplementation("androidx.test:rules:1.4.0")
    }
}
