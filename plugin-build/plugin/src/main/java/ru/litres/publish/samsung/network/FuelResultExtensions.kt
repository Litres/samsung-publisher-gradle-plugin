package ru.litres.publish.samsung.network

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.ResponseResultOf
import ru.litres.publish.samsung.exception.UploadApkException
import java.io.IOException

/** Fuel reports this status code when no http response was received at all. */
private const val NO_RESPONSE_STATUS_CODE = -1
private const val ERROR_BODY_LIMIT = 1000
private const val SERVER_ERROR_MIN_STATUS_CODE = 500
private const val SERVER_ERROR_MAX_STATUS_CODE = 599
private const val REQUEST_TIMEOUT_STATUS_CODE = 408
private const val TOO_MANY_REQUESTS_STATUS_CODE = 429

/**
 * Unwraps a fuel result, turning a failure into an exception that names the real cause.
 * Without it a transport level failure is printed to stdout and the build stops on a
 * follow up "field not found" message that hides what actually went wrong.
 */
internal fun <T : Any> ResponseResultOf<T>.bodyOrThrow(endpoint: String): T =
    third.fold(
        success = { it },
        failure = { error -> throw error.asPublishException(endpoint) },
    )

internal fun FuelError.asPublishException(endpoint: String): UploadApkException {
    val reason = exception.message ?: exception::class.java.name
    val message =
        if (response.statusCode == NO_RESPONSE_STATUS_CODE) {
            "Request to \"$endpoint\" (${response.url}) failed before any response was received: $reason. " +
                "The host could not be reached from this machine, " +
                "check network egress, proxy and firewall rules."
        } else {
            val body = errorData.decodeToString().take(ERROR_BODY_LIMIT)
            "Request to \"$endpoint\" (${response.url}) failed with HTTP ${response.statusCode}: $reason. " +
                "Response body: $body"
        }
    return UploadApkException(message, this, retryable = isRetryable)
}

/**
 * A transport failure or a server side error can succeed on the next attempt, and with the
 * akamai backed upload host a retry is often served by a different edge node. A malformed
 * request or a rejected binary can not, so those fail the build right away.
 *
 * Note that [FuelError.causedByInterruption] is deliberately not used here: it also covers
 * [java.net.SocketTimeoutException], which is exactly the case worth retrying.
 */
internal val FuelError.isRetryable: Boolean
    get() =
        when {
            exception is InterruptedException -> false
            exception is IOException -> true
            response.statusCode in SERVER_ERROR_MIN_STATUS_CODE..SERVER_ERROR_MAX_STATUS_CODE -> true
            else ->
                response.statusCode == REQUEST_TIMEOUT_STATUS_CODE ||
                    response.statusCode == TOO_MANY_REQUESTS_STATUS_CODE
        }
