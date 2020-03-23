/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package com.zhangyunfei.bluetirepersuretools.activity.templates;

import com.zhangyunfei.bluetirepersuretools.activity.printf.MyPrintInfo;
import com.zhangyunfei.bluetirepersuretools.activity.printf.Waybill4PrintInfo;
import com.zhangyunfei.bluetirepersuretools.activity.printf.WaybillPrintTemplate;

import java.util.HashMap;

/**
 * 自定义打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_Mine extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(MyPrintInfo.NAME, new Point(80, 38));
		data.put(MyPrintInfo.PHONE, new Point(45, 60));
		data.put(MyPrintInfo.ADDRESS, new Point(25, 46));

	}
}