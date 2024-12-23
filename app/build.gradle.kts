plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smariba_upv.airflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smariba_upv.airflow"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
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
    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.core)
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("com.google.android.gms:play-services-base:16.0.1")
    //cardview
    implementation("androidx.cardview:cardview:1.0.0")
    // Biometric
    implementation("androidx.biometric:biometric:1.2.0")
    implementation("androidx.biometric:biometric-ktx:1.4.0-alpha02")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // MySQL
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation(libs.play.services.vision.common)
    implementation(libs.play.services.vision)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.crashlytics.buildtools)

    // Testing
    testImplementation(libs.junit.junit)
    testImplementation(libs.rules)
    testImplementation(libs.junit.jupiter)
    testImplementation("org.mockito:mockito-core:4.2.0")
    androidTestImplementation(libs.junit.junit)

    // Maps
    implementation("org.osmdroid:osmdroid-android:6.1.14")
    implementation ("org.osmdroid:osmdroid-wms:6.1.14")
    implementation ("org.osmdroid:osmdroid-mapsforge:6.1.14")
    implementation ("com.google.maps.android:android-maps-utils:2.3.0")
    //implementation ("implementation 'com.google.android.gms:play-services-maps:18.0.0")

    //Calendar
    implementation ("com.prolificinteractive:material-calendarview:1.4.3")
}