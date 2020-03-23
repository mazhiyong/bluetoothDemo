/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package com.zhangyunfei.bluetirepersuretools.activity.printf;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Description: TODO 订单数据 ，for 打印用的，描述信息
 * @author
 * @date 2015-5-28 下午2:11:13
 * @version V1.0
 */
public class MyPrintInfo implements Serializable {
	private static final long serialVersionUID = 3406805761985154572L;

	/**
	 * 姓名
	 */
	public final static String NAME = "KEY_name";
	// public final static String KEY_sender_compony;
	/**
	 * 电话
	 */
	public final static String PHONE = "KEY_phone";
	/**
	 * 地址
	 */
	public final static String ADDRESS = "KEY_address";

	private HashMap<String, String> data;

	public MyPrintInfo() {
		super();
		data = new HashMap<String, String>(16);
	}

	public void put(String key, String value) {
		data.put(key, value);
	}

	public String get(String key) {
		return data.get(key);
	}

}