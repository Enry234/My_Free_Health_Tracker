plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.serialization")

}

android {
    namespace = "com.example.myfreehealthtracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myfreehealthtracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        versionNameSuffix = "PizzoSbirroEdition"
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
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //noinspection UseTomlInstead
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.play.services.location)
    implementation(libs.material)
    implementation(libs.coil.compose)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.view)
    implementation(libs.firebase.database.ktx)
    implementation(libs.volley)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.core)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui.android)
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:2.6.1")
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.compose.ui:ui:1.6.7")
    implementation("androidx.compose.material:material:1.6.7")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("io.ktor:ktor-client-core:1.6.3")
    implementation("io.ktor:ktor-client-android:1.6.3")
    implementation("io.ktor:ktor-client-logging:1.6.3")
    implementation("io.ktor:ktor-client-serialization:1.6.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("com.github.tehras:charts:0.2.4-alpha")
    implementation("com.github.vsnappy1:ComposeDatePicker:2.2.0")


    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m2)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)
    implementation(libs.vico.views)

//    implementation ("com.patrykandpatrick.vico:core:1.11.1")
//    implementation ("com.patrykandpatrick.vico:compose-m3:1.11.1")


}