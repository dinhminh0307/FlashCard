// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application) // Use this if you have it defined in your version catalog
}

android {
    namespace = "com.example.flashcard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.flashcard"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // MPAndroidChart Dependency
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
}
