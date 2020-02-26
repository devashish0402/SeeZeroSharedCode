package com.seezero.clientapi.implementation

import com.seezero.clientapi.global.GlobalStorage
import com.seezero.clientapi.global.GlobalStorage.scanDigestMap
import com.seezero.clientapi.models.*
import com.seezero.clientapi.service.hashString
import com.seezero.clientapi.service.login
import java.util.*

val defaultPackFileName = "kbkshare.czz"


fun main() {

    login(hashString("Go Green Go White"))

    val tempUser1 = C0User("Bob", "67890") //person
    val qrScanSource1 = createNewTrustedLinkDigest(tempUser1) //called here for demo
    // val qrScanSource = createNewTrustedLinkDigest() //shoiuld use logged on user bhy default

    // keyBankUser = C0User("Alice", "12345") //person
    val regDigest = scanAndRegisterTrustedLink(qrScanSource1)


    acceptRegisterLinkDigest(regDigest)


    val qrScanSource = createLoginShareRequest()


    val loginShareDigest = scanAndShareKeyBankWithDevice(qrScanSource)

    if (acceptLoginHostKeyBank(loginShareDigest))
        debug("keybank established successfully")

}

fun createNewTrustedLinkDigest(): String {
    return createNewTrustedLinkDigest(keyBank.user)
}

fun createNewTrustedLinkDigest(user: C0User): String {

    val linkDigest = C0LinkDigest(user, generateAsymkeyPair())

    // do not know who scanned the QR code, if at all. store in memory of global storage
    GlobalStorage.initQRCodeLinkDigest = linkDigest
    //generates a temporary key
    debug(" QR  with " + linkDigest.asymKey.id)

    return getEncodedStringFromC0LinkDigest(linkDigest)

}

fun scanAndRegisterTrustedLink(linkDigestText: String): String {
    //when a scan of QR code takes place we get a linkDigest that will have
    //the counterparty info +  counterparty temp public key
    val cptyLinkDigest = getLinkDigestFromEncodedString(linkDigestText)
    val cptyAsymKey = cptyLinkDigest.asymKey
    val cptyUser = cptyLinkDigest.user
    // create a keybank record here
    val mySideKey = generateAsymkeyPair()
    createFullLink(cptyUser, mySideKey, cptyAsymKey)//file always created with cpty name
    val myLinkDigest = C0LinkDigest(getKeyBankUser(), mySideKey)
    val registerDigestBytes =
        asymmetricEncryptBytes(getXmlFromC0LinkDigest(myLinkDigest).toByteArray(Charsets.UTF_8), cptyLinkDigest.asymKey)
    val registerDigest = Base64.getEncoder().encodeToString(registerDigestBytes)
    return registerDigest
}


fun acceptRegisterLinkDigest(registerDigest: String): Boolean {
    //get a result of a scan and register
    val myTempLinkDigest = GlobalStorage.initQRCodeLinkDigest  //retrieve temp key stached away
     val theBytes = Base64.getDecoder().decode(registerDigest)
    val cptyLinkDigestStr = asymmetricDecryptBytes(theBytes, myTempLinkDigest.asymKey).toString(Charsets.UTF_8)
    val cptyLinkDigest = getC0LinkDigestFromXml(cptyLinkDigestStr)
    createFullLink(cptyLinkDigest.user, myTempLinkDigest.asymKey, cptyLinkDigest.asymKey)
    return true

}

fun createLoginShareRequest(): String {

    //this is assuming PC  is showing the QR code
    val randomUserName = UUID.randomUUID().toString()
    val randomUID = UUID.randomUUID().toString()
    val randomUser = C0User(randomUserName, randomUID)
    val linkDigest = C0LinkDigest(randomUser, generateAsymkeyPair())
    scanDigestMap.put(randomUserName, linkDigest)
    // do not know who scanned the QR code, if at all. store in memory of global storage

    //generates a temporary key

    return getEncodedStringFromC0LinkDigest(linkDigest)

}

fun scanAndShareKeyBankWithDevice(linkDigestText: String): String {
    //when a scan of QR code takes place we get a linkDigest that will have
    //the counterparty info + temp public key
    val otherDeviceLinkDigest = getLinkDigestFromEncodedString(linkDigestText)
    val keyBankDigest = C0KeyBankDigest(keyBank, otherDeviceLinkDigest.user.userName, sessionSymKey)
    //  val scrambledBytes = asymmetricEncryptBytes(getXmlFromKeyBank(keyBank),otherDeviceLinkDigest.asymKey)
    val keyBankDigestStr =
        Base64.getEncoder().encodeToString(getXmlFromC0KeyBankDigest(keyBankDigest).toByteArray(Charsets.UTF_8))
    return keyBankDigestStr
}


fun acceptLoginHostKeyBank(keyBankDigest: String): Boolean {
    val rawXml = Base64.getDecoder().decode(keyBankDigest)
    val tempKeyBankDigest = getC0KeyBankDigestFromXml(rawXml.toString(Charsets.UTF_8))
    val checkLinkDigest = scanDigestMap.remove(tempKeyBankDigest.deviceRef)
    if (checkLinkDigest != null) {
        //successful = right device
        keyBank = tempKeyBankDigest.keyBank
        sessionSymKey = tempKeyBankDigest.sessionSymKey
        return true
    }
    return false


}

fun completeServerRegisterLink(linkDigestText: String): Boolean {
    //when server keys are received

    val cptyLinkDigest = getLinkDigestFromEncodedString(linkDigestText)
    val cptyAsymKey = cptyLinkDigest.asymKey
    val trustedLink = getTrustedLinkFromKeyBank("Server")
    trustedLink.theirKey = cptyAsymKey
    trustedLink.isTheirKeyExists = true
    return true
}