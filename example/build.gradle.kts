plugins {
    java
    id("ru.litres.plugin.publish.samsung")
}

samsungPublishConfig {
    privateKey.set(
        "-----BEGIN RSA PRIVATE KEY-----\n" +
            ".............................................\n" +
            "-----END RSA PRIVATE KEY-----\n"
    )
    artifactDir.set(File(""))
    serviceAccountId.set("......")
    contentId.set("......")
}
