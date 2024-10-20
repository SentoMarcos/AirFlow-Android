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

    // Dependencias de prueba
    testImplementation(libs.junit)
    testImplementation(libs.ext.junit)
    testImplementation(libs.rules)
    testImplementation(libs.androidx.espresso.intents)
    testImplementation(libs.androidx.espresso.intents)
    testImplementation(libs.androidx.espresso.core)

    // Pruebas instrumentadas
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // Asegúrate de tener la última versión
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1") // Incluye espresso-intents si usas Intents


    // Pruebas instrumentadas
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // Asegúrate de tener la última versión
    androidTestImplementation ("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1") // Incluye espresso-intents si usas Intents

    // Biometría
    implementation("androidx.biometric:biometric:1.2.0")
    implementation("androidx.biometric:biometric-ktx:1.4.0-alpha02")
}
