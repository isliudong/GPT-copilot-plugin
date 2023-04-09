import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.0"
    id("org.jetbrains.changelog") version "1.3.1"
}

group = "com.ld"
version = "0.6.0"

repositories {
    //阿里云仓库
    maven { setUrl("https://maven.aliyun.com/repository/public") }
    mavenCentral()
}


//依赖
dependencies {
    implementation("org.projectlombok:lombok:1.18.26")
    implementation("com.vladsch.flexmark:flexmark-all:0.64.0")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("com.vladsch.flexmark:flexmark:0.64.0")
    implementation("com.vladsch.flexmark:flexmark-util:0.64.0")
    implementation("cn.hutool:hutool-all:5.8.15")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.10.0")
}


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.1")
    type.set("IU") // Target IDE Platform
    plugins.set(listOf("tasks", "markdown"))
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
fun dateValue(pattern: String) = LocalDate.now(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern(pattern))



tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("233.*")
        pluginDescription.set(projectDir.resolve("description.md").readText())
        changeNotes.set(projectDir.resolve("changelog.md").readText())
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
    runIde {
        autoReloadPlugins.set(true)
        jvmArgs = listOf("-Xmx1536m", "-XX:+AllowEnhancedClassRedefinition")
    }


}
