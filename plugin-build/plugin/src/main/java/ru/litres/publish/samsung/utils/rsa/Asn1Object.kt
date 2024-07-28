package ru.litres.publish.samsung.utils.rsa

import java.io.IOException
import java.math.BigInteger

/**
 *  * An ASN.1 TLV. The object is not parsed. It can
 *  * only handle integers and strings.
 *  *
 *  * @author zhang
 *
 * Construct an ASN.1 TLV. The TLV could be either a
 * constructed or primitive entity.
 *
 *
 *
 * The first byte in DER encoding is made of following fields,
 * <pre>
 * -------------------------------------------------
 * |Bit 8|Bit 7|Bit 6|Bit 5|Bit 4|Bit 3|Bit 2|Bit 1|
 * -------------------------------------------------
 * |  Class    | CF  |     +      Type             |
 * -------------------------------------------------
</pre> *
 *
 *  * Class: Universal, Application, Context or Private
 *  * CF: Constructed flag. If 1, the field is constructed.
 *  * Type: This is actually called tag in ASN.1. It
 * indicates data type (Integer, String) or a construct
 * (sequence, choice, set).
 *
 *
 * @param tag    Tag or Identifier
 * @param value  Encoded octet string for the field.
 */
open class Asn1Object(
    private val tag: Int,
    private val value: ByteArray,
) {
    val type: Int = tag and 0x1F

    private val isConstructed: Boolean
        get() = tag and DerParser.CONSTRUCTED == DerParser.CONSTRUCTED // $NON-NLS-1$

    /**
     * For constructed field, return a parser for its content.
     *
     * @return A parser for the construct.
     * @throws IOException
     */
    @get:Throws(IOException::class)
    val parser: DerParser
        get() {
            if (!isConstructed) throw IOException("Invalid DER: can't parse primitive entity") // $NON-NLS-1$
            return DerParser(value)
        } // $NON-NLS-1$

    /**
     * Get the value as integer
     *
     * @return BigInteger
     * @throws IOException
     */
    @get:Throws(IOException::class)
    val integer: BigInteger
        get() {
            if (type != DerParser.INTEGER) throw IOException("Invalid DER: object is not integer") // $NON-NLS-1$
            return BigInteger(value)
        } // $NON-NLS-1$

    // $NON-NLS-1$
    // $NON-NLS-1$
    // $NON-NLS-1$
    // $NON-NLS-1$

    /**
     * Get value as string. Most strings are treated
     * as Latin-1.
     *
     * @return Java string
     * @throws IOException
     */
    @get:Throws(IOException::class)
    val string: String
        get() {
            val encoding =
                when (type) {
                    DerParser.NUMERIC_STRING,
                    DerParser.PRINTABLE_STRING,
                    DerParser.VIDEOTEX_STRING,
                    DerParser.IA5_STRING,
                    DerParser.GRAPHIC_STRING,
                    DerParser.ISO646_STRING,
                    DerParser.GENERAL_STRING,
                    -> "ISO-8859-1" // $NON-NLS-1$
                    DerParser.BMP_STRING -> "UTF-16BE" // $NON-NLS-1$
                    DerParser.UTF8_STRING -> "UTF-8" // $NON-NLS-1$
                    DerParser.UNIVERSAL_STRING -> {
                        throw IOException("Invalid DER: can't handle UCS-4 string")
                    } // $NON-NLS-1$
                    else -> throw IOException("Invalid DER: object is not a string") // $NON-NLS-1$
                }
            return String(value, charset(encoding))
        }
}
