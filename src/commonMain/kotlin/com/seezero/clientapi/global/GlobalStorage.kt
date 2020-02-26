package com.seezero.clientapi.global
import com.seezero.clientapi.models.*
//import java.sql.Connection
//import java.util.*
import kotlin.collections.HashMap

object  GlobalStorage {

    val keybank:HashMap<String,C0AsymKey> = HashMap<String,C0AsymKey>() //define empty hashmap

    var keyBankRootFolder = "C:/temp/keybank/"

    lateinit var trustedLinkDigestString : String
    var tlGenerated = false
    lateinit var sendFileName :String
    lateinit var encryptFileName :String

    var conn: Connection? = null

    lateinit var initQRCodeLinkDigest  : C0LinkDigest
    lateinit var loginShareRequestTempDigest  : C0LinkDigest
    lateinit var PrimaryDeviceSentFullDigest : C0LinkDigest
    lateinit var receivedDigestFromPrimary  : C0LinkDigest
    lateinit var SentFromDevicePermDigest  : C0LinkDigest

    var scanDigestMap     :HashMap<String,C0LinkDigest> = HashMap <String,C0LinkDigest>()

    fun setKeyBankLocation(keyBankPath: String) {
        keyBankRootFolder = keyBankPath
    }

}