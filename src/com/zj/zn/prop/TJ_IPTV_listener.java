package com.zj.zn.prop;


public interface TJ_IPTV_listener
{

	/**
	 * @param RetnValue 返回值
	 *  成功 ZJ_BUY_SCUCCESS = 1;
	 *  失败 ZJ_BUY_LOSE = 2; 
	 *  不能购买 ZJ_BUY_ORDER = 3;
	 *  连续购买三次 ZJ_BUY_TOP3 = 4;
	 *   封顶 ZJ_BUY_TOP = 5;
	 * @param PropCodes 购买的道具码
	 * @param custom 自定义参数   do_BuyProp自定义参数传的什么 接收的就是什么参数
	 */
	public void TJ_PROP_listener(int RetnValue, String PropCodes, int custom);

}

interface READ_listener
{

	/**
	 * @param str 返回的文本
	 * @param retnType 区分返回
	 */
	void ZJ_PROP_ReadServer(String str, int retnType);

}