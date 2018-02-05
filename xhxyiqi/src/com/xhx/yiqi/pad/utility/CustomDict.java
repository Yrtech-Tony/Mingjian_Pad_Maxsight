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
     * 因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
     */
    @Override
    public String toString() {
        return text;
    }

}
