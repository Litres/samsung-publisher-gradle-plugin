# Samsung publisher gradle plugin

Samsung publisher gradle plugin is Android's unofficial release automation Gradle Plugin.
It helps you automate uploading apk to samsung store. **The plugin does not send for review yet, it just downloads the apk**


## Quick start guide

### Obtainment credentials
Follow to [Samsung store](https://developer.samsung.com/galaxy-store/galaxy-store-developer-api/create-an-access-token.html#Create-a-service-account) and create service account with "Publishing & ITEM" permission.
Save `Private Key` and `Service Account ID`

Next, open [application list](https://seller.samsungapps.com/main/sellerMain.as) and click on application.
You will see link like that `https://seller.samsungapps.com/application/main.as?contentId=000000000000`.
Save `contentId` from link

### Installation
<details open><summary>Kotlin</summary>

```kt
plugins {
    id("com.android.application")
    id("ru.litres.plugin.publish.samsung") version "{last_version}"
}
```

</details>

<details><summary>Groovy</summary>

```groovy
plugins {
    id 'com.android.application'
    id 'ru.litres.plugin.publish.samsung' version '{last_version}'
}
```

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
    artifactDir.set(file("./build/output"))

    //Directory where plugin should find release apk.
    //Plugin searches by extension .apk and gets first file
    serviceAccountId.set("....")

    //Object with app setting
    publishSetting {
        //contentId from url
        contentId = "..."
    }
}
```
</details>

## PublishSetting fields


| Field |  Type   |                                                Description                                                   | Default value |
| :---   |:-------:|:------------------------------------------------------------------------------------------------------------------:|--------------:|
| contentId | String  |                                         Application id which you get from url                                      |             - |
| defaultLanguageCode  | String  |  The language in which you provide application information. See [Language codes](https://developer.samsung.com/galaxy-store/galaxy-store-developer-api/content-publish-api-reference.html#publish-content-api-added-language-codes) for a list of supported languages.  |           "RUS" |
| paid  | Boolean |  Whether or not app download requires a user payment    |         false |
| hasGoogleService  | Boolean |  Whether or not the app provides the user with any Google™ services    |          true |
