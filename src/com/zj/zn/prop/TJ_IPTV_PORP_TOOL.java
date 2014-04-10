package com.zj.zn.prop;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

public class TJ_IPTV_PORP_TOOL extends Canvas implements READ_listener {
	private final static int KEY_UP = -1;

	private final static int KEY_DOWN = -2;

	private final static int KEY_LEFT = -3;

	private final static int KEY_RIGHT = -4;

	private final static int KEY_FIRE = -5;

	private Image zj_back = null;

	private Image zj_num = null;

	private Image zj_button = null;

	private Image zj_button_esc = null;

	private Image zj_button_fire = null;

	private Image zj_button_rect = null;

	private Image zj_buy_lose = null;

	private Image zj_buy_success = null;

	private Image zj_cue = null;

	private Image zj_NoPermission = null;

	private Image zj_str_buytop3 = null;

	private Image zj_yuan = null;

	private Image zj_str_buyTop = null;

	private Image zj_timer = null; // 这个是数字 //时间图标

	private int CanvasHeight;

	private int CanvasWidth;

	private CPoint cPoint = null;

	private int MButton = 0;

	private final int SELECT_FIRE = -1;

	private final int SELECT_ESC = 1;

	// 业务名
	private Image PropName = null;

	// 详情
	private Image PropAbout = null;

	private JSONArray priceJson = null;

	public final String RETN_PROP_SUCCESS = "1212200"; // 成功

	private final String RETN_PROP_ORDER = "1212207"; // 不允许订购

	private final String RETN_PROP_TOP3 = "9999999"; // 连续购买是3次

	private final String RETN_PROP_TOP = "1007"; // 封顶

	// 状态
	private final byte ZJ_PROP_STATE_BUY = 0;

	private final byte ZJ_PROP_STATE_SHOWSUCCESS = 1;

	private final byte ZJ_PROP_STATE_SHOWLOSE = 2;

	private final byte ZJ_PROP_STATE_SHOWORDER = 3;

	private final byte ZJ_PROP_STATE_SHOWTOP3 = 4;

	private final byte ZJ_PROP_STATE_SHOWTOP = 5;

	private byte ZJ_NOW_PROP_STATE = 0;

	private MIDlet myMidlet = null;

	private Displayable myDisplayable = null;

	private byte Create_Button_Stop = 0;

	private TJ_IPTV_listener zj_iptv_listener = null;

	public static final byte ZJ_BUY_SCUCCESS = 1;

	public static final byte ZJ_BUY_LOSE = 2;

	public static final byte ZJ_BUY_ORDER = 3;

	private static final byte ZJ_BUY_TOP3 = 4;

	public static final byte ZJ_BUY_TOP = 5;

	public static final byte ZJ_BUY_ESC = 6;

	public static final byte TJ_SECONDCONFIRM = 7;

	private String PropPrice;

	private boolean isTest = false;

	private String BuyPropAdress = "http://202.99.114.17:8080/www.iptvtest.com/order.jumpurl.php?";

	private final String Version = "TJ_1.0.2";

	/**
	 * 保存产品码
	 */
	private String KindID;

	/**
	 * 接收保存道具码
	 */
	private String PropCode;

	private String userItv;

	private int delayTimer = 3000; // 设置对话框显示时间

	private int nkeyLockTimer = -1;
	int count;

	private String UserToken;

