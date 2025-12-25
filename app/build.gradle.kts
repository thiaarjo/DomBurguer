plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinAndroid) // <--- O jeito certo de chamar o plugin
    id("kotlin-kapt")
}
android {
    namespace = "com.thiagoazv.domburguer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.thiagoazv.domburguer"
        minSdk = 24
        targetSdk = 35
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
    // Configuração necessária para o Kotlin (JVM 11)
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Bibliotecas padrão
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- ADICIONADOS PARA O PROJETO ---

    // 1. Banco de Dados Offline (Room)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version") // <--- MUDAMOS DE annotationProcessor PARA kapt

    // 2. Gráficos (MPAndroidChart)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}