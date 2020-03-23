/*
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.
 *
 */

package com.zhangyunfei.bluetirepersuretools.activity.printf;

import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_Mine;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_baishihuitong;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_chengji;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_quanfeng;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_shentong;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_shunfeng;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_tiantian;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_youzheng;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_yuantong;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_yunda;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_zhongtie;
import com.zhangyunfei.bluetirepersuretools.activity.templates.WaybillPrintTemplate_zhongtong;

/**
 * @author
 * @version V1.0
 * @Description: TODO
 * @date 2015-6-1 下午8:22:29
 */
public class TemplateFactory {
    public static final int KEY_TEMPLATE_ID_my = 0;
    public static final int KEY_TEMPLATE_ID_zhongtie = 1;
    public static final int KEY_TEMPLATE_ID_zhongtong = 2;
    public static final int KEY_TEMPLATE_ID_yunda = 3;
    public static final int KEY_TEMPLATE_ID_yuantong = 4;
    public static final int KEY_TEMPLATE_ID_youzheng = 5;
    public static final int KEY_TEMPLATE_ID_shunfeng = 6;
    public static final int KEY_TEMPLATE_ID_shentong = 7;
    public static final int KEY_TEMPLATE_ID_quanfeng = 8;
    public static final int KEY_TEMPLATE_ID_baishihuitong = 9;
    public static final int KEY_TEMPLATE_ID_chengji = 10;
    public static final int KEY_TEMPLATE_ID_tiantian = 11;

    private TemplateFactory() {
        super();
    }

    /**
     * @param @param  template_id
     * @param @return
     * @return WaybillPrintTemplate
     * @throws
     * @Description: TODO 根据id 返回 打印模板
     */
    public static WaybillPrintTemplate getTemplate(int template_id) {

        WaybillPrintTemplate tmp = null;
        switch (template_id) {
            case KEY_TEMPLATE_ID_my:
                tmp = new WaybillPrintTemplate_Mine();
                break;
            case KEY_TEMPLATE_ID_zhongtie:
                tmp = new WaybillPrintTemplate_zhongtie();
                break;
            case KEY_TEMPLATE_ID_zhongtong:
                tmp = new WaybillPrintTemplate_zhongtong();
                break;
            case KEY_TEMPLATE_ID_yunda:
                tmp = new WaybillPrintTemplate_yunda();
                break;
            case KEY_TEMPLATE_ID_yuantong:
                tmp = new WaybillPrintTemplate_yuantong();
                break;
            case KEY_TEMPLATE_ID_youzheng:
                tmp = new WaybillPrintTemplate_youzheng();
                break;
            case KEY_TEMPLATE_ID_shunfeng:
                tmp = new WaybillPrintTemplate_shunfeng();
                break;
            case KEY_TEMPLATE_ID_shentong:
                tmp = new WaybillPrintTemplate_shentong();
                break;
            case KEY_TEMPLATE_ID_quanfeng:
                tmp = new WaybillPrintTemplate_quanfeng();
                break;
            case KEY_TEMPLATE_ID_baishihuitong:
                tmp = new WaybillPrintTemplate_baishihuitong();
                break;
            case KEY_TEMPLATE_ID_chengji:
                tmp = new WaybillPrintTemplate_chengji();
                break;
            case KEY_TEMPLATE_ID_tiantian:
                tmp = new WaybillPrintTemplate_tiantian();
                break;
        }
        return tmp;
    }

}
