package com.zj.zn.prop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class TJ_IPTV_log implements Runnable
{
	//public static final String	url				= "http://192.168.1.197/";	// 本地

	public static final String	url				= "http://122.224.212.78:7878/";	// 本地

	private final int			SEND_PROP_LOG	= 1;

	private final int			SEND_STRAT_GAME	= 2;

	private final int			SEND_END_GAME	= 3;

	private int					toDoSomeThing;

	private String				encryptedstr	= null;

	private String				lastResult		= null;

	public static String		GAME_NAME		= "";

	private String				submit_url		= null;

	public static String		UNIQUE_ORDERID;

	private String				iptvID			= null;

	public final int			STATE_KOFEI		= 1;

	private int					CType;

	//唯一标示符

	//http://192.168.1.197/?m=iptv.game.click&iptvid=IPTV编号&gameid=游戏代号(整型)&version=游戏版本&gameplatform=平台版本
	public TJ_IPTV_log()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param iptvid userID
	 * @param propcord 道具码
	 * @param kindid 产品吗
	 */
	public void propLog(String iptvid, String propcord, String kindid)
	{
		this.iptvID = iptvid;
		toDoSomeThing = SEND_PROP_LOG;
		submit_url = "iptv=" + iptvid + "&propcord=" + propcord + "&kindid=" + kindid;
		encryptedstr = new MySever().getUrl("prop", "log", submit_url);
		System.out.println(encryptedstr);
		new Thread(this).start();
	}

	/**
	 * @param iptvid
	 * @param kindid
	 * @param statisticstype 统计类型(1,游戏统计 2,排行榜统计)
	 */
	public void stratGame(String iptvid, String kindid, String statisticstype)
	{
		this.iptvID = iptvid;
		toDoSomeThing = SEND_STRAT_GAME;
		String unique_orderid;//  唯一统计标识(时间戳13位+IPTV编号)
		unique_orderid = System.currentTimeMillis() + iptvid;
		UNIQUE_ORDERID = unique_orderid;
		submit_url = "iptv=" + iptvid + "&unique_orderid=" + unique_orderid + "&kindid=" + kindid + "&statisticstype=" + statisticstype + "&operationtype=" + "add";
		encryptedstr = new MySever().getUrl("playtime.statistics", "add", submit_url);
		System.out.println(encryptedstr);
		new Thread(this).start();
	}

	/**
	 * @param iptvid
	 * @param kindid
	 * @param statisticstype 统计类型(1,游戏统计 2,排行榜统计)
	 */
	public void endGame(String iptvid, String kindid, String statisticstype)
	{
		this.iptvID = iptvid;
		toDoSomeThing = SEND_END_GAME;
		String unique_orderid;//  唯一统计标识(时间戳13位+IPTV编号)
		unique_orderid = UNIQUE_ORDERID;
		submit_url = "iptv=" + iptvid + "&unique_orderid=" + unique_orderid + "&kindid=" + kindid + "&statisticstype=" + statisticstype + "&operationtype=" + "update";
		encryptedstr = new MySever().getUrl("playtime.statistics", "add", submit_url);
		System.out.println(encryptedstr);
		new Thread(this).start();
	}

	/**
	 * @param iptvid
	 * @param kindid
	 * @param url
	 */

	public void run()
	{
		// TODO Auto-generated method stub
		try
		{
			switch (toDoSomeThing)
			{

				case SEND_PROP_LOG:
					lastResult = new String(getViaHttpConnection(encryptedstr));
					//System.out.println("执行保存="+lastResult);
					break;
				case SEND_STRAT_GAME:
					lastResult = new String(getViaHttpConnection(encryptedstr));
					System.out.println("执行结果:" + lastResult);
					break;
				case SEND_END_GAME:
					lastResult = new String(getViaHttpConnection(encryptedstr));
					System.out.println(lastResult);
					break;

			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private byte[] getViaHttpConnection(String url) throws IOException
	{
		HttpConnection httpc = null;
		InputStream is = null;
		int rc;
		byte[] data;
		try
		{
			httpc = (HttpConnection) Connector.open(url);
			httpc.setRequestMethod(HttpConnection.GET);//get连接
			rc = httpc.getResponseCode();
			if (rc == HttpConnection.HTTP_OK)
				if (rc != HttpConnection.HTTP_OK)
				{
					throw new IOException("HTTP response code: " + rc);
				}
			is = httpc.openInputStream();
			String type = httpc.getType();
			int ch;
			int len = (int) httpc.getLength();
			if (len > 0)
			{//连接方式1
				int actual = 0;
				int bytesread = 0;
				data = new byte[len];
				while ((bytesread != len) && (actual != -1))
				{
					actual = is.read(data, bytesread, len - bytesread);
					bytesread += actual;
				}
			}
			else
			{ //上面的方式没读取到
				ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
				while ((ch = is.read()) != -1)
				{
					bStrm.write(ch);
				}
				data = bStrm.toByteArray();
			}

		}
		catch (ClassCastException e)
		{
			throw new IllegalArgumentException("Not an HTTP URL");
		}
		finally
		{
			if (is != null)
				is.close();
			if (httpc != null)
				httpc.close();
		}
		return data;
	}

}

////////////////////////////////////////////////////
class MySever
{
	private final String	APP_KEY	= "bH2i2F3h58cj9bfl9A2baSeQ0A7Z7p0T8seC36aT9Cbn8jcn8z3DcH3S5g6U9Z3D";	//密钥

	// ?m=iptv.game.fish.
	/**
	 * @param gameName 游戏地址名 如 fish
	 * @param action 提交参数 示例 "get"
	 * @param param 提交内容 submit_url
	 * @return
	 */
	public String getUrl(String gameName, String action, String param)
	{
		String input = encrypt(param);
		input = new md5().getMD5ofStr((input + APP_KEY)) + input;
		String COMPANYURL = TJ_IPTV_log.url;
		String url = COMPANYURL + "?m=iptv.game." + gameName + "." + action + "&input=" + input;
		return url;
	}

	/**
	 * 加密专用函数
	 * 
	 * @param param
	 * @return
	 */
	private String enZnCode(String param)
	{
		String input = encrypt(param);
		input = new md5().getMD5ofStr((input + APP_KEY)) + input;
		return input;
	}

	/**
	 * 加密方法，并返回一个字符串值 被调用的方法
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
