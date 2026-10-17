# Samsung publisher gradle plugin

Samsung publisher gradle plugin is Android's unofficial release automation Gradle Plugin.
It helps you automate uploading of apk to samsung store. **The plugin haven't sent for review yet, it just downloads the apk**


## Quick start guide

### Obtainment credentials
Follow to [Samsung store](https://developer.samsung.com/galaxy-store/galaxy-store-developer-api/create-an-access-token.html#Create-a-service-account) and create service account with "Publishing & ITEM" permission.
Save `Private Key` and `Service Account ID`

Next, open the [application list](https://seller.samsungapps.com/main/sellerMain.as) and click on application.
You will see a link like that `https://seller.samsungapps.com/application/main.as?contentId=000000000000`.
Save `contentId` from link

### Installation
<details open><summary>Kotlin</summary>

```kt
//app build.gradle.kts
plugins {
    id("ru.litres.plugin.publish.samsung") version "{last_version}"
}
```

</details>

<details><summary>Groovy</summary>

```groovy
//app build.gradle.kts
plugins {
    id 'ru.litres.plugin.publish.samsung' version '{latest_version}'
}
```

</details>

<details><summary>Legacy</summary>
    <details open><summary>Kotlin</summary>

```kt
   //root build.gradle.kts
  dependencies {
    classpath("ru.litres.plugin:plugin:{latest_version}")
  }

  //app build.gradle.kts
  apply(plugin = "ru.litres.plugin.publish.samsung")
```

</details>

<details><summary>Groovy</summary>

```groovy
   //root build.gradle.kts
  dependencies {
    classpath "ru.litres.plugin:plugin:{latest_version}"
  }

  //app build.gradle.kts
  apply plugin: "ru.litres.plugin.publish.samsung"
```

</details>
</details>

### Common configuration
<details open><summary>Kotlin</summary>

```kt
android { ... }

samsungPublishConfig {
    //private key from service account
    privateKey.set(
        "-----BEGIN RSA PRIVATE KEY-----\n" +
            "....\n" +
            "......\n" +
            "-----END RSA PRIVATE KEY-----"
    )

    //Service Account ID from service account
    serviceAccountId.set("....")

    //Directory where plugin should find release apk.
    //Plugin searches by extension .apk and gets first file
    artifactDir.set(File("build/output"))

    //Object with app setting
    publishSetting {
        //contentId from url
        contentId = "..."
    }
}
```
</details>

<details><summary>Groovy</summary>

```groovy
android { ... }

samsungPublishConfig {
    //private key from service account
    privateKey.set(
        "-----BEGIN RSA PRIVATE KEY-----\n" +
            "....\n" +
            "......\n" +
            "-----END RSA PRIVATE KEY-----"
    )

    //Service Account ID from service account
    serviceAccountId.set("....")

    //Directory where plugin should find release apk.
    //Plugin searches by extension .apk and gets first file
    artifactDir.set(file("./build/output"))

    //Object with app setting
    publishSetting {
        //contentId from url
        contentId = "..."
    }
}
```
</details>

### Usage
And finally if your configuration is correct you can use gradle task `samsungPublish` for upload apk


## PublishSetting fields


| Field |  Type   |                                                                                                                              Description                                                                                                                              | Default value |
| :---   |:-------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|--------------:|
| contentId | String  |                                                                                                                 Application id which you get from url                                                                                                                 |             - |
| defaultLanguageCode  | String  | The language in which you provide application information. See [Language codes](https://developer.samsung.com/galaxy-store/galaxy-store-developer-api/content-publish-api-reference.html#publish-content-api-added-language-codes) for a list of supported languages. |           "RUS" |
| paid  | Boolean |                                                                                                             Whether app download requires a user payment                                                                                                              |         false |
| hasGoogleService  | Boolean |                                                                                                      Whether the app provides the user with any Google™ services                                                                                                      |          true |
| submitReview  | Boolean |                                                                                                      Whether app is submitted for review after uploading                                                                                                      |          false |
| publicationType  | PublicationType | When the app is published, once it has passed the review: `AUTOMATIC` — right after the pre-review phase, `MANUAL` — you publish it yourself from Seller Portal. Note that this value overwrites the one stored in Seller Portal |          MANUAL |


## Network setting

Uploading happens against `seller.samsungapps.com`, which is a different host (and a different CDN)
than the rest of the API. If that host is unreachable from your build agent the upload fails with a
connect timeout — see [#7](https://github.com/Litres/samsung-publisher-gradle-plugin/issues/7).
The `networkSetting` block lets you tune timeouts, retries and egress without patching the plugin.

<details open><summary>Kotlin</summary>

```kt
samsungPublishConfig {
    // ...

    networkSetting {
        //fail fast when the upload host is unreachable
        connectTimeoutMs = 20_000

        //Galaxy Store scans the binary before answering, so give the upload call room
        uploadReadTimeoutMs = 900_000

        //a retry is often served by another CDN edge node
        uploadAttempts = 3
        uploadRetryDelayMs = 5_000

        //route the calls through a proxy, "host:port" or "scheme://host:port"
        proxy = "proxy.example.com:3128"
    }
}
```

</details>

<details><summary>Groovy</summary>

```groovy
samsungPublishConfig {
    // ...

    networkSetting {
        connectTimeoutMs = 20_000
        uploadReadTimeoutMs = 900_000
        uploadAttempts = 3
        uploadRetryDelayMs = 5_000
        proxy = "proxy.example.com:3128"
    }
}
```

</details>

### NetworkSetting fields

| Field | Type | Description | Default value |
| :--- | :---: | :---: | ---: |
| connectTimeoutMs | Int | Timeout of the TCP connect phase. A connection that is not established within this window will not be established at all | 20 000 |
| readTimeoutMs | Int | Response timeout for the small json calls of the publish API | 120 000 |
| uploadReadTimeoutMs | Int | Response timeout for `/galaxyapi/fileUpload`. Galaxy Store accepts the whole binary and scans it before answering | 900 000 |
| uploadAttempts | Int | How many times the apk upload is attempted. Only transport failures, `408`, `429` and `5xx` answers are retried | 3 |
| uploadRetryDelayMs | Long | Pause before the first retry, doubled on every further attempt | 5 000 |
| proxy | String? | Proxy for every call, `host:port` or `scheme://host:port`. `null` keeps the JVM defaults, so `-Dhttps.proxyHost` still works | null |
| uploadUrl | String? | Overrides the upload url returned by `/seller/createUploadSessionId`. `null` means the url from the session response is used, and such a url is only followed when it points at a `samsungapps.com` host, since the upload carries the access token | null |
