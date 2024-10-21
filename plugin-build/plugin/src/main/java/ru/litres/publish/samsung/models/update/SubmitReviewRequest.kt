package ru.litres.publish.samsung.models.update

import kotlinx.serialization.Serializable

@Serializable
data class SubmitReviewRequest(
    val contentId: String,
)
