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
     * Whether app is submitted for review after uploading
     */
    var submitReview: Boolean = false

    /**
     * When the app is published, once it has passed the review.
     * Note that contentUpdate overwrites whatever is set in Seller Portal.
     */
    var publicationType: PublicationType = PublicationType.MANUAL

    /**
     * App has Google service or not
     */
    var hasGoogleService: Boolean = true
}
