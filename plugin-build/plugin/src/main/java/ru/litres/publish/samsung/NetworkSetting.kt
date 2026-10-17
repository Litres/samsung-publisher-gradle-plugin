package ru.litres.publish.samsung

import ru.litres.publish.samsung.network.NetworkClient

open class NetworkSetting {
    /**
     * Timeout of the tcp connect phase, ms.
     * A connection that is not established within this window will not be established at all.
     */
    var connectTimeoutMs: Int = NetworkClient.DEFAULT_CONNECT_TIMEOUT_MS

    /**
     * Response timeout for the small json calls of the publish api, ms
     */
    var readTimeoutMs: Int = NetworkClient.DEFAULT_READ_TIMEOUT_MS

    /**
     * Response timeout for the apk upload call, ms.
     * Galaxy Store accepts the whole binary and scans it before answering
     */
    var uploadReadTimeoutMs: Int = NetworkClient.UPLOAD_READ_TIMEOUT_MS

    /**
     * How many times the apk upload is attempted before the build fails.
     * Only transport failures and 5xx answers are retried
     */
    var uploadAttempts: Int = DEFAULT_UPLOAD_ATTEMPTS

    /**
     * Pause before the first retry, doubled on every further attempt, ms
     */
    var uploadRetryDelayMs: Long = DEFAULT_UPLOAD_RETRY_DELAY_MS

    /**
     * Proxy for every call, "host:port" or "scheme://host:port".
     * Null keeps the jvm defaults, so the -Dhttps.proxyHost family still works
     */
    var proxy: String? = null

    /**
     * Overrides the upload url returned by "/seller/createUploadSessionId".
     * Null means the url from the session response is used
     */
    var uploadUrl: String? = null

    companion object {
        const val DEFAULT_UPLOAD_ATTEMPTS = 3
        const val DEFAULT_UPLOAD_RETRY_DELAY_MS = 5_000L
    }
}
