plugins {
    java
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.viaversion.com")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly(files("lib/paper0.jar"))
    compileOnly("com.viaversion:viaversion-api:5.11.0")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

tasks {
    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
