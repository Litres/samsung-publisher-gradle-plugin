package ru.litres.publish.samsung.models.update

import kotlinx.serialization.Serializable

/**
 * Response for POST /seller/v2/content/binary.
 * Success is indicated by resultCode == "0000".
 *
 * Example:
 * { "resultCode": "0000", "resultMessage": "Ok", "data": { "binarySeq": "3" } }
 */
@Serializable
data class AddBinaryResponse(
    val resultCode: String? = null,
    val resultMessage: String? = null,
    val data: AddBinaryData? = null,
)

@Serializable
data class AddBinaryData(
    val binarySeq: String? = null,
)
