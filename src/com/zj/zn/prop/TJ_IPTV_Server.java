package com.zj.zn.prop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class TJ_IPTV_Server implements Runnable
{
	protected static final byte	GET_PRICE				= 0;

	protected static final byte	BUY_PROP				= 1;

	protected static final byte	BUY_PROP_ISTEST			= 2;
	
	protected static final byte	BUY_PROP_ESC			= 4;

	private byte				toDoSomeThing;
//获取单价的
	public static String	GET_PROPPRICE_ADDRESS	;

	private String				lastResult				= null;

	private String				encryptedstr			= null;

	private READ_listener		read_listener			= null;

	//http://122.224.212.78:7878/?m=iptv.props.get&input=803019e40ba4880aa3510bd73cbbf6d3XQtdVwwCDmgKAQYDVw==
	public TJ_IPTV_Server(READ_listener rl)
	{
		read_listener = rl;
	}

	public TJ_IPTV_Server()
	{

	}

	/**
	 * @param kindid 读取单价
	 */
	public void GetPriceAll(String kindid,String userID)
	{
		toDoSomeThing = GET_PRICE;
		String url=GET_PROPPRICE_ADDRESS +"input="+"userid="+userID+"&kindid=" + kindid;
		System.out.println(url);
		encryptedstr = GET_PROPPRICE_ADDRESS +"input="+ new Conn().enZnCode("userid="+userID+"&kindid=" + kindid);
		System.out.println("获得道具价格---->"+encryptedstr);
		new Thread(this).start();
	}

	/**
	 * @param url 具
	 * @param accountStb itvID
	 * @param productCode 产品码
	 * @param propsCode 道具码
	 */
	public void BuyProp(String url, String accountStb, String productCode, String propsCode,String UserToken)
	{

		//accountStb=1511120&productCode=P31011&propsCode=DJ32005
		toDoSomeThing = BUY_PROP;
		encryptedstr = url + "input="+new Conn().enZnCode("userid=" + accountStb + "&UserToken="+UserToken+"&kindid=" + productCode + "&propid=" + propsCode);
		new Thread(this).start();
	}

	public void run()
	{
		// TODO Auto-generated method stub
		try
		{
			switch (toDoSomeThing)
			{
				case GET_PRICE:
					lastResult = new String(getViaHttpConnection(encryptedstr));
					read_listener.ZJ_PROP_ReadServer(lastResult, GET_PRICE);
					lastResult = null;
					encryptedstr = null;
					break;
				case BUY_PROP:
					lastResult = new String(getViaHttpConnection(encryptedstr));
					System.out.println(lastResult);
					read_listener.ZJ_PROP_ReadServer(lastResult, BUY_PROP);
					break;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

	}

	private byte[] getViaHttpConnection(String url) throws IOException
	{
		HttpConnection httpc = null;
		InputStream is = null;
		int rc;
		byte[] data=null;
		try
		{
			httpc = (HttpConnection) Connector.open(url);
//			httpc.setRequestMethod(HttpConnection.GET);//get连接
			rc = httpc.getResponseCode();
				if (rc != HttpConnection.HTTP_OK)
				{
					throw new IOException("HTTP response code: " + rc);
				}
			is = httpc.openInputStream();
//			String type = httpc.getType();
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
