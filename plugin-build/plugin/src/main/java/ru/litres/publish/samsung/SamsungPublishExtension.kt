package ru.litres.publish.samsung

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

@Suppress("UnnecessaryAbstractClass")
abstract class SamsungPublishExtension {

    /**
     * Specify a directory where prebuilt artifacts such as APKs may be found.
     *
     */
    @get:Internal("Directory mapped to a useful set of files later")
    abstract val artifactDir: DirectoryProperty

    /**
     * Private key for generate jwt token from samsung store
     * https://developer.samsung.com/galaxy-store/galaxy-store-developer-api/create-an-access-token.html#Create-a-service-account
     */
    @get:Input
    abstract val privateKey: Property<String>

    /**
     * Service account id from samsung store
     * https://developer.samsung.com/galaxy-store/galaxy-store-developer-api/create-an-access-token.html#Create-a-service-account
     */
    @get:Input
    abstract val serviceAccountId: Property<String>

    /**
     * Id your application
     */
    @get:Input
    abstract val contentId: Property<String>
}
