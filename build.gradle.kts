import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("extra-java-module-info")
    id("edu.sc.seis.launch4j").version("3.0.6")
    id("jacoco")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

extra["mainClass"] = "com.aha.pdftools.gui.PermissionManager"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

///////////////////////////////////////////////////
// Launch4j

launch4j {
    mainClassName = extra["mainClass"] as String
    dontWrapJar = true
}

///////////////////////////////////////////////////
// Dependencies

repositories {
    mavenCentral()
}

dependencyLocking {
    lockAllConfigurations()
}

dependencies{
    implementation(libs.itextpdf)
    implementation(libs.bouncycastle)
    implementation(libs.jgoodies.binding)
    implementation(libs.slf4j.api)
    implementation(libs.flatlaf)
    compileOnly(libs.jspecify)

    runtimeOnly(libs.slf4j.jdk14)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.jupiter.platform.launcher)
}
extraJavaModuleInfo {
    module("itextpdf-${libs.versions.itextpdf.get()}.jar", "com.itextpdf") {
        requires("java.desktop")
        requires("java.logging")
        requires("java.xml.crypto")
        requires("java.xml")
        requires("org.bouncycastle.provider")
        exports("com.itextpdf.awt")
        exports("com.itextpdf.awt.geom")
        exports("com.itextpdf.awt.geom.gl")
        exports("com.itextpdf.awt.geom.misc")
        exports("com.itextpdf.testutils")
        exports("com.itextpdf.text")
        exports("com.itextpdf.text.api")
        exports("com.itextpdf.text.error_messages")
        exports("com.itextpdf.text.exceptions")
        exports("com.itextpdf.text.factories")
        exports("com.itextpdf.text.html")
        exports("com.itextpdf.text.html.simpleparser")
        exports("com.itextpdf.text.io")
        exports("com.itextpdf.text.log")
        exports("com.itextpdf.text.pdf")
        exports("com.itextpdf.text.pdf.codec")
        exports("com.itextpdf.text.pdf.codec.wmf")
        exports("com.itextpdf.text.pdf.collection")
        exports("com.itextpdf.text.pdf.crypto")
        exports("com.itextpdf.text.pdf.draw")
        exports("com.itextpdf.text.pdf.events")
        exports("com.itextpdf.text.pdf.fonts")
        exports("com.itextpdf.text.pdf.fonts.cmaps")
        exports("com.itextpdf.text.pdf.fonts.otf")
        exports("com.itextpdf.text.pdf.hyphenation")
        exports("com.itextpdf.text.pdf.interfaces")
        exports("com.itextpdf.text.pdf.internal")
        exports("com.itextpdf.text.pdf.languages")
        exports("com.itextpdf.text.pdf.parser")
        exports("com.itextpdf.text.pdf.parser.clipper")
        exports("com.itextpdf.text.pdf.qrcode")
        exports("com.itextpdf.text.pdf.security")
        exports("com.itextpdf.text.xml")
        exports("com.itextpdf.text.xml.simpleparser")
        exports("com.itextpdf.text.xml.simpleparser.handler")
        exports("com.itextpdf.text.xml.xmp")
        exports("com.itextpdf.xmp")
        exports("com.itextpdf.xmp.impl")
        exports("com.itextpdf.xmp.impl.xpath")
        exports("com.itextpdf.xmp.options")
        exports("com.itextpdf.xmp.properties")
    }
    module("jgoodies-binding-${libs.versions.jgoodies.binding.get()}.jar", "com.jgoodies.binding") {
        requires("java.desktop")
        requiresTransitive("com.jgoodies.common")
        exports("com.jgoodies.binding")
        exports("com.jgoodies.binding.adapter")
        exports("com.jgoodies.binding.beans")
        exports("com.jgoodies.binding.binder")
        exports("com.jgoodies.binding.extras")
        exports("com.jgoodies.binding.internal")
        exports("com.jgoodies.binding.list")
        exports("com.jgoodies.binding.util")
        exports("com.jgoodies.binding.value")
    }
    module("jgoodies-common-${libs.versions.jgoodies.common.get()}.jar", "com.jgoodies.common") {
        requires("java.desktop")
        exports("com.jgoodies.common.base")
        exports("com.jgoodies.common.bean")
        exports("com.jgoodies.common.collect")
        exports("com.jgoodies.common.display")
        exports("com.jgoodies.common.format")
        exports("com.jgoodies.common.internal")
        exports("com.jgoodies.common.swing")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

///////////////////////////////////////////////////
// jacoco

jacoco {
    toolVersion = "0.8.13"
}
tasks.named("jacocoTestReport") {
    dependsOn(tasks.test)
}

///////////////////////////////////////////////////
// About

tasks.register<Exec>("gitVersion") {
    executable = "git"
    args("describe", "--abbrev=6", "--always")
    standardOutput = ByteArrayOutputStream()
    inputs.dir(".git")
    doLast {
        extra["version"] = standardOutput.toString().trim()
    }
}

tasks.register("setVersion") {
    val gitVersion = tasks.named("gitVersion")
    dependsOn(gitVersion)
    val destFile = file("doc/version.rst")
    outputs.file(destFile)
    doLast {
        val version = gitVersion.get().extra["version"] as String
        destFile.writeText(".. |version-nr| replace:: $version")
    }
}

tasks.register<Exec>("aboutPage") {
    val srcFile = "doc/about.rst"
    val destFile = "src/main/resources/com/aha/pdftools/gui/about.html"
    inputs.dir("doc")
    inputs.files(tasks.named("setVersion").get().outputs)
    outputs.file(destFile)
    commandLine("rst2html", "--no-xml-declaration", "--stylesheet=doc/html4css1.css,doc/about.css", srcFile, destFile)
}
tasks.named("processResources") {
    dependsOn("aboutPage")
}

///////////////////////////////////////////////////
// Packaging

fun manifestClassPath(cp: FileCollection): String {
    return cp.filter { it.name.endsWith(".jar") }.map { "lib/${it.name}" }.joinToString(" ")
}

tasks.jar {
    val gitVersion = tasks.named("gitVersion")
    dependsOn(gitVersion)
    manifest {
        attributes("Version" to provider { gitVersion.get().extra["version"] })
        attributes("Main-Class" to project.extra["mainClass"])
        attributes("Class-Path" to provider { manifestClassPath(sourceSets.main.get().runtimeClasspath) })
    }
}

tasks.register<Zip>("distZip") {
    group = "Build"
    description = "Bundles the project as a JVM application with libs and the launch4j .exe"
    val gitVersion = tasks.named("gitVersion")
    dependsOn(gitVersion)
    dependsOn("createExe", "jar")
    archiveFileName = provider { "${project.name}-v${gitVersion.get().extra["version"]}.zip" }
    destinationDirectory = project.layout.buildDirectory
    val jarTask = tasks.getByName("jar")
    val jarName = jarTask.outputs.files.singleFile.name
    from(project.layout.buildDirectory.dir("launch4j"))
        .exclude("lib/$jarName")
        .into(project.name)
    from(jarTask.outputs)
        .into(project.name)
}

///////////////////////////////////////////////////////
// Run Application

tasks.register<JavaExec>("run") {
    group = "Application"
    description = "Runs this project as a JVM application"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = project.extra["mainClass"] as String
}

///////////////////////////////////////////////////////
// Wrapper

tasks.wrapper {
    gradleVersion = "8.14.3"
}
