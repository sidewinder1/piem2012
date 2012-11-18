package money.Tracker.common.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.Environment;

public class XmlParser {
	private String CONFIG_FILE = "Pfm/PfmConfig.xml";
	private static String mBaseDir = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory
			.newInstance();
	DocumentBuilder builder = null;

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer;

    DOMSource source = null;
    StreamResult result = new StreamResult(
        new File(mBaseDir + File.separator + CONFIG_FILE));

	private Document _configDocument;
	private static XmlParser sInstance;

	public XmlParser() {
		try {
			builder = builderFactory.newDocumentBuilder();
			transformer = factory.newTransformer();
		} catch (ParserConfigurationException e) {
			Logger.Log(e.getMessage(), "XmlParser");
		} catch (TransformerConfigurationException e) {
			Logger.Log(e.getMessage(), "XmlParser");
		}
	}

	public String getConfigContent(String attributeName){
		if (_configDocument == null){
			_configDocument = getDocument(mBaseDir + File.separator + CONFIG_FILE);
			new DOMSource(_configDocument);
		}
		if (_configDocument == null){
			return "";
		}
		
		NodeList nodeList = _configDocument.getElementsByTagName(attributeName);
		if (nodeList == null || nodeList.getLength() == 0){
			return "";
		}
		return nodeList.item(0).getFirstChild().getNodeValue();
	}

	public void setConfigContent(String attributeName, String value){
		if (_configDocument == null){
			_configDocument = getDocument(mBaseDir + File.separator + CONFIG_FILE);
		}
		
		NodeList nodeList = _configDocument.getElementsByTagName(attributeName);
		if (nodeList == null || nodeList.getLength() == 0){
			return;
		}
		
		nodeList.item(0).getFirstChild().setNodeValue(value);
		
	    try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			Logger.Log(e.getMessage(), "XmlParser");
		}
	}
	
	public static XmlParser getInstance() {
		return (sInstance == null) ? (sInstance = new XmlParser()) : sInstance;
	}

	public Document getDocument(String path) {
		Document document = null;
		try {
			document = builder.parse(new FileInputStream(path));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return document;
	}
}