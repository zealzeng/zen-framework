/*
 * Copyright (c) 2012, All rights reserved.
 */
package com.github.zenframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Zeal 2012-11-2
 */
public class XmlUtils {
	
	private static XPathFactory xpathFactory = XPathFactory.newInstance();
	
	private Document document = null;
	
	
	public XmlUtils(String xml, String charset) throws IOException {
		document = loadDocument(xml, charset);
	}
	
	public NodeList getNodeList(String xpath) {
		return getChildNodeList(document, xpath);
	}
	
	public Node getNode(String xpath) {
		return getChildNode(document, xpath);
	}	
	
	public String getNodeText(String xpath) {
		return getChildNodeText(document, xpath);
	}	
	
	public Map<String, String> getNodeTextMap(String xpath) {
		return getChildNodeTextMap(document, xpath);
	}
	
	public Map<String, String> getNodeTextMap(String xpath, boolean sortKey) {
		return getChildNodeTextMap(document, xpath, sortKey);
	}
	
	
	
	
	private static Document loadDocument(String xml, String charset) throws IOException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);
	    try {
	        DocumentBuilder builder = domFactory.newDocumentBuilder();
	        InputStream in = IOUtils.toInputStream(xml, charset);
	        Document document = builder.parse(in);
	        return document;
	    }
	    catch (IOException e) {
	    	throw e;
	    }
	    catch (Exception e) {
	    	throw new IOException(e);
	    }
	}
	
	public static Node getRootNode(String xml, String charset) {
		Document document;
		try {
			document = loadDocument(xml, charset);
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return document.getFirstChild();
	}
	
	public static String getNodeAttribute(Node node, String attributeName) {
		NamedNodeMap map = node.getAttributes();
		if (map == null || map.getLength() <= 0) {
			return null;
		}
		Node attrNode = map.getNamedItem(attributeName);
	    return attrNode == null ? null : attrNode.getNodeValue();
	}
	
	public static Node getChildNode(Node parentNode, String xpath) {
		XPath path = xpathFactory.newXPath();
		try {
			XPathExpression expr  = path.compile(xpath);
			Object result = expr.evaluate(parentNode, XPathConstants.NODE);
			return (Node) result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getChildNodeText(Node rootNode, String xpath) {
		if (!xpath.endsWith("/text()")) {
			if (!xpath.endsWith("/")) {
				xpath += "/";
			}
			xpath += "text()";
		}
		XPath path = xpathFactory.newXPath();
		try {
			XPathExpression expr  = path.compile(xpath);
			Object result = expr.evaluate(rootNode, XPathConstants.STRING);
			return (String) result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	public static NodeList getChildNodeList(Node rootNode, String xpath) {
		XPath path = xpathFactory.newXPath();
		try {
			XPathExpression expr  = path.compile(xpath);
			Object result = expr.evaluate(rootNode, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			return nodes;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	public static Map<String, String> getChildNodeTextMap(Node rootNode, String xpath) {
		return getChildNodeTextMap(rootNode, xpath, false);
	}
	
	public static Map<String, String> getChildNodeTextMap(Node rootNode, String xpath, boolean sortKey) {
		NodeList nodeList = getChildNodeList(rootNode, xpath);
		if (nodeList == null || nodeList.getLength() <= 0) {
			return new LinkedHashMap<String, String>(0);
		}
		Map<String, String> map = null;
		if (sortKey) {
			map = new TreeMap<String, String>();
		}
		else {
			map = new LinkedHashMap<String, String>(nodeList.getLength());
		}
		
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			String key = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = firstChild == null ? "" : firstChild.getNodeValue();
			map.put(key, value);
		}
		return map;
	}
	
//	public static Node searchNodeByAttribute(NodeList nodeList, String attributeName, String attributeValue) {
//		for (int i = 0; i < nodeList.getLength(); ++i) {
//			Node node = nodeList.item(i);
//			NamedNodeMap map = node.getAttributes();
//			if (map == null) {
//				continue;
//			}
//			Node attr = map.getNamedItem(attributeName);
//			if (attr == null) {
//				continue;
//			}
//			return attributeValue.equals(attr.getNodeValue()) ? attr : null;
//		}
//		return null;
//	}
	
	public static Map<String, String> getNodeAttributeMap(Node node) {
		NamedNodeMap attrMap = node.getAttributes();
		Map<String, String> map = new LinkedHashMap<String, String>(attrMap.getLength());
		for (int i = 0; i < attrMap.getLength(); ++i) {
			Node attrNode = attrMap.item(i);
			String key = attrNode.getNodeName();
			String value = attrNode.getNodeValue();
			map.put(key, value);
		}
		return map;
	}
	
	public static void main(String[] args) throws Exception {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resp><city>深圳</city><updatetime>16:41</updatetime><wendu>26</wendu><fengli>1级</fengli><shidu>99%</shidu><fengxiang>东风</fengxiang><sunrise_1>05:40</sunrise_1><sunset_1>19:11</sunset_1><sunrise_2></sunrise_2><sunset_2></sunset_2><environment><aqi>18</aqi><pm25>8</pm25><suggest>各类人群可自由活动</suggest><quality>优</quality><MajorPollutants></MajorPollutants><o3>33</o3><co>1</co><pm10>15</pm10><so2>6</so2><no2>34</no2><time>16:00:00</time></environment><alarm><cityKey>1012806</cityKey><cityName><![CDATA[广东省深圳市]]></cityName><alarmType><![CDATA[雷电]]></alarmType><alarmDegree><![CDATA[黄色]]></alarmDegree><alarmText><![CDATA[广东省深圳市气象台发布雷电黄色预警]]></alarmText><alarm_details><![CDATA[深圳市气象局于06月20日08时54分发布雷电黄色预警信号，请注意防御。]]></alarm_details><standard><![CDATA[6小时内可能发生雷电活动，可能会造成雷电灾害事故。]]></standard><suggest><![CDATA[1、政府及相关部门按照职责做好防雷工作；2、密切关注天气，尽量避免户外活动。]]></suggest><imgUrl><![CDATA[http://static.etouch.cn/weather/alarm_icon/small/thunder_3@3x.png]]></imgUrl><time>2017-06-20 08:54:18</time></alarm><yesterday><date_1>19日星期一</date_1><high_1>高温 29℃</high_1><low_1>低温 25℃</low_1><day_1><type_1>大雨</type_1><fx_1>无持续风向</fx_1><fl_1>微风</fl_1></day_1><night_1><type_1>大到暴雨</type_1><fx_1>无持续风向</fx_1><fl_1>微风</fl_1></night_1></yesterday><forecast><weather><date>20日星期二</date><high>高温 29℃</high><low>低温 25℃</low><day><type>大到暴雨</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>大雨</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather><weather><date>21日星期三</date><high>高温 30℃</high><low>低温 26℃</low><day><type>暴雨</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>暴雨</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather><weather><date>22日星期四</date><high>高温 31℃</high><low>低温 27℃</low><day><type>阵雨</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>阵雨</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather><weather><date>23日星期五</date><high>高温 32℃</high><low>低温 27℃</low><day><type>雷阵雨</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>阵雨</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather><weather><date>24日星期六</date><high>高温 32℃</high><low>低温 27℃</low><day><type>阵雨</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>多云</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather></forecast><zhishus><zhishu><name>晨练指数</name><value>不宜</value><detail>有较强降水，请避免户外晨练，建议在室内做适当锻炼，保持身体健康。</detail></zhishu><zhishu><name>舒适度</name><value>较舒适</value><detail>白天有雨，从而使空气湿度加大，会使人们感觉有点儿闷热，但早晚的天气很凉爽、舒适。</detail></zhishu><zhishu><name>穿衣指数</name><value>热</value><detail>天气热，建议着短裙、短裤、短薄外套、T恤等夏季服装。</detail></zhishu><zhishu><name>感冒指数</name><value>较易发</value><detail>天气转凉，空气湿度较大，较易发生感冒，体质较弱的朋友请注意适当防护。</detail></zhishu><zhishu><name>晾晒指数</name><value>不宜</value><detail>有较强降水，不适宜晾晒。若需要晾晒，请在室内准备出充足的空间。</detail></zhishu><zhishu><name>旅游指数</name><value>较不宜</value><detail>温度适宜，风力不大，但预计将有有强降水出现，会给您的出游增添很多麻烦，建议您最好选择室内活动。</detail></zhishu><zhishu><name>紫外线强度</name><value>弱</value><detail>紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。</detail></zhishu><zhishu><name>洗车指数</name><value>不宜</value><detail>不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。</detail></zhishu><zhishu><name>运动指数</name><value>较不宜</value><detail>有较强降水，建议您选择在室内进行健身休闲运动。</detail></zhishu><zhishu><name>约会指数</name><value>不适宜</value><detail>室外有风，同时较强降水天气更是会给室外约会增添许多麻烦，最好在室内促膝谈心。</detail></zhishu><zhishu><name>雨伞指数</name><value>带伞</value><detail>有较强降水，您在外出的时候一定要带雨伞，以免被雨水淋湿。</detail></zhishu></zhishus></resp><!-- 10.10.156.163(10.10.156.163):43773 ; 10.10.156.163:8080 -->";
		Node rootNode = XmlUtils.getRootNode(xml, "UTF-8");
		Map<String, String> map = XmlUtils.getChildNodeTextMap(rootNode, "/resp/*");
		System.out.println(map);
		map = XmlUtils.getChildNodeTextMap(rootNode, "/resp/environment/*");
		System.out.println(map);
		map = XmlUtils.getChildNodeTextMap(rootNode, "/resp/alarm/*");
		System.out.println(map);
		NodeList nodeList = XmlUtils.getChildNodeList(rootNode, "/resp/forecast/weather");
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			map = getChildNodeTextMap(node, "*");
			System.out.println(map);
//			Map<String,String >dayMap = getChildNodeTextMap(node, "day/*");
			
		}
	}

}
