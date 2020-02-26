package com.seezero.clientapi.models


import com.seezero.clientapi.implementation.generateAsymkeyPair
import com.seezero.clientapi.implementation.loadXMLFromString
import org.w3c.dom.Node
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.util.*
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

fun main() {
    val testKey = generateAsymkeyPair()
    val xml = getXmlFromC0AsymKey(testKey, true)
    println(xml)
    val key2 = getC0AsymKeyFromXml(xml)
    println(getXmlFromC0AsymKey(key2, true))
}

class C0AsymKey {

    var generatedUTC = LocalDateTime.now()
    var id = UUID.randomUUID()
    private var hasPrivateKey = false
    lateinit var keyPair: KeyPair

    // private val Parameters: RSAParameters = RSAParameters

    constructor()

    constructor(kp: KeyPair, hasPrivKey: Boolean) : this() {
        generatedUTC = LocalDateTime.now()
        id = UUID.randomUUID()
        hasPrivateKey = hasPrivKey
        keyPair = kp
    }

    constructor(p_id: UUID, p_utc: LocalDateTime, kp: KeyPair, p_hasPrivate: Boolean) : this() {
        id = p_id
        generatedUTC = p_utc
        keyPair = kp
        hasPrivateKey = p_hasPrivate
    }

    constructor(publicKey: PublicKey) : this() {
        generatedUTC = LocalDateTime.now()
        id = UUID.randomUUID()
        keyPair = KeyPair(publicKey, null)
        hasPrivateKey = false

    }

    constructor(p_id: UUID, p_utc: LocalDateTime, publicKey: PublicKey) : this() {
        id = p_id
        generatedUTC = p_utc
        keyPair = KeyPair(publicKey, null)
        hasPrivateKey = false

    }

    fun hasPrivateKey(): Boolean {
        return hasPrivateKey
    }

}


fun getXmlFromC0AsymKey(asymKey: C0AsymKey, p_includePrivate: Boolean): String {
    val sb = StringBuilder()
    sb.append("<C0AsymKey>")
    sb.append("<GeneratedUTC>" + asymKey.generatedUTC + "</GeneratedUTC>")
    sb.append("<Id>" + asymKey.id + "</Id>")
    if (p_includePrivate && asymKey.hasPrivateKey()) {
        sb.append("<HasPrivate>" + "Y" + "</HasPrivate>")

    } else {
        sb.append("<HasPrivate>" + "N" + "</HasPrivate>")
    }
    sb.append("<Key>")

    sb.append("<Public>" + Base64.getEncoder().encodeToString(asymKey.keyPair.public.encoded) + "</Public>")
    if (p_includePrivate) {
        sb.append("<Private>" + Base64.getEncoder().encodeToString(asymKey.keyPair.private.encoded) + "</Private>")
    } else {
        sb.append("<Private>" + "</Private>")
    }
    sb.append("</Key>")
    sb.append("</C0AsymKey>")

    return sb.toString()

}

