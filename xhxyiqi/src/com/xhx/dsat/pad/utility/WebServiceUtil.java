package com.xhx.dsat.pad.utility;

import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class WebServiceUtil {
	
	private static final String LOG_TAG = "WebServiceUtil";
	// WebService�������ռ�
	private static String NAMESPACE = "http://tempuri.org/";
	// ������������url
	private static String URL = "http://123.57.229.128/GACFCAServer/Service.asmx";// http://192.168.0.68:88/WeTestWS.asmx
	// ������
	//private static final String METHOD_NAME = "SearchProject";// GetWeListInfo
	//private static String SOAP_ACTION = "http://tempuri.org/SearchProject";// GetWeListInfo
	
	/**����Webservice�ķ���
	 * @param methodName ��������
	 * @param parameter ��������
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> CallWebServiceMethod(String methodName, ArrayList<Parameter> parameter) {
		try {

			// ����HttpTransportSE����,ͨ��HttpTransportSE��Ĺ��췽������ָ��WebService��url
			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.debug = true;
			// ָ��WebService�������ռ�ͺ�����
			SoapObject soapObject = new SoapObject(NAMESPACE, methodName);
			String soapAction = NAMESPACE + methodName;
			
			if(parameter != null && parameter.size() > 0) {
				// ���õ��÷���������ֵ
				for (int i = 0; i < parameter.size(); i++) {
					Parameter item = parameter.get(i);
					soapObject.addProperty(item.name, item.value);
				}
			}
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
			envelope.bodyOut = soapObject;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(soapObject);

			// ʹ��call��������WebService����
			transport.call(soapAction, envelope);
			Log.i(LOG_TAG, "DUMP>> " + transport.requestDump);
			Log.i(LOG_TAG, "DUMP<< " + transport.responseDump);
			SoapObject sb = null;
			try {
				sb = (SoapObject) envelope.getResponse();
			} catch (Exception e) {
				return null;
			}
			ArrayList<HashMap<String, String>> xmlMaps = new ArrayList<HashMap<String, String>>();
			SoapObject sbData  = (SoapObject)((SoapObject)sb.getProperty("diffgram")).getProperty("NewDataSet");//.getProperty(1);
			for (int i = 0; i < sbData.getPropertyCount(); i++) {
				SoapObject itemObject = (SoapObject)sbData.getProperty(i);
//				if(itemObject.hasProperty("ShopCode")){
//					itemObject.getProperty("ShopCode");
//				}
				if(itemObject == null) break;
				String temp = itemObject.toString();
				String[] tempStrings = temp.substring(temp.indexOf("{")+1, temp.lastIndexOf("}")).split("; ");
				HashMap<String, String> hashMap = new HashMap<String, String>();
				for (int j = 0; j < tempStrings.length; j++) {
					String startString = tempStrings[j].substring(0,  tempStrings[j].indexOf("="));
					String endString = tempStrings[j].substring(tempStrings[j].indexOf("=")+1,  tempStrings[j].length());
					endString = endString.equals("anyType{}")?"":endString;
					hashMap.put(startString, endString);
				}
				xmlMaps.add(hashMap);
			}
			return xmlMaps;
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			return null;
		}
	}
	
	public static String getNAMESPACE() {
		return NAMESPACE;
	}

	public static void setNAMESPACE(String nAMESPACE) {
		NAMESPACE = nAMESPACE;
	}

	public static String getURL() {
		return URL;
	}

	public static void setURL(String uRL) {
		URL = uRL;
	}

	public class Parameter {
		private String name;
		private String value;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
	}
}
