package com.seezero.clientapi.models
import java.security.SecureRandom
import java.util.*

fun main(args: Array<String>) {
    val test = byteArrayOf(0xC1.toByte(), 0x2C.toByte(), 0x38.toByte(), 0xD4.toByte(), 0x89.toByte(), 0xC3.toByte(),
        0xA1.toByte(), 0x2E.toByte(), 0x38.toByte(), 0xD4.toByte(), 0x89.toByte(), 0xC3.toByte(),
        0xA1.toByte(), 0x2E.toByte(), 0x38.toByte(), 0xD4.toByte())

    val theSalt = C0Salt(test)
    println(theSalt)
    val theSalt2 = C0Salt()
    println(theSalt2)
}

private fun fetchConstantByteArray(): ByteArray {
    val DEFAULT_SALT_BYTES = byteArrayOfInts(0x3d, 0x32, 0x67, 0xc5, 0x57, 0xb0, 0x93, 0x30, 0x7b, 0x82, 0x66, 0xa4, 0xbc, 0x1b, 0x34, 0x0b)

    return DEFAULT_SALT_BYTES
}
private fun fetchRandomByteArray(): ByteArray {
    val random = SecureRandom()
    var randomByteArray = ByteArray(16)
    random.nextBytes(randomByteArray)
    return randomByteArray
}
fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

class C0Salt{
    companion object {
        public final fun getDefaultSalt():C0Salt
        {
            return C0Salt(byteArrayOfInts(0x3d, 0x32, 0x67, 0xc5, 0x57, 0xb0, 0x93, 0x30, 0x7b, 0x82, 0x66, 0xa4, 0xbc, 0x1b, 0x34, 0x0b))
        }
    }
         val DEFAULT_SALT_BYTES = byteArrayOfInts(0x3d, 0x32, 0x67, 0xc5, 0x57, 0xb0, 0x93, 0x30, 0x7b, 0x82, 0x66, 0xa4, 0xbc, 0x1b, 0x34, 0x0b)

    val SALT_LEN_BYTES = 16;
    var  value  = byteArrayOfInts(0x3d, 0x32, 0x67, 0xc5, 0x57, 0xb0, 0x93, 0x30, 0x7b, 0x82, 0x66, 0xa4, 0xbc, 0x1b, 0x34, 0x0b)
    var length = 0 //{ get { return Value.Length; } }
   // val  DEFAULT_SALT =  Salt(DEFAULT_SALT_BYTES);


    constructor(p_value:ByteArray)  {
        value = p_value
    }
    constructor( ) : this(fetchRandomByteArray())


    fun toBase64String() :String
    {
        return Base64.getEncoder().encodeToString(value)
    }
    override fun toString() :String
    {
        return toBase64String();
    }
}
