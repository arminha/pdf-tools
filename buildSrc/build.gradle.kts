plugins {
    `java-gradle-plugin`
}

dependencyLocking { lockAllConfigurations() }

dependencies {
    implementation(libs.asm)
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        // here we register our plugin with an ID
        register("extra-java-module-info") {
            id = "extra-java-module-info"
            implementationClass = "com.aha.pdftools.transform.javamodules.ExtraModuleInfoPlugin"
        }
    }
}
