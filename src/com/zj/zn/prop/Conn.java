package com.zj.zn.prop;

public class Conn
{
	private static final String	APP_KEY	= "bH2i2F3h58cj9bfl9A2baSeQ0A7Z7p0T8seC36aT9Cbn8jcn8z3DcH3S5g6U9Z3D";	//��Կ

	/**
	 * ����ר�ú���
	 * 
	 * @param param
	 * @return ���ؼ���֮����ı�
	 */
	protected String enZnCode(String param)
	{
		String input = encrypt(param);
		input = new md5().getMD5ofStr((input + APP_KEY)) + input;
		return input;
	}

	/**
	 * ���ܷ�����������һ���ַ���ֵ �����õķ���
	 * 
	 * @param param
	 * @return
	 */

	private String encrypt(String param)
	{
		byte[] mkey = new md5().getMD5ofStr(APP_KEY).substring(1, 19).getBytes();
		int mlen = mkey.length;
		int mk;

		int num = param.length();
		byte[] output = new byte[num];
		byte[] strbyte = param.getBytes();
		for (int i = 0; i < num; i++)
		{
			mk = i % mlen;
			output[i] = (byte) (strbyte[i] ^ mkey[mk]);
		}

		return Base64.encode(output);
	}

}
