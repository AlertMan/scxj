package cn.scxj.substationPatrol.util.net.webservice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XmlParser {

	
	
	public static String parseBugXMl(String xml) {
//		List<LoginUsers> users = new ArrayList<LoginUsers>();
		String ststus="";
		InputStream in = new ByteArrayInputStream(xml.getBytes());
		Document dom = DomParser.getDocument(in);
		if (dom != null) {
			Element root = dom.getDocumentElement();
			NodeList lists = root.getElementsByTagName("RESULT");
			if (lists == null || lists.getLength() < 1) {
				return ststus;
			}
			for (int i=0; i<lists.getLength(); i++) {
				Node node = lists.item(i);
				ststus=DomParser.getElementValueByTagName(node, "STATUS");
			}
		}
		return ststus;
	}
	
	
	public static String parseBugXMl1(String xml) {
//		List<LoginUsers> users = new ArrayList<LoginUsers>();
		String bugId="";
		InputStream in = new ByteArrayInputStream(xml.getBytes());
		Document dom = DomParser.getDocument(in);
		if (dom != null) {
			Element root = dom.getDocumentElement();
			NodeList lists = root.getElementsByTagName("RESULT");
			if (lists == null || lists.getLength() < 1) {
				return bugId;
			}
			for (int i=0; i<lists.getLength(); i++) {
//				LoginUsers user = new LoginUsers();
				Node node = lists.item(i);
				bugId=DomParser.getElementValueByTagName(node, "STATUSSUCCESSINFO");
				if(bugId!=null||!bugId.equals("")){
					String [] bugIds;
					bugIds=bugId.split("\\|");
					if(bugIds.length>0){
						
//						bugId=bugId.split("\\|")[1];
						bugId=bugIds[1];
					}
				
				}
			}
		}
		return bugId;
	}
}
