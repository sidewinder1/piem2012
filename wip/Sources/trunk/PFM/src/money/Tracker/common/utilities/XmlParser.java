package money.Tracker.common.utilities;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlParser {
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory
			.newInstance();
	DocumentBuilder builder = null;
	private static XmlParser sInstance;
	
	public XmlParser() {
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static XmlParser getInstance(){
		return (sInstance == null) ? (sInstance = new XmlParser()) : sInstance;
	}
	
	public Document getDocument(String path){
		Document document = null;
		try {
		    document = builder.parse(
		            new FileInputStream(path));
		} catch (SAXException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return document;
	}
}