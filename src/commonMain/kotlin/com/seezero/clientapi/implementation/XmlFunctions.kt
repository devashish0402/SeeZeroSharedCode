package com.seezero.clientapi.implementation

import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

@Throws(Exception::class)
public fun loadXMLFromString(xml: String): Document {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val `is` = InputSource(StringReader(xml))
    return builder.parse(`is`)
}