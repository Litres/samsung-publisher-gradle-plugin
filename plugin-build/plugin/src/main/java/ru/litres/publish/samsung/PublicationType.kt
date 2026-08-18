package ru.litres.publish.samsung

/**
 * When the app is published, once it has passed the review.
 * Maps to the "publicationType" parameter of POST /seller/contentUpdate.
 *
 * Note that this is not about sending the app for review — that is [PublishSetting.submitReview].
 */
enum class PublicationType(val code: String) {
    /**
     * Publish automatically after the pre-review phase has completed
     */
    AUTOMATIC("01"),

    /**
     * Publish manually from Seller Portal after all review phases have completed
     */
    MANUAL("03"),
}
