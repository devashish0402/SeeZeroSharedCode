package com.seezero.clientapi.models

import com.seezero.clientapi.implementation.*
import org.w3c.dom.Document
import java.time.LocalDateTime
import java.util.*
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec



class C0MetaData {
    var encryptedUTC = LocalDateTime.now()

    lateinit  var encryptedBy: String

    lateinit var encryptionKeyId: UUID // id for public key of user ! who sends file

    lateinit var encryptedSymKey: ByteArray

    var contentSize: Long = 0

    fun ToXml(): String {
        val sb = StringBuilder()
        sb.append("<C0MetaData>")
        sb.append("<C0MetaData>$encryptedUTC</EncryptedUTC>")
        sb.append("<EncryptedBy>$encryptedBy</EncryptedBy>")
        sb.append("<EncryptionKeyId>" + encryptionKeyId + "</EncryptionKeyId>")
        sb.append("<EncryptedSymKey>" + Base64.getEncoder().encodeToString(encryptedSymKey) + "</EncryptedSymKey>")
        sb.append("<ContentSize>$contentSize</ContentSize>")
        sb.append("</C0MetaData>")
        return sb.toString()
    }


    fun fromXmlNode(p_node: Document?): C0MetaData {
        val metaData = C0MetaData()
        p_node?.documentElement?.normalize()
        val xPath: XPath = XPathFactory.newInstance().newXPath()
        val utc = xPath.compile("/C0MetaData/EncryptedUTC").evaluate(p_node, XPathConstants.STRING) as String

        metaData.encryptedBy =
            xPath.compile("/C0MetaData/EncryptedBy").evaluate(p_node, XPathConstants.STRING) as String
        var encryptionKeyIdStr =
            xPath.compile("/C0MetaData/EncryptionKeyId").evaluate(p_node, XPathConstants.STRING) as String
        metaData.encryptionKeyId = UUID.fromString(encryptionKeyIdStr)
         var encodedKey = xPath.compile("/C0MetaData/EncryptedSymKey").evaluate(p_node, XPathConstants.STRING)  as String
        metaData.encryptedSymKey = Base64.getDecoder().decode(encodedKey)

        var longValue = xPath.compile("/C0MetaData/ContentSize").evaluate(p_node, XPathConstants.STRING) as String
         metaData.contentSize = longValue.toLong()
        return metaData;

    }

    fun fromXml(p_xml: String): C0MetaData {
        val doc = loadXMLFromString(p_xml)
        return fromXmlNode(doc)
    }

}