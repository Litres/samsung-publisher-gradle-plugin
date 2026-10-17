package ru.litres.publish.samsung.utils

import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URI

private const val SCHEME_SEPARATOR = "://"
private const val DEFAULT_PROXY_SCHEME = "http://"
private const val UNDEFINED_PORT = -1
private const val DEFAULT_PROXY_PORT = 8080

/**
 * Builds an http proxy out of a "host:port" or "scheme://host:port" string.
 * A null or blank spec means "keep the jvm defaults", so -Dhttps.proxyHost keeps working.
 */
fun createProxy(spec: String?): Proxy? {
    if (spec.isNullOrBlank()) return null

    val normalized = if (spec.contains(SCHEME_SEPARATOR)) spec else "$DEFAULT_PROXY_SCHEME$spec"
    val uri = URI(normalized)
    val host = requireNotNull(uri.host) { "Can not parse proxy \"$spec\", expected format is \"host:port\"" }
    val port = if (uri.port != UNDEFINED_PORT) uri.port else DEFAULT_PROXY_PORT

    return Proxy(Proxy.Type.HTTP, InetSocketAddress(host, port))
}
