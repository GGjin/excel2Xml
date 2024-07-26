import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
//    id("org.jetbrains.kotlin.plugin.compose")
//    alias(libs.plugins.jetbrainsCompose) apply false
//    alias(libs.plugins.compose.compiler) apply false
}

group = "com.example"
version = "1.0-SNAPSHOT"

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    //操作excel
    implementation("org.apache.poi:poi:5.0.0")
    implementation("org.apache.poi:poi-ooxml:5.0.0")
//    //操作xml
//    implementation("org.dom4j:dom4j:2.1.3")
//    implementation("jaxen:jaxen:1.2.0")
    implementation("org.jdom:jdom2:2.0.6")

}
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = "17"
    }
}
compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "excel2Xml"
            packageVersion = "1.0.0"
        }
    }
}
