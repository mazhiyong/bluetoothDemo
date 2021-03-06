/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package com.zhangyunfei.bluetirepersuretools.activity.templates;

import java.util.HashMap;

import com.zhangyunfei.bluetirepersuretools.activity.printf.Waybill4PrintInfo;
import com.zhangyunfei.bluetirepersuretools.activity.printf.WaybillPrintTemplate;

/**
 * 申通快递的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_shentong extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(40, 33));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(50, 65));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(20, 55));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(135, 33));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(145, 65));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(115, 55));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(120, 71));

		data.put(Waybill4PrintInfo.KEY_pay_way_cash, new Point(173, 78));
		
		data.put(Waybill4PrintInfo.KEY_is_bao_jia, new Point(29, 78));
		
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(55, 78));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(120, 85));
		data.put(Waybill4PrintInfo.KEY_total_price, new Point(185, 85));
	}
}