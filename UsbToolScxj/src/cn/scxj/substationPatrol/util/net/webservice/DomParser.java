package cn.scxj.substationPatrol.util.net.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cn.scxj.usbnet.Logger;


public class DomParser {

	public static Document getDocument(InputStream inputStream) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(inputStream);
			Document dom = builder.parse(inputSource);

			return dom;

		} catch (Exception e) {
			Logger.warn("DomParser"+ e.getMessage() + e.getStackTrace());
		}
		return null;
	}

	public static Document getDocument(URL url) {
		try {
			return getDocument(url.openConnection().getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * 快速获得一个简单XML中的某项数据，改方法只适用于某个值在DOM中只存在一个
	 * */
	public static Node getElementByTagName(Node parentNode, String tagName) {
		try {
			NodeList list = parentNode.getChildNodes();
			if (list.getLength() > 0) {
				for (int i = 0; i < list.getLength(); i++) {
					if(list.item(i).getNodeName().equalsIgnoreCase(tagName)){
						return list.item(i);	
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Logger.warn("DomParser"+ e.getMessage() + e.getStackTrace());
		}
		return null;
	}


	public static List<Node> getElementsByTagName(Node parentNode, String tagName) {
		List<Node> nodes = new ArrayList<Node>();
		try {
			NodeList list = parentNode.getChildNodes();
			if (list.getLength() > 0) {
				for (int i = 0; i < list.getLength(); i++) {
					if(list.item(i).getNodeName().equalsIgnoreCase(tagName)){
						nodes.add(list.item(i));
					}
				}
			}
		} catch (Exception e) {
			Logger.warn("DomParser"+ e.getMessage() + e.getStackTrace());
		}
		return nodes;
	}


	/**
	 * 快速获得一个简单XML中的某项数据，改方法只适用于某个值在DOM中只存在一个
	 * */
	public static String getElementValueByTagName(Node parentNode, String tagName) {
		Node node = getElementByTagName(parentNode,tagName);
		if(node != null){
			//return getElementByTagName(parentNode,tagName).getFirstChild().getNodeValue();
			String nodeValue = "";
			if (node != null && node.getFirstChild() != null && node.getFirstChild().getNodeValue() != null) {
				nodeValue = node.getFirstChild().getNodeValue().trim();
			}
			return nodeValue;
		}else {
			return null;
		}	
	}


	/**
	 * 快速获得一个简单XML中属性
	 * */
	public static String getElementAttributeByTagName(Node node, String attributeName) {
		NamedNodeMap maps = node.getAttributes();
		if (maps.getLength() > 0) {
			Node nameAttrNode = maps.getNamedItem(attributeName);
			return nameAttrNode.getTextContent().trim();
		}
		return null;
	}

	/**
	 * xpath解析
	 * */
	public static String xPath(InputStream inputStream,String xpath) throws XPathExpressionException{
		XPathFactory xPathFactory = XPathFactory.newInstance();
		// To get an instance of the XPathFactory object itself.
		XPath xPath = xPathFactory.newXPath();
		// Create an instance of XPath from the factory class.
		//String expression = "SomeXPathExpression";
		XPathExpression xPathExpression = xPath.compile(xpath);
		// Compile the expression to get a XPathExpression object.
		return xPathExpression.evaluate(inputStream);
	}
}
