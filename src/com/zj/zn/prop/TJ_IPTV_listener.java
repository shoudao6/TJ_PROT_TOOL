package com.zj.zn.prop;


public interface TJ_IPTV_listener
{

	/**
	 * @param RetnValue ����ֵ
	 *  �ɹ� ZJ_BUY_SCUCCESS = 1;
	 *  ʧ�� ZJ_BUY_LOSE = 2; 
	 *  ���ܹ��� ZJ_BUY_ORDER = 3;
	 *  ������������ ZJ_BUY_TOP3 = 4;
	 *   �ⶥ ZJ_BUY_TOP = 5;
	 * @param PropCodes ����ĵ�����
	 * @param custom �Զ������   do_BuyProp�Զ����������ʲô ���յľ���ʲô����
	 */
	public void TJ_PROP_listener(int RetnValue, String PropCodes, int custom);

}

interface READ_listener
{

	/**
	 * @param str ���ص��ı�
	 * @param retnType ���ַ���
	 */
	void ZJ_PROP_ReadServer(String str, int retnType);

}