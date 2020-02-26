import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    java
    id("org.jetbrains.kotlin.native.cocoapods")
}

kotlin {

    cocoapods {
        summary = "Shared Code for Android and iOS"
        homepage = "Link to a Kotlin/Native module homepage"
    }

    //select iOS target platform depending on the Xcode environment variables
    /*val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "SharedCode"
            }
        }
    }*/

    jvm("android")

    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
    }

    sourceSets["androidMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
    }
}


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    //implementation(kotlin("stdlib", "1.3.61"))
    //implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //testCompile (group: 'junit', name: 'junit', version: '4.12')
    implementation ("io.github.microutils:kotlin-logging:1.5.9")
    //compile group: 'org.jetbrains.kotlin', name: 'kotlin-reflect', version: '1.3.61'
    implementation(kotlin("reflect"))
    implementation ("org.slf4j:slf4j-api:1.7.25")
    implementation ("log4j:log4j:1.2.17")
}


/*
val packForXcode by tasks.creating(Sync::class) {
    val targetDir = File(buildDir, "xcode-frameworks")

    /// selecting the right configuration for the iOS
    /// framework depending on the environment
    /// variables set by Xcode build
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets
        .getByName<KotlinNativeTarget>("ios")
        .binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    from({ framework.outputDirectory })
    into(targetDir)

    /// generate a helpful ./gradlew wrapper with embedded Java path
    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\n"
                + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
                + "cd '${rootProject.rootDir}'\n"
                + "./gradlew \$@\n")
        gradlew.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(packForXcode)*/
