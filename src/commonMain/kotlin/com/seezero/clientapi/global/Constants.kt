package com.seezero.clientapi.global

import com.seezero.clientapi.implementation.byteArrayOfInts
import com.seezero.clientapi.implementation.generateSymKeyFromPassword
import com.seezero.clientapi.models.C0Salt
import com.seezero.clientapi.models.C0SymKey
//import java.io.File
//import java.lang.Exception


fun getDefaultPasswordSalt() :ByteArray
{
        return byteArrayOfInts(
            0x3d, 0x32, 0x67, 0xc5, 0x57, 0xb0, 0x93,
            0x30, 0x7b, 0x82, 0x66, 0xa4, 0xbc, 0x1b, 0x34, 0x0b
        )
}

fun getDefaultPasswordHardener() : String
{
    val fixedHardener = "Thequickbrownfoxjumpsoverthelazydog"
    return fixedHardener
}

fun getDefaultPasswordHashMethod() : String
{
     return "PBKDF2WithHmacSHA1"
}


fun getPasswordHashIterations(): Int
{
    return 65536
}


fun getPasswoerdBitSize(): Int
{
    return 256
}


fun getDefaultAsymKeySize(): Int
{
    return 4096
}


class Constants
{


}
