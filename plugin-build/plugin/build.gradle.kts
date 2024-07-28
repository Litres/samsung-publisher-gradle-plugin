import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation(libs.bundles.fuel)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.bundles.jjwt.runtime)
    testImplementation(libs.junit)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
            version = System.getenv("RELEASE_VERSION") ?: property("VERSION").toString()
            displayName = property("DISPLAY_NAME").toString()
            description = property("DESCRIPTION").toString()
            tags.set(listOf("samsung", "publish"))
        }
    }
}

gradlePlugin {
    website.set(property("WEBSITE").toString())
    vcsUrl.set(property("VCS_URL").toString())
}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}
