package com.kdg7.webservice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.utils.DateUtil;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;

public class WebService {

	private static WebService webService;

	private WebService() {
	};

	public static WebService getinstance() {
		if (webService == null) {
			webService = new WebService();
		}
		return webService;
	}

	public String getData(String sqlid, String param) throws Exception {
		String ret = "";

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("in0", DateUtil.encode16(param));
		map.put("in1", DateUtil.encode16(sqlid));
		try {
			ret = getWebService(Constant.STM_NAMESPACE,Constant.STM_WEBSERVICE_URL_dx, "uf_json_getdata", map).toString();
		} catch (Exception e) {
			throw new Exception("网络错误"+e.getMessage());
		}
		try {
			ret = DateUtil.decode16(ret);
			System.out.println("json:"+ret);
			JSONObject json = new JSONObject(ret);
			return json.toString();
		} catch (Exception e) {
			throw new Exception("数据错误"+ret);
		}
	}
	
	public String setData(String sqlid, String param,String czy,String pk) throws Exception {
		String ret = "";

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("in0", DateUtil.encode16(sqlid));
		map.put("in1", DateUtil.encode16(param));
		map.put("in2", DateUtil.encode16(czy));
		map.put("in3", DateUtil.encode16(pk));
		try {
			ret = getWebService(Constant.STM_NAMESPACE,
					Constant.STM_WEBSERVICE_URL_dx, "uf_json_setdata", map).toString();
		} catch (Exception e) {
			throw new Exception("网络错误"+e.getMessage());
		}
		try {
			ret = DateUtil.decode16(ret);
			System.out.println("json:"+ret);
			JSONObject json = new JSONObject(ret);
			return json.toString();
		} catch (Exception e) {
			throw new Exception("数据错误"+ret);
		}
	}
	
	public String setData2(String sqlid, String param,String czy,String pk) throws Exception {
		String ret = "";

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("in0", DateUtil.encode16(sqlid));
		map.put("in1", DateUtil.encode16(param));
		map.put("in2", DateUtil.encode16(czy));
		map.put("in3", DateUtil.encode16(pk));
		try {
			ret = getWebService(Constant.STM_NAMESPACE,
					Constant.STM_WEBSERVICE_URL_dx, "uf_json_setdata2", map).toString();
		} catch (Exception e) {
			throw new Exception("网络错误"+e.getMessage());
		}
		try {
			ret = DateUtil.decode16(ret);
			System.out.println("json:"+ret);
			JSONObject json = new JSONObject(ret);
			return json.toString();
		} catch (Exception e) {
			throw new Exception("数据错误"+ret);
		}
	}
	
	public String setData2_p11(String sqlid, String param,String czy,String pk,byte[] data) throws Exception {
		String ret = "";

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("in0", DateUtil.encode16(sqlid));
		map.put("in1", DateUtil.encode16(param));
		map.put("in3", DateUtil.encode16(czy));
		map.put("in2", DateUtil.encode16(pk));
		map.put("in4", data);
		try {
			ret = getWebService(Constant.STM_NAMESPACE,
					Constant.STM_WEBSERVICE_URL_dx, "uf_json_setdata2_p11", map).toString();
		} catch (Exception e) {
			throw new Exception("网络错误"+e.getMessage());
		}
		try {
			ret = DateUtil.decode16(ret);
			System.out.println("json:"+ret);
			JSONObject json = new JSONObject(ret);
			return json.toString();
		} catch (Exception e) {
			throw new Exception("数据错误"+ret);
		}
	}
	

//	 /**
//	 * ����ָ����webservice
//	 *
//	 * @param namespace
//	 * @param url
//	 * @param method
//	 * @param param
//	 * @return
//	 */
//	 public Object getWebService(String namespace, String url, String method,
//	 Map<String, Object> param) {
//			
//			String soapmethod = namespace + "/" + method;
//			SoapObject rpc = new SoapObject(namespace, method);//
//			Set<String> set = param.keySet();
//			Iterator<String> ite = set.iterator();
//			while (ite.hasNext()) {
//				String paraName = ite.next();
//				Object paravalue = param.get(paraName);
//				rpc.addProperty(paraName, paravalue);
//			}
//			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//					SoapSerializationEnvelope.VER11);
//			
//			envelope.dotNet = false;
//			envelope.setOutputSoapObject(rpc);
//			(new MarshalBase64()).register(envelope); 
//			
//			HttpTransportSE httpse = new HttpTransportSE(url,1000*60);//
//			httpse.debug = true;
//			String costTime = "";
//			try {
//				final long StartMillis = System.currentTimeMillis();
//				httpse.call(soapmethod, envelope);
//				Object obj = envelope.getResponse();
//				final long endMillis = System.currentTimeMillis();
//				costTime = (endMillis-StartMillis)+"";
//				
//				if (obj instanceof SoapObject) {
//					SoapObject result = (SoapObject) obj;
//					return result;
//				}
//				// �ж�obj �ǲ��� String ��ʵ��
//				if (obj instanceof String) {
//					String result = (String) obj;
//					result = result.replace("{\"flag\":", "{\"costTime\":"+costTime+",\"flag\":");
//					return result;
//				}
//				// �ж�obj �ǲ��� Vector ��ʵ��
//				if (obj instanceof Vector) {
//					Vector<Object> result = (Vector<Object>) obj;
//					return result;
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				return "";
//			}
//			
//			return  null;
//		}

