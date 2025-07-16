import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("edu.sc.seis.launch4j").version("3.0.6")
    id("jacoco")
}

configure<JavaPluginExtension> {
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

dependencies{
    implementation("com.itextpdf:itextpdf:5.5.13.4")
    implementation("org.bouncycastle:bcprov-jdk18on:1.81")
    implementation("com.jgoodies:jgoodies-binding:2.13.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("com.formdev:flatlaf:3.6")
    compileOnly("org.jspecify:jspecify:1.0.0")

    runtimeOnly("org.slf4j:slf4j-jdk14:2.0.17")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
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
