plugins {
    java
    id("ru.litres.plugin.publish.samsung")
}

samsungPublishConfig {
    privateKey.set(
        "-----BEGIN RSA PRIVATE KEY-----\n" +
            "....\n" +
            "......\n" +
            "-----END RSA PRIVATE KEY-----",
    )
    artifactDir.set(File(""))
    serviceAccountId.set("....")
    publishSetting {
        contentId = "..."
    }

    debug {
        dryMode = false
    }

    // Everything here is optional, the defaults below are the ones the plugin uses.
    // The apk goes to seller.samsungapps.com, which is a different host and a different CDN than
    // the rest of the api, so a build agent can reach the api and still fail to reach the upload.
    networkSetting {
        // Only the tcp connect phase. Keep it short: a connection that is not established within
        // this window will not be established at all, and the build fails fast instead of hanging
        connectTimeoutMs = 20_000

        // Answer timeout for the small json calls
        readTimeoutMs = 120_000

        // Answer timeout for the upload. Galaxy Store accepts the whole binary and scans it before
        // answering, so a multi hundred megabyte apk needs far more than the calls above
        uploadReadTimeoutMs = 900_000

        // Transport failures, 408, 429 and 5xx are retried, a rejected binary is not.
        // Every attempt opens a new upload session and sends the apk again
        uploadAttempts = 3
        uploadRetryDelayMs = 5_000

        // Route every call through a proxy, "host:port" or "scheme://host:port".
        // Leave it out to keep the jvm defaults, so -Dhttps.proxyHost and the gradle
        // systemProp.https.proxyHost family keep working
        // proxy = "proxy.example.com:3128"

        // Overrides the upload url that /seller/createUploadSessionId returns.
        // Only needed when the upload has to go somewhere else, an internal mirror for example:
        // a url from the store itself is followed as is, as long as it is a samsungapps.com host
        // uploadUrl = "https://mirror.example.com/galaxyapi/fileUpload"
    }
}