	/**
	 * 
	 * @param midlet
	 * @param zj_listenner
	 *            当前需要监听的类
	 * @param kindid
	 *            产品id
	 * @param istest
	 *            是否测试状态
	 * @param userid
	 *            用户id
	 */
	public TJ_IPTV_PORP_TOOL(MIDlet midlet, TJ_IPTV_listener zj_listenner,
			String kindid, boolean istest, String userid) {
		myMidlet = midlet;
		// userItv = myMidlet.getAppProperty("userId");
		userItv = userid;
		if (userItv == null) {
			userItv = "0571000441";
		}
		String tAddress = myMidlet.getAppProperty("url");
		UserToken = myMidlet.getAppProperty("UserToken");
		System.out.println("UserToken=" + UserToken);
		if (tAddress != null) {
			BuyPropAdress = tAddress;// 获取到
		}
		String priceUrl=myMidlet.getAppProperty("priceUrl");
		if(priceUrl==null){
			TJ_IPTV_Server.GET_PROPPRICE_ADDRESS="http://202.99.114.17:8080/www.iptvtest.com/iptv.props.get.php?";
		}else{
			TJ_IPTV_Server.GET_PROPPRICE_ADDRESS=priceUrl;
		}
		System.out.println("<----------------------priceUrl="+TJ_IPTV_Server.GET_PROPPRICE_ADDRESS);

		zj_iptv_listener = zj_listenner;
		new TJ_IPTV_Server(this).GetPriceAll(kindid, userItv);
		count = 0;
//		while (priceJson == null) {
//			if (count > 5000) {
//				break;
//			} else {
				count++;
				System.out.println("count=" + count);
//			}
//		}
		this.KindID = kindid;
		this.isTest = istest;
		setFullScreenMode(true);
		setMButton(SELECT_FIRE);
		CanvasHeight = this.getHeight();
		CanvasWidth = this.getWidth();
		cPoint = new CPoint(CanvasWidth / 2, CanvasHeight / 2);
	}

	int Custom;

