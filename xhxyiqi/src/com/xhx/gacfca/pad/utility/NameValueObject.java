package com.xhx.gacfca.pad.utility;

public class NameValueObject {
	private String _dispaly;
	private String _value;
	
	public String getDisplay(){return _dispaly;}
	public void setDisplay(String value){_dispaly = value;}
	public String getValue(){return _value;}
	public void setValue(String value){_value = value;}
	
	@Override
	public String toString()
	{
		return _dispaly;
	}
}
