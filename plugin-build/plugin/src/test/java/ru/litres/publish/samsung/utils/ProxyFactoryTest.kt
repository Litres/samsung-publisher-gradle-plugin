package ru.litres.publish.samsung.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.net.InetSocketAddress

class ProxyFactoryTest {
    @Test
    fun `null and blank specs keep the jvm defaults`() {
        assertNull(createProxy(null))
        assertNull(createProxy(""))
        assertNull(createProxy("   "))
    }

    @Test
    fun `host and port without a scheme are accepted`() {
        val address = createProxy("proxy.example.com:3128")?.address() as InetSocketAddress

        assertEquals("proxy.example.com", address.hostString)
        assertEquals(3128, address.port)
    }

    @Test
    fun `scheme is stripped and the port is kept`() {
        val address = createProxy("http://proxy.example.com:3128")?.address() as InetSocketAddress

        assertEquals("proxy.example.com", address.hostString)
        assertEquals(3128, address.port)
    }

    @Test
    fun `a spec without a port falls back to 8080`() {
        val address = createProxy("proxy.example.com")?.address() as InetSocketAddress

        assertEquals("proxy.example.com", address.hostString)
        assertEquals(8080, address.port)
    }

    @Test
    fun `an unparsable spec fails with a message naming the expected format`() {
        val error = assertThrows(IllegalArgumentException::class.java) { createProxy("::::") }

        assertEquals("Can not parse proxy \"::::\", expected format is \"host:port\"", error.message)
    }
}
