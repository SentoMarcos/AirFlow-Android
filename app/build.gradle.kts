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
    implementation(libs.androidx.core)
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    testImplementation(libs.junit.junit)
    testImplementation(libs.rules)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.junit.junit)

    //biometria
    implementation("androidx.biometric:biometric:1.2.0")
    implementation("androidx.biometric:biometric-ktx:1.4.0-alpha02")

    //mysql
    implementation ("mysql:mysql-connector-java:8.0.32")

    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //implementacion para los test mockito
    testImplementation("org.mockito:mockito-core:4.2.0")

}