	/**
	 * @param propName道具名称
	 *            图片对象
	 * @param propAbout道具说明
	 *            图片对象
	 * @param propCode道具码
	 * @param custom
	 *            自定义参数 该参数传什么 接口 的自定义参数返回什么
	 */
	public void do_BuyProp(Image propName, Image propAbout, String propCode,
			int custom) {
		bKeyLock = false;
		secondConfirm = false;
		nkeyLockTimer = -1;
		this.PropName = propName;
		this.PropAbout = propAbout;
		PropPrice = GetPropPrice(propCode);
		this.PropCode = propCode;
		if (getCreate_Button_Stop() == 0) {
			setMButton(SELECT_FIRE); // 设置默认按钮在确认上
		} else if (getCreate_Button_Stop() == 1) {
			setMButton(SELECT_ESC); // 设置默认按钮在确认上
		}
		setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_BUY);
		if (myDisplayable == null) {
			myDisplayable = Display.getDisplay(myMidlet).getCurrent();
		}
		Custom = custom;
		Display.getDisplay(myMidlet).setCurrent(this);

	}

	/**
	 * @param propName道具名称
	 *            图片对象
	 * @param propAbout道具说明
	 *            图片对象
	 * @param propCode道具码
	 * @param custom
	 *            自定义参数 该参数传什么 接口 的自定义参数返回什么
	 * @param keyLockTimer
	 *            键盘锁住时间设置 单位秒
	 */

	public void do_BuyProp(Image propName, Image propAbout, String propCode,
			int custom, int keyLockTimer) {
		lockKey(keyLockTimer);
		this.PropName = propName;
		this.PropAbout = propAbout;
		PropPrice = GetPropPrice(propCode);
		this.PropCode = propCode;
		if (getCreate_Button_Stop() == 0) {
			setMButton(SELECT_FIRE); // 设置默认按钮在确认上
		} else if (getCreate_Button_Stop() == 1) {
			setMButton(SELECT_ESC); // 设置默认按钮在确认上
		}
		setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_BUY);
		if (myDisplayable == null) {
			myDisplayable = Display.getDisplay(myMidlet).getCurrent();
		}
		Custom = custom;
		Display.getDisplay(myMidlet).setCurrent(this);

	}

	public void do_BuyShowTop3() {
		setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWTOP3);
		if (myDisplayable == null) {
			myDisplayable = Display.getDisplay(myMidlet).getCurrent();
		}
		Display.getDisplay(myMidlet).setCurrent(this);
		jmpUser(false);
	}

	private Timer tKeyTimer = null;

	private boolean bKeyLock = false;

	private boolean secondConfirm;

	public static String BUY_RESULT;



	/**
	 * @param isCallInterface
	 *            返回时是否需要调用zj_iptv_listener接口
	 */
	private void lockKey(int nltimer/* 倒计时时间 单位秒 */) {
		nkeyLockTimer = nltimer;
		bKeyLock = true;
		if (tKeyTimer == null) {
			tKeyTimer = new Timer();
			tKeyTimer.schedule(new TimerTask() {

				public void run() {

					if (--nkeyLockTimer < 0) {
						bKeyLock = false;
						tKeyTimer.cancel();
						tKeyTimer = null;
					}
					repaint();
				}
			}, 0, 1000);
		}

	}

	// 显示时间倒计时
	private void showTimer(Graphics g) {
		if (zj_timer == null)// 时间数字图片
		{
			try {
				zj_timer = Image.createImage("/zj_prop/zj_timer.png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (nkeyLockTimer >= 0) {
			drawnumber(zj_timer, g, nkeyLockTimer, cPoint.x - 43,
					cPoint.y + 66, 11, 17);
		}

	}

	/**
	 * @param img
	 *            数字图片对象
	 * @param g
	 *            画笔
	 * @param number
	 *            数字
	 * @param x
	 *            坐标X
	 * @param y
	 *            坐标Y
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 */
	private void drawnumber(Image img, Graphics g, int number, int x, int y,
			int w, int h) {
		int sum = 10;//
		String sb;
		sb = String.valueOf(number);
		String strstem;
		int intemp;
		for (int i = 0; i < sb.length(); i++) {
			strstem = String.valueOf(sb.charAt(i));// 依次取出字符
			intemp = Integer.parseInt(strstem);
			g.drawRegion(img, intemp * w, 0, w, h, 0, x + i * w, y, 0);

		}
	}

	protected void paint(Graphics g) {

		loadImg();
		switch (ZJ_NOW_PROP_STATE) {

		case ZJ_PROP_STATE_BUY:
			showBuyProp(g);
			showTimer(g);
			break;
		case ZJ_PROP_STATE_SHOWLOSE:
			showCueLose(g);
			break;
		case ZJ_PROP_STATE_SHOWORDER:
			showCueShoworder(g);
			break;
		case ZJ_PROP_STATE_SHOWSUCCESS:
			showCueSuccess(g);
			break;
		case ZJ_PROP_STATE_SHOWTOP:
			showCueTop(g);
			break;
		case ZJ_PROP_STATE_SHOWTOP3:
			showCueTop3(g);
			break;
		case TJ_SECONDCONFIRM:
			showSecondConfirm(g);
			break;

		}

	}

	// 图片检测
	private void loadImg() {
		try {

			switch (ZJ_NOW_PROP_STATE) {

			case ZJ_PROP_STATE_BUY:
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_back.png");

					zj_num = Image.createImage("/zj_prop/zj_num.png");
					zj_button_fire = Image
							.createImage("/zj_prop/zj_button_fire.png");
					zj_button_esc = Image
							.createImage("/zj_prop/zj_button_esc.png");
					zj_button = Image.createImage("/zj_prop/zj_button.png");
					zj_button_rect = Image
							.createImage("/zj_prop/zj_button_rect.png");
					zj_yuan = Image.createImage("/zj_prop/wjb.png");

				}

				break;
			case TJ_SECONDCONFIRM:
				if(zj_back==null){
					zj_back=Image.createImage("/zj_prop/secondConfirm.png");
					zj_num = Image.createImage("/zj_prop/zj_num.png");
					zj_button_fire = Image
							.createImage("/zj_prop/zj_button_fire.png");
					zj_button_esc = Image
							.createImage("/zj_prop/zj_button_esc.png");
					zj_button = Image.createImage("/zj_prop/zj_button.png");
					zj_button_rect = Image
							.createImage("/zj_prop/zj_button_rect.png");
					zj_yuan = Image.createImage("/zj_prop/zj_yuan.png");
				}
				
				break;
			case ZJ_PROP_STATE_SHOWLOSE:// 失败
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_tips.png");
					zj_buy_lose = Image.createImage("/zj_prop/zj_buy_lose.png");
				}

				break;
			case ZJ_PROP_STATE_SHOWORDER:// 不能购买?
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_tips.png");
					zj_NoPermission = Image
							.createImage("/zj_prop/zj_NoPermission.png");

				}

				break;
			case ZJ_PROP_STATE_SHOWSUCCESS:// 成功
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_tips.png");
					zj_buy_success = Image
							.createImage("/zj_prop/zj_buy_success.png");

				}
				break;
			case ZJ_PROP_STATE_SHOWTOP:// 封顶
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_tips.png");
					zj_str_buyTop = Image
							.createImage("/zj_prop/zj_str_buyTop.png");
				}
				break;
			case ZJ_PROP_STATE_SHOWTOP3:// 连续购买是三次
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_tips.png");
					zj_str_buytop3 = Image
							.createImage("/zj_prop/zj_str_buytop3.png");
				}
				break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 购买失败
	private void showCueLose(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_buy_lose, cPoint.x, cPoint.y, 3);
		g.setColor(0);
		g.drawString(BUY_RESULT, 245, 200, 0);
	}

	// 购买成功
	private void showCueSuccess(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_buy_success, cPoint.x, cPoint.y, 3);
	}

	// 不允许订购?
	private void showCueShoworder(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_NoPermission, cPoint.x, cPoint.y, 3);
	}

	// 封顶
	private void showCueTop(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_str_buyTop, cPoint.x, cPoint.y, 3);
	}

	// 连续购买三次
	private void showCueTop3(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_str_buytop3, cPoint.x, cPoint.y, 3);
	}

	// 这里是购买道具
	private void showBuyProp(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(PropName, cPoint.x-70, cPoint.y - 85, 0);
		g.drawImage(PropAbout, cPoint.x - 80, cPoint.y - 14, 0);
		int price=Integer.parseInt(PropPrice)*10;
		drawYPrice(zj_num, zj_yuan, g, price+"", cPoint.x - 70, cPoint.y - 45,
				10, 15);// 显示价格
		ShowButton(g);

	}
	
	private void showSecondConfirm(Graphics g){
		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(PropName, cPoint.x-70, cPoint.y - 65, 0);
		drawYPrice(zj_num, zj_yuan, g,PropPrice, cPoint.x - 70, cPoint.y - 25,
				10, 15);// 显示价格
		ShowButton(g);
	}

	// 320 265
	private void ShowButton(Graphics g) {
		g.drawImage(zj_button, cPoint.x - 70, cPoint.y + 75, 3);
		g.drawImage(zj_button, cPoint.x + 70, cPoint.y + 75, 3);
		g.drawImage(zj_button_fire, cPoint.x - 70, cPoint.y + 75, 3);
		g.drawImage(zj_button_esc, cPoint.x + 70, cPoint.y + 75, 3);
		g.drawImage(zj_button_rect, cPoint.x + 70 * getMButton(),
				cPoint.y + 75, 3);// 显示 选中按钮
	}

	/**
	 * @param img
	 *            数字
	 * @param yimg
	 *            元
	 * @param g
	 * @param yuan
	 *            价格
	 * @param x
	 *            坐标
	 * @param y
	 * @param w
	 * @param h
	 */
	private void drawYPrice(Image img, Image yimg, Graphics g, String yuan,
			int x, int y, int w, int h) {

		String sb;
		sb = yuan.toString();
		int intemp;
		int len = sb.length();
		for (int i = 0; i < len; i++) {

			intemp = sb.charAt(i);// ascii

			if (intemp >= 48 && intemp <= 57)// 0-9
			{
				g.drawRegion(img, (intemp - 48) * w, 0, w, h, 0, x + i * w, y,
						0);
			} else if (intemp == 46)// .号
			{
				g.drawRegion(img, 10 * w, 0, w, h, 0, x + i * w, y, 0);
			}

		}
		g.drawImage(yimg, x + (len + 1) * w, y - 3, 0);

	}

	protected void setStopInputKey() {

		ZJ_NOW_PROP_STATE = 111;
	}

	// 是否需要停在确认上
	public byte getCreate_Button_Stop() {
		return Create_Button_Stop;
	}

	// 设置停止按钮默认停在
	public void setCreate_Button_Stop(byte create_button_stop_fire) {
		Create_Button_Stop = create_button_stop_fire;
	}

	/**
	 * @param timer
	 *            设置对话框显示时间 以毫秒为单位
	 */
	public void setDelayTimer(int timer) {
		this.delayTimer = timer;
	}

	protected void keyPressed(int keyCode) {

		super.keyPressed(keyCode);
		switch (keyCode) {
		case KEY_UP:
		case KEY_DOWN:
		case KEY_LEFT:
		case KEY_RIGHT:
			if (getZJ_NOW_PROP_STATE() == ZJ_PROP_STATE_BUY || getZJ_NOW_PROP_STATE()==TJ_SECONDCONFIRM) {
				moveButton();
			}
			break;
		case KEY_FIRE:
			System.out.println("111111111111");
			if (getZJ_NOW_PROP_STATE() == ZJ_PROP_STATE_BUY) {
				if (getMButton() == -1) {
					if (isTest) // 测试状态
					{
						if (!bKeyLock) {
							ZJ_PROP_ReadServer("istest",
									TJ_IPTV_Server.BUY_PROP_ISTEST);
						}
					} else {
							setZJ_NOW_PROP_STATE(TJ_SECONDCONFIRM);
							setMButton(SELECT_ESC);
					}

				} else {
					callEsc();
				}

			}else if(getZJ_NOW_PROP_STATE()==TJ_SECONDCONFIRM){
				System.out.println("222222222222");
				if (getMButton() == -1) {
					if (!bKeyLock) {
						setStopInputKey();
						new TJ_IPTV_Server(this).BuyProp(BuyPropAdress,
								this.userItv, this.KindID,
								this.PropCode, UserToken);
						System.out.println("3333333333");
					}
				}else{
					callEsc();
				}
			}

			break;

		}
		this.repaint();
	}

	// 移动按钮
	private void moveButton() {

		setMButton(MButton == SELECT_FIRE ? SELECT_ESC : SELECT_FIRE);
	}

	private int getMButton() {
		return MButton;
	}

	/**
	 * @param button
	 *            设置按钮默认位置 -1 在确认 1 在取消
	 */
	private void setMButton(int button) {
		MButton = button;
	}

	private byte getZJ_NOW_PROP_STATE() {
		return ZJ_NOW_PROP_STATE;
	}

	public void setZJ_NOW_PROP_STATE(byte zj_now_prop_state) {
		ZJ_NOW_PROP_STATE = zj_now_prop_state;
		removeImg0();
		repaint();
	}

	private void removeImg0() {
		zj_back = null;

		zj_num = null;

		zj_button = null;

		zj_button_esc = null;

		zj_button_fire = null;

		zj_button_rect = null;

		zj_buy_lose = null;

		zj_buy_success = null;

		zj_cue = null;

		zj_NoPermission = null;

		zj_str_buytop3 = null;

		zj_yuan = null;

		zj_str_buyTop = null;

		zj_timer = null;
		

		System.gc();
	}

	private void removeImg() {

		zj_back = null;

		zj_num = null;

		zj_button = null;

		zj_button_esc = null;

		zj_button_fire = null;

		zj_button_rect = null;

		zj_buy_lose = null;

		zj_buy_success = null;

		zj_cue = null;

		zj_NoPermission = null;

		zj_str_buytop3 = null;

		zj_yuan = null;

		zj_str_buyTop = null;

		PropName = null;

		PropAbout = null;

		zj_timer = null;
		System.gc();

	}

	class CPoint {
		int x; // 居中 X

		int y; // 居中Y

		public CPoint(int cx, int cy) {
			// TODO Auto-generated constructor stub
			this.x = cx;
			this.y = cy;
		}

	}

	/**
	 * @param propCode
	 *            通过道具码获取价格 成功返回价格 失败返回1
	 * @return
	 */
	public String GetPropPrice(String propCode) {
//		if (priceJson == null) {
//			System.out.println("<-----------------暂时未获取到价格");
//			return "3";
//		}
		while(priceJson==null){
			
		}
		JSONObject jObject = null;
		String temp = null;
		String tPrice = null;

		try {
			for (int i = 0; i < priceJson.length(); i++) {
				jObject = priceJson.getJSONObject(i);
				temp = (jObject.getString("Proid"));
				if (temp.equals(propCode)) {
					tPrice = (jObject.getString("price"));
//					System.out.println("<-----------------price="+tPrice);
					return tPrice;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		System.out.println("道具码输入错误?未检测到你输入的道具码");
		return "0";
	}

	/**
	 * @param propCode
	 *            这个是元宝?
	 * @return
	 */
	public String GetPropGamegolds(String propCode) {
		if (priceJson == null) {
			System.out.println("暂时未获取到价格");
			return "0";
		}
		JSONObject jObject = null;
		String temp = null;
		String tPrice = null;

		try {
			for (int i = 0; i < priceJson.length(); i++) {
				jObject = priceJson.getJSONObject(i);
				temp = (jObject.getString("Proid"));
				if (temp.equals(propCode)) {
					tPrice = (jObject.getString("gamegolds"));
					return tPrice;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		System.out.println("道具码输入错误?未检测到你输入的道具码");
		return "0";
	}

	public String getVersion() {
		return Version;
	}

	// RetnValue, BuyType, PropCodes
	private int RetnValue, BuyType;

	private String PropCodes;

	public void ZJ_PROP_ReadServer(String str, int retnType) {
		// TODO Auto-generated method stub
		switch (retnType) {
		case TJ_IPTV_Server.GET_PRICE: // 获取单价开始
			try {
				priceJson = new JSONObject(str).getJSONArray("res");
//				if (isTest) {
					System.out.println(priceJson);
//				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case TJ_IPTV_Server.BUY_PROP:// 购买返回
			// String hert = getHret(str);
			// String propcode = getCode(str);
			// PropCodes = propcode;
			BUY_RESULT=str;

			if (str.equals(RETN_PROP_SUCCESS)) {// 成功
				setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWSUCCESS);
				RetnValue = ZJ_BUY_SCUCCESS;
				new TJ_IPTV_log().propLog(this.userItv, PropCodes, this.KindID);

			} else if (str.equals(RETN_PROP_ORDER)) {// 该组不能购买
				setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWORDER);
				RetnValue = ZJ_BUY_ORDER;
			} else if (str.equals(RETN_PROP_TOP)) {// 封顶
				setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWTOP);
				RetnValue = ZJ_BUY_TOP;
			} else {
				setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWLOSE);
				RetnValue = ZJ_BUY_LOSE;
				System.out.println("购买失败");
				// 购买失败
			}
			jmpUser(true);
			break;
		case TJ_IPTV_Server.BUY_PROP_ISTEST:// 测试状态直接成功
			// System.out.println("当前处于测试状态");
			setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWSUCCESS);
			RetnValue = ZJ_BUY_SCUCCESS;
			PropCodes = this.PropCode;

			jmpUser(true);
			break;
		}

	}

	// 用户选择 了取消
	private void callEsc() {
		setStopInputKey();
		removeImg();
		zj_iptv_listener.TJ_PROP_listener(ZJ_BUY_ESC, PropCode, Custom);// 取消
		Display.getDisplay(myMidlet).setCurrent(myDisplayable);
	}

	private Timer timeHand;

	/**
	 * @param isCallInterface
	 *            返回时是否需要调用zj_iptv_listener接口
	 */
	private void jmpUser(final boolean isCallInterface) {
		if (timeHand == null) {
			timeHand = new Timer();
			timeHand.schedule(new TimerTask() {

				public void run() {
					// 跳回到原来的
					removeImg();
					timeHand.cancel();
					timeHand = null;
					Display.getDisplay(myMidlet).setCurrent(myDisplayable);
					if (isCallInterface) {
						zj_iptv_listener.TJ_PROP_listener(RetnValue, PropCodes,
								Custom);
					}

				}
			}, delayTimer);
		}

	}

	// 获取 购买返回之后的返回值
	private String getHret(String s) {
		String str;
		int beginIndex;
		int endIndex;
		int headLength;
		beginIndex = s.indexOf("<hret>");
		endIndex = s.indexOf("</hret>");
		headLength = "<hret>".length();// 加上 这个 是为了减去 原本的长度
		str = retnString(s, beginIndex, headLength, endIndex);
		return str;
	}

	private String getCode(String s) {
		String str;
		int beginIndex;
		int endIndex;
		int headLength;
		beginIndex = s.indexOf("<propsCode>");
		endIndex = s.indexOf("</propsCode>");
		headLength = "<propsCode>".length();// 加上 这个 是为了减去 原本的长度
		str = retnString(s, beginIndex, headLength, endIndex);
		return str;
	}

	private String retnString(String s, int beginIndex, int headLength,
			int endIndex) {
		return s.substring(beginIndex + headLength, endIndex);
	}

}
