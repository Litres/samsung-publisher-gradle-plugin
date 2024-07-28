package ru.litres.publish.samsung

open class PublishSetting {
    /**
     * Id your application
     */
    var contentId: String? = null

    /**
     * Default language your application
     * For more information check documentation
     * https://developer.samsung.com/galaxy-store/galaxy-store-developer-api/content-publish-api-reference.html
     */
    var defaultLanguageCode: String = "RUS"

    /**
     * Whether app download requires a user payment
     */
    var paid: Boolean = false

    /**
     * App has Google service or not
     */
    var hasGoogleService: Boolean = true
}
