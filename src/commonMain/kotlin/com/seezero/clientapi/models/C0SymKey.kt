package com.seezero.clientapi.models

import com.seezero.clientapi.implementation.loadXMLFromString
import org.w3c.dom.Node
import java.time.LocalDateTime
import java.util.*
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory


class C0SymKey() {
    val DEFAULT_LENGTH_BYTES2 = 32
    val DEFAULT_LENGTH_BITS = DEFAULT_LENGTH_BYTES2 * 8
    val iv_LEN_BYTES = 16
    val iv_LEN_BITS = iv_LEN_BYTES * 8
    val DEFAULT_HASH_ITERATIONS = 10000


    //Following are only variables serialized or accesible outside
    var generatedUTC = LocalDateTime.MIN
    var id: UUID? = UUID.randomUUID()
    var salt: C0Salt? = null
    var iv = ByteArray(16)
    var key = ByteArray(256)
    var hash: C0Hash? = null


    constructor(p_salt: C0Salt?, p_iv: ByteArray, p_key: ByteArray) : this() {
        generatedUTC = LocalDateTime.now()
        id = UUID.randomUUID()
        salt = p_salt
        iv = p_iv
        key = p_key
    }

    constructor(p_id: UUID, p_utc: LocalDateTime, p_salt: C0Salt?, p_iv: ByteArray, p_key: ByteArray) : this() {
        generatedUTC = p_utc
        id = p_id
        salt = p_salt
        iv = p_iv
        key = p_key
    }

    constructor(p_id: UUID, p_utc: LocalDateTime, p_salt: C0Salt?, p_iv: ByteArray, p_key: ByteArray,p_hash:C0Hash) : this() {
        generatedUTC = p_utc
        id = p_id
        salt = p_salt
        iv = p_iv
        key = p_key
        hash = p_hash
    }


}

fun getXmlFromC0SymKey(symKey: C0SymKey): String {
    val sb = StringBuilder()
    sb.append("<C0SymKey>")
    sb.append("<GeneratedUTC>" + symKey.generatedUTC + "</GeneratedUTC>")
    sb.append("<Id>" + symKey.id.toString() + "</Id>")
    sb.append("<Salt>" + Base64.getEncoder().encodeToString(symKey.salt!!.value) + "</Salt>")
    sb.append("<IV>" + Base64.getEncoder().encodeToString(symKey.iv) + "</IV>")
    sb.append("<Key>" + Base64.getEncoder().encodeToString(symKey.key) + "</Key>")
    sb.append("<Hash>")
    sb.append(getXmlFromC0Hash(symKey.hash!!))
    sb.append("</Hash>")
    sb.append("</C0SymKey>")
    return sb.toString()
}


fun getC0SymKeyFromXmlNode(p_node: Node): C0SymKey {

    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val utc_string = xPath.compile(".//GeneratedUTC").evaluate(p_node, XPathConstants.STRING) as String
    val utc = LocalDateTime.parse(utc_string)
    val id_string = xPath.compile(".//Id").evaluate(p_node, XPathConstants.STRING) as String
    val id = UUID.fromString(id_string)
    val salt_base64 = xPath.compile(".//Salt").evaluate(p_node, XPathConstants.STRING) as String
    val salt = C0Salt(Base64.getDecoder().decode(salt_base64))
    val iv_base64 = xPath.compile(".//IV").evaluate(p_node, XPathConstants.STRING) as String
    val iv = Base64.getDecoder().decode(iv_base64)
    val key_base64 = xPath.compile(".//Key").evaluate(p_node, XPathConstants.STRING) as String
    val key = Base64.getDecoder().decode(key_base64)
    val hash = getC0HashFromXmlNode(p_node)
    return C0SymKey(id, utc, salt, iv, key,hash)
}

fun getSymKeyFromXml(p_xml: String): C0SymKey {
    val doc = loadXMLFromString(p_xml)
    val xPath: XPath = XPathFactory.newInstance().newXPath()

    val symNode = xPath.evaluate(".//C0SymKey", doc, XPathConstants.NODE) as Node
    return getC0SymKeyFromXmlNode(symNode)
}