	public static Object getWebService(String namespace, String url,
			String method, Map<String, Object> params) throws Exception {
		String s = "";
		try {
			String soapRequestData = buildRequestData(method, namespace, params);
			String str = invoke(soapRequestData, url, method);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream iStream = new ByteArrayInputStream(str.getBytes());
			Document doc = builder.parse(iStream);
			Element root = doc.getDocumentElement();
			NodeList nodes = root.getElementsByTagName(method + "Return");
			if (nodes != null) {
				for (int i = 0; i < nodes.getLength(); i++) {
					Node ndes = nodes.item(i);
					s = ndes.getFirstChild().getNodeValue();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return s;
	}

	private static String invoke(String soapRequestData, String url,
			String method) throws Exception {
		PostMethod postMethod = new PostMethod(url);
		// ����POST��������ʱ
		postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
		Header header = new Header("SOAPAction", "http://tempurl.org/" + method);
		postMethod.setRequestHeader(header);
		try {
			byte[] b = soapRequestData.getBytes("utf-8");
			InputStream inputStream = new ByteArrayInputStream(b, 0, b.length);
			RequestEntity re = new InputStreamRequestEntity(inputStream,
					b.length, "text/xml; charset=utf-8");
			postMethod.setRequestEntity(re);
			HttpClient httpClient = new HttpClient();
			HttpConnectionManagerParams managerParams = httpClient
					.getHttpConnectionManager().getParams();
			// �������ӳ�ʱʱ��(��λ����)
			managerParams.setConnectionTimeout(30000);
			// ���ö����ݳ�ʱʱ��(��λ����)
			managerParams.setSoTimeout(100000);
			int statusCode = httpClient.executeMethod(postMethod);
			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception(postMethod.getStatusLine()+"");
			}
			InputStream txtis = postMethod.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(txtis));
			String tempbf;
			StringBuffer html = new StringBuffer(100);
			while ((tempbf = br.readLine()) != null) {
				html.append(tempbf);
			}
			txtis.close();
			inputStream.close();
			return html.toString();
		} catch (Exception e) {
			throw e;
		} finally {
			postMethod.releaseConnection();
		}
	}

	private static String buildRequestData(String methodName, String namespace,
			Map<String, Object> patameterMap) {
		StringBuffer soapRequestData = new StringBuffer();
		soapRequestData.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soapRequestData
				.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
						+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
						+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		soapRequestData.append("<soapenv:Body>");
		soapRequestData
				.append("<ns1:"
						+ methodName
						+ " soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\""
						+ namespace + "\">");
		Set<String> nameSet = patameterMap.keySet();
		for (String name : nameSet) {
			if ("uf_json_setdata2_p11".equals(methodName)) {
				if ("in4".equals(name)) {
					String s = Base64.encodeToString(DataCache.getinition()
							.getImageByte(), Base64.DEFAULT);
					DataCache.getinition().setImageByte(null);
					soapRequestData.append("<" + name
							+ " xsi:type='xsd:base64Binary'>" + s + "</" + name
							+ ">");
				} else {
					soapRequestData.append("<" + name
							+ " xsi:type='xsd:string'>"
							+ patameterMap.get(name) + "</" + name + ">");
				}
			} else if ("uf_json_setdata2_p1".equals(methodName)) {
				if ("in2".equals(name)) {
					String s = Base64.encodeToString(DataCache.getinition()
							.getImageByte(), Base64.DEFAULT);
					DataCache.getinition().setImageByte(null);
					soapRequestData.append("<" + name
							+ " xsi:type='xsd:base64Binary'>" + s + "</" + name
							+ ">");
				} else {
					soapRequestData.append("<" + name
							+ " xsi:type='xsd:string'>"
							+ patameterMap.get(name) + "</" + name + ">");
				}
			} else {
				soapRequestData.append("<" + name + " xsi:type='xsd:string'>"
						+ patameterMap.get(name) + "</" + name + ">");
			}

		}
		soapRequestData.append("</ns1:" + methodName + ">");
		soapRequestData.append("</soapenv:Body>");
		soapRequestData.append("</soapenv:Envelope>");
		return soapRequestData.toString();
	}
}
