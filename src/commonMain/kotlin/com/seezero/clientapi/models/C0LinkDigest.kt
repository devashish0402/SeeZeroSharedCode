package com.seezero.clientapi.models

import com.seezero.clientapi.implementation.generateAsymkeyPair
import com.seezero.clientapi.implementation.loadXMLFromString
import org.w3c.dom.Node
import java.io.StringWriter
import java.time.LocalDateTime
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

fun main() {
    val user = C0User("test1234", UUID.randomUUID().toString())
    val asymKey = generateAsymkeyPair()
    val link = C0LinkDigest(user, asymKey)
    val xml = getEncodedStringFromC0LinkDigest(link)
    println(xml)
    val linkDigest = getLinkDigestFromEncodedString(xml)
    println(getXmlFromC0LinkDigest(linkDigest))
}

class C0LinkDigest {

    // This structure is used to send when a QR code needs generation
    // A Temporary keypair is generated to construct this

    var generatedUTC = LocalDateTime.now()
    var id = UUID.randomUUID()
    var user: C0User
    var asymKey: C0AsymKey
    var savedRef = ""

    constructor(p_user: C0User, p_asymKey: C0AsymKey) {
        generatedUTC = LocalDateTime.now()
        id = UUID.randomUUID()
        user = p_user
        asymKey = p_asymKey
        savedRef = ""
    }

    constructor(p_utc: LocalDateTime, p_id: UUID, p_user: C0User, p_asymKey: C0AsymKey) {
        generatedUTC = p_utc
        id = p_id
        user = p_user
        asymKey = p_asymKey
        savedRef = ""
    }

    constructor(p_utc: LocalDateTime, p_id: UUID, p_user: C0User, p_asymKey: C0AsymKey, p_ref: String) {
        generatedUTC = p_utc
        id = p_id
        user = p_user
        asymKey = p_asymKey
        savedRef = p_ref
    }


}

fun getXmlFromC0LinkDigest(linkDigest: C0LinkDigest): String {
    val sb = StringBuilder()
    sb.append("<C0LinkDigest>")
    sb.append("<GeneratedUTC>" + linkDigest.generatedUTC + "</GeneratedUTC>")
    sb.append("<Id>" + linkDigest.id.toString() + "</Id>")
    sb.append("<LinkUser>")
    sb.append(getXmlFromC0User(linkDigest.user))
    sb.append("</LinkUser>")
    sb.append("<Key>")
    sb.append(getXmlFromC0AsymKey(linkDigest.asymKey, false))
    sb.append("</Key>")
    sb.append("</C0LinkDigest>")
    return sb.toString()
}

fun getC0LinkDigestFromXmlNode(ldNode: Node): C0LinkDigest {

    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val userNode = xPath.evaluate(".//LinkUser/C0User", ldNode, XPathConstants.NODE) as Node

    val userName = xPath.evaluate(".//UserName", userNode, XPathConstants.STRING) as String
    val userId = xPath.evaluate(".//UserId", userNode, XPathConstants.STRING) as String
    val user = C0User(userName, userId)
    val myKeyNode = xPath.compile(".//Key/C0AsymKey").evaluate(ldNode, XPathConstants.NODE) as Node
    val asymKey = getC0AsymKeyFromXmlNode(myKeyNode)
    return C0LinkDigest(user, asymKey)
}

fun getC0LinkDigestFromXml(p_xml: String): C0LinkDigest {
    val doc = loadXMLFromString(p_xml)
    doc.documentElement?.normalize()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val ldNode = xPath.evaluate("/C0LinkDigest", doc, XPathConstants.NODE) as Node

    return getC0LinkDigestFromXmlNode(ldNode)

}

fun getEncodedStringFromC0LinkDigest(linkDigest: C0LinkDigest): String {
    return Base64.getEncoder().encodeToString((getXmlFromC0LinkDigest(linkDigest).toByteArray()))
}

fun getLinkDigestFromEncodedString(qrScan: String): C0LinkDigest {
    val xml = Base64.getDecoder().decode(qrScan).toString(Charsets.UTF_8)
    val user = getC0UserfromXml(xml)
    val doc = loadXMLFromString(xml)
    doc.documentElement?.normalize()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val utc_string = xPath.compile("//GeneratedUTC").evaluate(doc, XPathConstants.STRING) as String
    val utc = LocalDateTime.parse(utc_string)
    val id_string = xPath.compile("//Id").evaluate(doc, XPathConstants.STRING) as String
    val id = UUID.fromString(id_string)

    val myKey = xPath.compile("//C0AsymKey").evaluate(doc, XPathConstants.NODE) as Node
    val buf = StringWriter()
    val xform: Transformer = TransformerFactory.newInstance().newTransformer()
    xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    xform.transform(DOMSource(myKey), StreamResult(buf))
    val myAsymKey = getC0AsymKeyFromXml(buf.toString())

    return C0LinkDigest(utc, id, user, myAsymKey)
}