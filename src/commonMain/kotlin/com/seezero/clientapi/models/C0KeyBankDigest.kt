package com.seezero.clientapi.models

import com.seezero.clientapi.implementation.loadXMLFromString
import com.seezero.clientapi.implementation.sessionSymKey
import org.w3c.dom.Node
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class C0KeyBankDigest {
    var keyBank: C0KeyBank
    var deviceRef: String
    var sessionSymKey: C0SymKey

    constructor(p_keybank: C0KeyBank, p_ref: String, p_symKey: C0SymKey) {
        keyBank = p_keybank
        deviceRef = p_ref
        sessionSymKey = p_symKey
    }

}

fun getXmlFromC0KeyBankDigest(kbd: C0KeyBankDigest): String {

    //To be done .... infomplete

    val sb = StringBuilder()
    sb.append("<C0KeyBankDigest>")
    sb.append("<DeviceId>" + kbd.deviceRef + "</DeviceId>")
    sb.append("<KeyBank>")
    sb.append(getXmlFromC0KeyBank(kbd.keyBank))
    sb.append("</KeyBank>")
    sb.append("<SessionKey>")
    sb.append(getXmlFromC0SymKey(sessionSymKey))
    sb.append("</SessionKey>")
    sb.append("</C0KeyBankDigest>")
    return sb.toString()
}


fun getC0KeyBankDigestFromXmlNode(tlNode: Node?): C0KeyBankDigest {

    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val deviceId = xPath.evaluate(".//DeviceId", tlNode, XPathConstants.STRING) as String

    val keyBankNode = xPath.compile(".//KeyBank").evaluate(tlNode, XPathConstants.NODE) as Node
    val keyBank = getC0KeyBankFromXmlNode(keyBankNode)
    val sessKeyNode = xPath.compile(".//SessionKey").evaluate(tlNode, XPathConstants.NODE) as Node
    val sessKey = getC0SymKeyFromXmlNode(sessKeyNode)

    return C0KeyBankDigest(keyBank, deviceId, sessKey)
}

fun getC0KeyBankDigestFromXml(p_xml: String): C0KeyBankDigest {
    val doc = loadXMLFromString(p_xml)
    val xPath: XPath = XPathFactory.newInstance().newXPath()

    val kbNode = xPath.evaluate("/C0KeyBankDigest", doc, XPathConstants.NODE) as Node
    return getC0KeyBankDigestFromXmlNode(kbNode)
}