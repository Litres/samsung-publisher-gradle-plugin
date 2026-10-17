package ru.litres.publish.samsung.exception

/**
 * @param retryable whether repeating the very same call has a chance to succeed,
 * true for transport failures and server side errors
 */
class UploadApkException(
    error: String,
    cause: Throwable? = null,
    val retryable: Boolean = false,
) : Exception(error, cause)
