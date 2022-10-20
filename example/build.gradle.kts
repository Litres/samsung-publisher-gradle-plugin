plugins {
    java
    id("ru.litres.plugin.publish.samsung")
}

samsungPublishConfig {
    privateKey.set(
        "-----BEGIN RSA PRIVATE KEY-----\n" +
            "....\n" +
            "......\n" +
            "-----END RSA PRIVATE KEY-----"
    )
    artifactDir.set(File(""))
    serviceAccountId.set("....")
    publishSetting {
        contentId = "..."
    }
}
