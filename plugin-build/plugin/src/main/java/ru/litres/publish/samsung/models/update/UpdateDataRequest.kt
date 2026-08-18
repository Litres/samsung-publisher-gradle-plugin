package ru.litres.publish.samsung.models.update

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDataRequest(
    val contentId: String,
    val defaultLanguageCode: String,
    val paid: String,
    val publicationType: String,
)
