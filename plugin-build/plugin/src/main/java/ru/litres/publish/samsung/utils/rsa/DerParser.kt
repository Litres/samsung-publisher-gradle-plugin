package ru.litres.publish.samsung.utils.rsa

/****************************************************************************
* Copyright (c) 1998-2010 AOL Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License")
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Modified 23-4-2015 misterti
*
****************************************************************************/

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigInteger

/**
 * A bare-minimum ASN.1 DER decoder, just having enough functions to
 * decode PKCS#1 private keys. Especially, it doesn't handle explicitly
 * tagged types with an outer tag.
 * This parser can only handle one layer. To parse nested constructs,
 * get a new parser for each layer using `Asn1Object.getParser()`.
 * There are many DER decoders in JRE but using them will tie this
 * program to a specific JCE/JVM.
 *
 * @author zhang
 */
open class DerParser
/**
 * Create a new DER decoder from an input stream.
 *
 * @param in The DER encoded stream
 */(private var `in`: InputStream) {
    /**
     * Create a new DER decoder from a byte array.
     *
     * @param The encoded bytes
     * @throws IOException
     */
    constructor(bytes: ByteArray?) : this(ByteArrayInputStream(bytes)) {}

    /**
     * Read next object. If it's constructed, the value holds
     * encoded content, and it should be parsed by a new
     * parser from `Asn1Object.getParser`.
     *
     * @return A object
     * @throws IOException
     */
    @Throws(IOException::class)
    fun read(): Asn1Object {
        val tag = `in`.read()
        if (tag == -1) throw IOException("Invalid DER: stream too short, missing tag") // $NON-NLS-1$
        val length = length
        val value = ByteArray(length)
        val n = `in`.read(value)
        if (n < length) throw IOException("Invalid DER: stream too short, missing value") // $NON-NLS-1$
        return Asn1Object(tag, length, value)
    } // $NON-NLS-1$

    // $NON-NLS-1$
    // $NON-NLS-1$
// $NON-NLS-1$

    // A single byte short length

    // We can't handle length longer than 4 bytes
    /**
     * Decode the length of the field. Can only support length
     * encoding up to 4 octets.
     *
     *
     *
     * In BER/DER encoding, length can be encoded in 2 forms,
     *
     *  * Short form. One octet. Bit 8 has value "0" and bits 7-1
     * give the length.
     *  * Long form. Two to 127 octets (only 4 is supported here).
     * Bit 8 of first octet has value "1" and bits 7-1 give the
     * number of additional length octets. Second and following
     * octets give the length, base 256, most significant digit first.
     *
     *
     * @return The length as integer
     * @throws IOException
     */
    @get:Throws(IOException::class)
    private val length: Int
        get() {
            val i = `in`.read()
            if (i == -1) throw IOException("Invalid DER: length missing") // $NON-NLS-1$

            // A single byte short length
            if (i and 0x7F.inv() == 0) return i
            val num = i and 0x7F

            // We can't handle length longer than 4 bytes
            if (i >= 0xFF || num > 4) throw IOException("Invalid DER: length field too big ( $i )") // $NON-NLS-1$
            val bytes = ByteArray(num)
            val n = `in`.read(bytes)
            if (n < num) throw IOException("Invalid DER: length too short") // $NON-NLS-1$
            return BigInteger(1, bytes).toInt()
        }

    companion object {
        // Classes
        const val UNIVERSAL = 0x00
        const val APPLICATION = 0x40
        const val CONTEXT = 0x80
        const val PRIVATE = 0xC0

        // Constructed Flag
        const val CONSTRUCTED = 0x20

        // Tag and data types
        const val ANY = 0x00
        const val BOOLEAN = 0x01
        const val INTEGER = 0x02
        const val BIT_STRING = 0x03
        const val OCTET_STRING = 0x04
        const val NULL = 0x05
        const val OBJECT_IDENTIFIER = 0x06
        const val REAL = 0x09
        const val ENUMERATED = 0x0a
        const val RELATIVE_OID = 0x0d
        const val SEQUENCE = 0x10
        const val SET = 0x11
        const val NUMERIC_STRING = 0x12
        const val PRINTABLE_STRING = 0x13
        const val T61_STRING = 0x14
        const val VIDEOTEX_STRING = 0x15
        const val IA5_STRING = 0x16
        const val GRAPHIC_STRING = 0x19
        const val ISO646_STRING = 0x1A
        const val GENERAL_STRING = 0x1B
        const val UTF8_STRING = 0x0C
        const val UNIVERSAL_STRING = 0x1C
        const val BMP_STRING = 0x1E
        const val UTC_TIME = 0x17
        const val GENERALIZED_TIME = 0x18
    }
}
