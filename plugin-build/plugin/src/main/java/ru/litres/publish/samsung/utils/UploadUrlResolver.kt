package ru.litres.publish.samsung.utils

import ru.litres.publish.samsung.exception.UploadApkException
import java.net.URI

private const val STORE_HOST = "samsungapps.com"
private const val STORE_HOST_SUFFIX = ".samsungapps.com"

/**
 * Picks the url the apk is uploaded to.
 *
 * [override] wins, because an explicit `networkSetting.uploadUrl` is a deliberate decision of
 * whoever runs the build, an internal mirror for example. Otherwise the url that
 * "/seller/createUploadSessionId" returned next to the session id is used, and [fallbackPath]
 * covers a store answer that carries no url at all.
 *
 * The upload call carries the access token and the service account id in its headers, so a
 * session url that points outside the store is refused rather than followed.
 */
internal fun resolveUploadUrl(
    override: String?,
    sessionUrl: String?,
    fallbackPath: String,
): String =
    when {
        !override.isNullOrBlank() -> override
        sessionUrl.isNullOrBlank() -> fallbackPath
        else -> sessionUrl.also { it.requireStoreHost() }
    }

private fun String.requireStoreHost() {
    val host = runCatching { URI(this).host }.getOrNull()?.lowercase()
    if (host == null || (host != STORE_HOST && !host.endsWith(STORE_HOST_SUFFIX))) {
        throw UploadApkException(
            "\"/seller/createUploadSessionId\" returned the upload url \"$this\", which is not a " +
                "$STORE_HOST host. The upload carries the access token, so it is not sent there. " +
                "Set networkSetting.uploadUrl if this host is intended.",
        )
    }
}
