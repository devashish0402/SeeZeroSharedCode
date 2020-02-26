package com.seezero.clientapi.models
import com.seezero.clientapi.implementation.loadXMLFromString
import org.w3c.dom.Node
import java.util.*
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

fun main(args: Array<String>) {
    val test = byteArrayOf(0xC1.toByte(), 0x2C.toByte(), 0x38.toByte(), 0xD4.toByte(), 0x89.toByte(), 0xC3.toByte(),
        0xA1.toByte(), 0x2E.toByte(), 0x38.toByte(), 0xD4.toByte(), 0x89.toByte(), 0xC3.toByte(),
        0xA1.toByte(), 0x2E.toByte(), 0x38.toByte(), 0xD4.toByte())

    val theHash = C0Hash(test,"SHA-256")
    getXmlFromC0Hash(theHash)
}

class C0Hash {

    val DEFAULT_LEN_BYTES = 32
    val DEFAULT_LEN_BITS = DEFAULT_LEN_BYTES * 8
    var value: ByteArray
    var algo: String
    var hash = ""
    var lengthBytes = 0
    var lengthBits = 0 // { get { return LengthBytes * 8; } }
    var signatureKeyId = UUID.randomUUID()

    constructor(p_value: ByteArray?, p_algo: String = "SHA256") {
        value = p_value ?: ByteArray(0)
        algo = p_algo
        //hash is added as a signature later. not to be set in constructor
        // hash = getDigest(p_value,p_algo)
    }

    constructor(p_value: ByteArray?, p_algo: String = "SHA256", p_sig: UUID) {
        value = p_value ?: ByteArray(0)
        algo = p_algo
        signatureKeyId = p_sig
    }

}


fun getXmlFromC0Hash(hash: C0Hash): String {
    val sb = StringBuilder()
    sb.append("<C0Hash>")
    sb.append("<Value>" + Base64.getEncoder().encodeToString(hash.value) + "</Value>")
    sb.append("<Algo>" + hash.algo.toString() + "</Algo>")
    sb.append("<SignatureKeyId>" + hash.signatureKeyId.toString().toString() + "</SignatureKeyId>")
    sb.append("</C0Hash>")
    return sb.toString()
}

fun getC0HashFromXmlNode(p_node: Node): C0Hash {

    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val value = xPath.compile(".//Value").evaluate(p_node, XPathConstants.STRING) as String
    val hashValue = Base64.getDecoder().decode(value)
    val hashAlgo = xPath.compile(".//Algo").evaluate(p_node, XPathConstants.STRING) as String
    val signatureKeyId = xPath.compile(".//SignatureKeyId").evaluate(p_node, XPathConstants.STRING) as String
    val hashSignatureId = UUID.fromString(signatureKeyId)
    return C0Hash(hashValue, hashAlgo, hashSignatureId)
}

fun getC0HashFromXml(p_xml: String): C0Hash {
    val doc = loadXMLFromString(p_xml)
    val xPath: XPath = XPathFactory.newInstance().newXPath()

    val hashNode = xPath.evaluate(".//C0Hash", doc, XPathConstants.NODE) as Node
    return getC0HashFromXmlNode(hashNode)
}