fun getC0AsymKeyFromXmlNode(asymNode: Node?): C0AsymKey {

    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val utc_string = xPath.compile(".//GeneratedUTC").evaluate(asymNode, XPathConstants.STRING) as String
    val utc = LocalDateTime.parse(utc_string)
    val id_string = xPath.compile(".//Id").evaluate(asymNode, XPathConstants.STRING) as String
    val id = UUID.fromString(id_string)
    val hasPrivateStr = xPath.compile(".//HasPrivate").evaluate(asymNode, XPathConstants.STRING) as String
    val hasPrivate = (hasPrivateStr.equals("Y"))
    val publicStr = xPath.compile(".//Public").evaluate(asymNode, XPathConstants.STRING) as String
    val publicBytes = Base64.getDecoder().decode(publicStr)
    val pubKeySpec = X509EncodedKeySpec(publicBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    val pubKey = keyFactory.generatePublic(pubKeySpec)
    if (hasPrivate) {
        val privateStr = xPath.compile(".//Private").evaluate(asymNode, XPathConstants.STRING) as String
        val privateBytes = Base64.getDecoder().decode(privateStr)
        val priKeySpec = PKCS8EncodedKeySpec(privateBytes)
        val priKey = keyFactory.generatePrivate(priKeySpec)
        return C0AsymKey(id, utc, KeyPair(pubKey, priKey), true)
    } else {
        return C0AsymKey(id, utc, KeyPair(pubKey, null), false)
    }

}

fun getC0AsymKeyFromXml(p_xml: String): C0AsymKey {
    val doc = loadXMLFromString(p_xml)
    doc
        .documentElement?.normalize()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val userNode = xPath.evaluate(".//C0AsymKey", doc, XPathConstants.NODE) as Node

    return getC0AsymKeyFromXmlNode(userNode)


}
/* from and to XML below to match C# style serialization - not used */
//
//    public fun toXml(p_includePrivate: Boolean): String {
//        val sb = StringBuilder()
//        sb.append("<C0AsymKey>")
//        sb.append("<GeneratedUTC>" + generatedUTC + "</GeneratedUTC>")
//        sb.append("<Id>" + id + "</Id>")
//        // key pair is in 'kp'
//        // key pair is in 'kp'
//
//
//        val kf = KeyFactory.getInstance("RSA")
//        val ks: RSAPrivateCrtKeySpec = kf.getKeySpec(keyPair.private, RSAPrivateCrtKeySpec::class.java)
//
//        val ks2 = ks
//
//        sb.append("<Key>")
//        sb.append("<RSAKeyValue>")
//
//        if (p_includePrivate) {
//            sb.append("<Modulus>" + ks.modulus + "</Modulus>")
//            sb.append("<Exponent>" + ks2.publicExponent + "</Exponent>")
//            sb.append("<P>" + ks2.primeP + "</P>")
//            sb.append("<Q>" + ks2.primeQ + "</Q>")
//            sb.append("<DP>" + ks2.primeExponentP + "</DP>")
//            sb.append("<DQ>" + ks2.primeExponentQ + "</DQ>")
//            sb.append("<InverseQ>" + ks2.crtCoefficient + "</InverseQ>")
//            sb.append("<D>" + ks.privateExponent + "</D>")
//        } else {
//            sb.append("<Modulus>" + ks.modulus + "</Modulus>")
//            sb.append("<Exponent>" + ks2.publicExponent + "</Exponent>")
//        }
//
//        println("</RSAKeyValue>")
//        sb.append("</Key>")
//        sb.append("</C0AsymKey>")
//
//        return sb.toString()
//
//    }
//
//    fun HasPrivate(): Boolean {
//        val kf = KeyFactory.getInstance("RSA")
//        val ks = kf.getKeySpec(
//            keyPair.private, RSAPrivateCrtKeySpec::class.java
//        )
//        return ks.privateExponent != null && ks.primeExponentP != null && ks.primeExponentQ != null &&
//                ks.primeP != null && ks.primeQ != null;
//    }
//
//    fun fromXmlNode(p_node: Document?): C0AsymKey {
//
//        println(p_node!!.parentNode)
//        p_node?.documentElement?.normalize()
////        val eElement = p_node as Element
//
//        val xPath: XPath = XPathFactory.newInstance().newXPath()
//        val nodex = xPath.compile("//C0AsymKey").evaluate(
//            p_node, XPathConstants.NODE
//        )
//        //       val sss = eElement.getElementsByTagName("GeneratedUTC").item(0).textContent
//        val utc_string = xPath.compile("//GeneratedUTC").evaluate(p_node, XPathConstants.STRING) as String
//        //    val utc = LocalDateTime.parse(utc_string)
//        val utc = LocalDateTime.now()
//        val id_string = xPath.compile("//Id").evaluate(p_node, XPathConstants.STRING) as String
//        val id = UUID.fromString(id_string)
//        val expBytes =
//            Base64.getDecoder().decode(xPath.compile("//Exponent").evaluate(p_node, XPathConstants.STRING) as String)
//        val modBytes =
//            Base64.getDecoder().decode(xPath.compile("//Modulus").evaluate(p_node, XPathConstants.STRING) as String)
//        val dBytes = Base64.getDecoder().decode(xPath.compile("//D").evaluate(p_node, XPathConstants.STRING) as String)
//
//        val modules = BigInteger(1, modBytes);
//        val exponent = BigInteger(1, expBytes);
//        val d = BigInteger(1, dBytes);
//
//        val factory = KeyFactory.getInstance("RSA");
//
//        val pubSpec = RSAPublicKeySpec(modules, exponent);
//        val pubKey = factory.generatePublic(pubSpec);
//
//        val privSpec = RSAPrivateKeySpec(modules, d);
//        val privKey = factory.generatePrivate(privSpec);
//        val kp = KeyPair(pubKey, privKey)
//
//        return C0AsymKey(id, utc, kp)
//    }
//
//    public fun fromXml(p_xml: String): C0AsymKey {
//        val doc = loadXMLFromString(p_xml)
//        return fromXmlNode(doc)
//    }
