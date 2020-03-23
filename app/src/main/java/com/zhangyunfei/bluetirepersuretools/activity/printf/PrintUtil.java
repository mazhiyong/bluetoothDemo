/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package com.zhangyunfei.bluetirepersuretools.activity.printf;

import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;



/**
 * @Description: TODO
 * @author
 * @date 2015-5-28 下午3:43:52
 * @version V1.0
 */
public class PrintUtil {
	private static final String TAG = null;
	PrintDataService printDataService;
	WaybillPrintTemplate template;
	MyPrintInfo bean;

	private PrintUtil(PrintDataService printDataService) {
		super();
		if (printDataService == null)
			throw new NullPointerException();
		this.printDataService = printDataService;
	}

	public static PrintUtil create(PrintDataService printDataService) {
		return new PrintUtil(printDataService);
	}

	public PrintUtil setTemplate(WaybillPrintTemplate template) {
		this.template = template;
		return this;
	}

	public PrintUtil setData(MyPrintInfo bean) {
		this.bean = bean;
		return this;
	}

	public void print() {
		if (printDataService == null)
			throw new NullPointerException();
		if (template == null) {
			return;
		}
		if (bean == null) {
			return;
		}
		Set<Entry<String, WaybillPrintTemplate.Point>> coll = template.getData().entrySet();
		Iterator<Entry<String, WaybillPrintTemplate.Point>> iterator = coll.iterator();
		Object[] arr = new Object[coll.size()];
		int n = 0;
		Entry<String, WaybillPrintTemplate.Point> tmp;
		while (iterator.hasNext()) {
			arr[n++] = iterator.next();
		}
		// 排序，先按y,在按x，升序
		sortBy_y_x(arr);

		// 处理一个上边距
		int marginTop = template.getMarginTop();
		if (marginTop > 0)
			printDataService.move_vertical_relative(marginTop);

		// int lastY = 0;
		int yCursor = 0;
		Entry<String, WaybillPrintTemplate.Point> item;
		WaybillPrintTemplate.Point point1;
		String filedValue = "";
		for (int i = 0; i < arr.length; i++) {
			item = (Entry<String, WaybillPrintTemplate.Point>) arr[i];
			if (item == null)
				continue;
			point1 = item.getValue();
			filedValue = bean.get(item.getKey());
			if (TextUtils.isEmpty(filedValue))// 遇到空字段，跳过
				continue;
			if (point1.y > yCursor) {// 遇到一个 靠下的坐标时
				int y_relative = point1.y - yCursor;
				printDataService.move_vertical_relative(y_relative);
				yCursor += y_relative;
				printDataService.move_horizontal_abslute(point1.x);
				printDataService.send(filedValue);
				// lastY = point1.y;
			} else if (point1.y < yCursor) {
				// 遇到反向坐标， do nothing
			} else {// 遇到同行坐标
				printDataService.move_horizontal_abslute(point1.x);
				printDataService.send(filedValue);
			}

		}
		printDataService.sendComplete();
	}

	/**
	 * 
	 * @Description: TODO 排序，先按 y 升序，再按 x升序 排列
	 * @param @param arr
	 * @return void
	 * @throws
	 */
	private void sortBy_y_x(Object[] arr) {
		Arrays.sort(arr, new Comparator<Object>() {

			@Override
			public int compare(Object obj1, Object obj2) {
				if (obj1 == null && obj2 == null)
					return 0;
				if (obj1 == null && obj2 != null)
					return -1;
				if (obj1 != null && obj2 == null)
					return 1;

				WaybillPrintTemplate.Point p1 = ((Entry<String, WaybillPrintTemplate.Point>) obj1).getValue();
				WaybillPrintTemplate.Point p2 = ((Entry<String, WaybillPrintTemplate.Point>) obj2).getValue();
				if (p1.y != p2.y) {
					return p1.y - p2.y;
				} else {
					return p1.x - p2.x;
				}
			}
		});
		StringBuilder sb = new StringBuilder();
		int n = 0;
		for (Object i : arr) {
			if (i == null)
				continue;
			if (n != 0)
				sb.append(",");
			sb.append(i == null ? "" : ((Entry<String, WaybillPrintTemplate.Point>) i).getValue().toString());
		}
		Log.i(TAG, "排序后：" + sb.toString());

	}

	private void write(WaybillPrintTemplate.Point point, String fieldString) {
		if (point == null)
			return;

	}

}
