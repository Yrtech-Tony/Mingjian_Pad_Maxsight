package com.xhx.yiqi.pad.utility;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CustomDict implements Serializable {
	private String value;
    private String text;

    public CustomDict() {
    }

    public CustomDict(String value, String text) {
        super();
        this.value = value;
        this.text = text;
    }
    
    public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	/**
     * ��Ϊ����������ʾ���ݵ�ʱ����������������Ķ������ַ���������£�ֱ�Ӿ�ʹ�ö���.toString()
     */
    @Override
    public String toString() {
        return text;
    }

}
