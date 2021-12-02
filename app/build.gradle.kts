import `in`.surajsau.jisho.Dep

plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "in.surajsau.jisho"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        kotlinCompilerExtensionVersion = "1.0.4"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(Dep.AndroidX.Core)
    implementation(Dep.AndroidX.AppCompat)
    implementation(Dep.AndroidX.Material)
    implementation(Dep.AndroidX.Lifeycycle)

    implementation(Dep.Compose.Ui)
    implementation(Dep.Compose.Material)
    implementation(Dep.Compose.Tooling)
    implementation(Dep.Compose.LiveData)
    implementation(Dep.Compose.ViewModel)
    implementation(Dep.Compose.Activity)
    implementation(Dep.Compose.Navigation)

    implementation(Dep.Hilt.Core)
    kapt(Dep.Hilt.Compiler)
    implementation(Dep.Hilt.Compose)
    implementation(Dep.Hilt.ViewModel)
    kapt(Dep.Hilt.AndroidCompiler)

    implementation(Dep.MLKit.DigitalInk)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation(Dep.Compose.Test)
    debugImplementation(Dep.Compose.ToolingTest)
}