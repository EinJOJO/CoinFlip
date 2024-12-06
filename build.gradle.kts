plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.3"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "de.einjojo"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        name = "codemc"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "placeholderapi"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "essentailsx"
        url = uri("https://repo.essentialsx.net/releases/")
    }
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(libs.paper.api)
    implementation(libs.universal.scheduler)
    implementation(libs.inventoryframework)
    compileOnly(libs.vault)
    compileOnly(libs.placeholderapi)
    // provided by spigot library loader
    compileOnly(libs.confgurate)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-parameters")
    }
    assemble {
        dependsOn("shadowJar")
    }

    shadowJar {
        //val shadePath = "de.einjojo.coinflip.shaded"
        //relocate("com.github.stefvanschie.inventoryframework", "$shadePath.inventoryframework")
        //relocate("com.github.Anon8281.universalScheduler", "shadePath.universalScheduler")
    }

    runServer {
        minecraftVersion("1.21.3")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        val configurateVersion = libs.confgurate.get().toString()

        from(sourceSets.main.get().resources) {
            val props = mapOf(
                "version" to version.toString(),
                "configurate" to configurateVersion
            )
            filesMatching("plugin.yml") {
                expand(props)
            }
        }
    }

    withType(xyz.jpenilla.runtask.task.AbstractRun::class) {

        jvmArgs("-XX:+AllowEnhancedClassRedefinition")
    }

    test {
        useJUnitPlatform()
    }
}