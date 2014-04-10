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

	private Image zj_timer = null; // ��������� //ʱ��ͼ��

	private int CanvasHeight;

	private int CanvasWidth;

	private CPoint cPoint = null;

	private int MButton = 0;

	private final int SELECT_FIRE = -1;

	private final int SELECT_ESC = 1;

	// ҵ����
	private Image PropName = null;

	// ����
	private Image PropAbout = null;

	private JSONArray priceJson = null;

	public final String RETN_PROP_SUCCESS = "1212200"; // �ɹ�

	private final String RETN_PROP_ORDER = "1212207"; // ��������

	private final String RETN_PROP_TOP3 = "9999999"; // ����������3��

	private final String RETN_PROP_TOP = "1007"; // �ⶥ

	// ״̬
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
	 * �����Ʒ��
	 */
	private String KindID;

	/**
	 * ���ձ��������
	 */
	private String PropCode;

	private String userItv;

	private int delayTimer = 3000; // ���öԻ�����ʾʱ��

	private int nkeyLockTimer = -1;
	int count;

	private String UserToken;

	/**
	 * 
	 * @param midlet
	 * @param zj_listenner
	 *            ��ǰ��Ҫ��������
	 * @param kindid
	 *            ��Ʒid
	 * @param istest
	 *            �Ƿ����״̬
	 * @param userid
	 *            �û�id
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
			BuyPropAdress = tAddress;// ��ȡ��
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
	 * @param propName��������
	 *            ͼƬ����
	 * @param propAbout����˵��
	 *            ͼƬ����
	 * @param propCode������
	 * @param custom
	 *            �Զ������ �ò�����ʲô �ӿ� ���Զ����������ʲô
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
			setMButton(SELECT_FIRE); // ����Ĭ�ϰ�ť��ȷ����
		} else if (getCreate_Button_Stop() == 1) {
			setMButton(SELECT_ESC); // ����Ĭ�ϰ�ť��ȷ����
		}
		setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_BUY);
		if (myDisplayable == null) {
			myDisplayable = Display.getDisplay(myMidlet).getCurrent();
		}
		Custom = custom;
		Display.getDisplay(myMidlet).setCurrent(this);

	}

	/**
	 * @param propName��������
	 *            ͼƬ����
	 * @param propAbout����˵��
	 *            ͼƬ����
	 * @param propCode������
	 * @param custom
	 *            �Զ������ �ò�����ʲô �ӿ� ���Զ����������ʲô
	 * @param keyLockTimer
	 *            ������סʱ������ ��λ��
	 */

	public void do_BuyProp(Image propName, Image propAbout, String propCode,
			int custom, int keyLockTimer) {
		lockKey(keyLockTimer);
		this.PropName = propName;
		this.PropAbout = propAbout;
		PropPrice = GetPropPrice(propCode);
		this.PropCode = propCode;
		if (getCreate_Button_Stop() == 0) {
			setMButton(SELECT_FIRE); // ����Ĭ�ϰ�ť��ȷ����
		} else if (getCreate_Button_Stop() == 1) {
			setMButton(SELECT_ESC); // ����Ĭ�ϰ�ť��ȷ����
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
	 *            ����ʱ�Ƿ���Ҫ����zj_iptv_listener�ӿ�
	 */
	private void lockKey(int nltimer/* ����ʱʱ�� ��λ�� */) {
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

	// ��ʾʱ�䵹��ʱ
	private void showTimer(Graphics g) {
		if (zj_timer == null)// ʱ������ͼƬ
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
	 *            ����ͼƬ����
	 * @param g
	 *            ����
	 * @param number
	 *            ����
	 * @param x
	 *            ����X
	 * @param y
	 *            ����Y
	 * @param w
	 *            ���
	 * @param h
	 *            �߶�
	 */
	private void drawnumber(Image img, Graphics g, int number, int x, int y,
			int w, int h) {
		int sum = 10;//
		String sb;
		sb = String.valueOf(number);
		String strstem;
		int intemp;
		for (int i = 0; i < sb.length(); i++) {
			strstem = String.valueOf(sb.charAt(i));// ����ȡ���ַ�
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

	// ͼƬ���
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
			case ZJ_PROP_STATE_SHOWLOSE:// ʧ��
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_tips.png");
					zj_buy_lose = Image.createImage("/zj_prop/zj_buy_lose.png");
				}

				break;
			case ZJ_PROP_STATE_SHOWORDER:// ���ܹ���?
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_tips.png");
					zj_NoPermission = Image
							.createImage("/zj_prop/zj_NoPermission.png");

				}

				break;
			case ZJ_PROP_STATE_SHOWSUCCESS:// �ɹ�
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_tips.png");
					zj_buy_success = Image
							.createImage("/zj_prop/zj_buy_success.png");

				}
				break;
			case ZJ_PROP_STATE_SHOWTOP:// �ⶥ
				if (zj_back == null) {
					zj_back = Image.createImage("/zj_prop/zj_tips.png");
					zj_str_buyTop = Image
							.createImage("/zj_prop/zj_str_buyTop.png");
				}
				break;
			case ZJ_PROP_STATE_SHOWTOP3:// ��������������
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

	// ����ʧ��
	private void showCueLose(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_buy_lose, cPoint.x, cPoint.y, 3);
		g.setColor(0);
		g.drawString(BUY_RESULT, 245, 200, 0);
	}

	// ����ɹ�
	private void showCueSuccess(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_buy_success, cPoint.x, cPoint.y, 3);
	}

	// ��������?
	private void showCueShoworder(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_NoPermission, cPoint.x, cPoint.y, 3);
	}

	// �ⶥ
	private void showCueTop(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_str_buyTop, cPoint.x, cPoint.y, 3);
	}

	// ������������
	private void showCueTop3(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(zj_str_buytop3, cPoint.x, cPoint.y, 3);
	}

	// �����ǹ������
	private void showBuyProp(Graphics g) {

		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(PropName, cPoint.x-70, cPoint.y - 85, 0);
		g.drawImage(PropAbout, cPoint.x - 80, cPoint.y - 14, 0);
		int price=Integer.parseInt(PropPrice)*10;
		drawYPrice(zj_num, zj_yuan, g, price+"", cPoint.x - 70, cPoint.y - 45,
				10, 15);// ��ʾ�۸�
		ShowButton(g);

	}
	
	private void showSecondConfirm(Graphics g){
		g.drawImage(zj_back, cPoint.x, cPoint.y, 3);
		g.drawImage(PropName, cPoint.x-70, cPoint.y - 65, 0);
		drawYPrice(zj_num, zj_yuan, g,PropPrice, cPoint.x - 70, cPoint.y - 25,
				10, 15);// ��ʾ�۸�
		ShowButton(g);
	}

	// 320 265
	private void ShowButton(Graphics g) {
		g.drawImage(zj_button, cPoint.x - 70, cPoint.y + 75, 3);
		g.drawImage(zj_button, cPoint.x + 70, cPoint.y + 75, 3);
		g.drawImage(zj_button_fire, cPoint.x - 70, cPoint.y + 75, 3);
		g.drawImage(zj_button_esc, cPoint.x + 70, cPoint.y + 75, 3);
		g.drawImage(zj_button_rect, cPoint.x + 70 * getMButton(),
				cPoint.y + 75, 3);// ��ʾ ѡ�а�ť
	}

	/**
	 * @param img
	 *            ����
	 * @param yimg
	 *            Ԫ
	 * @param g
	 * @param yuan
	 *            �۸�
	 * @param x
	 *            ����
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
			} else if (intemp == 46)// .��
			{
				g.drawRegion(img, 10 * w, 0, w, h, 0, x + i * w, y, 0);
			}

		}
		g.drawImage(yimg, x + (len + 1) * w, y - 3, 0);

	}

	protected void setStopInputKey() {

		ZJ_NOW_PROP_STATE = 111;
	}

	// �Ƿ���Ҫͣ��ȷ����
	public byte getCreate_Button_Stop() {
		return Create_Button_Stop;
	}

	// ����ֹͣ��ťĬ��ͣ��
	public void setCreate_Button_Stop(byte create_button_stop_fire) {
		Create_Button_Stop = create_button_stop_fire;
	}

	/**
	 * @param timer
	 *            ���öԻ�����ʾʱ�� �Ժ���Ϊ��λ
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
					if (isTest) // ����״̬
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

	// �ƶ���ť
	private void moveButton() {

		setMButton(MButton == SELECT_FIRE ? SELECT_ESC : SELECT_FIRE);
	}

	private int getMButton() {
		return MButton;
	}

	/**
	 * @param button
	 *            ���ð�ťĬ��λ�� -1 ��ȷ�� 1 ��ȡ��
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
		int x; // ���� X

		int y; // ����Y

		public CPoint(int cx, int cy) {
			// TODO Auto-generated constructor stub
			this.x = cx;
			this.y = cy;
		}

	}

	/**
	 * @param propCode
	 *            ͨ���������ȡ�۸� �ɹ����ؼ۸� ʧ�ܷ���1
	 * @return
	 */
	public String GetPropPrice(String propCode) {
//		if (priceJson == null) {
//			System.out.println("<-----------------��ʱδ��ȡ���۸�");
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
		System.out.println("�������������?δ��⵽������ĵ�����");
		return "0";
	}

	/**
	 * @param propCode
	 *            �����Ԫ��?
	 * @return
	 */
	public String GetPropGamegolds(String propCode) {
		if (priceJson == null) {
			System.out.println("��ʱδ��ȡ���۸�");
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
		System.out.println("�������������?δ��⵽������ĵ�����");
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
		case TJ_IPTV_Server.GET_PRICE: // ��ȡ���ۿ�ʼ
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

		case TJ_IPTV_Server.BUY_PROP:// ���򷵻�
			// String hert = getHret(str);
			// String propcode = getCode(str);
			// PropCodes = propcode;
			BUY_RESULT=str;

			if (str.equals(RETN_PROP_SUCCESS)) {// �ɹ�
				setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWSUCCESS);
				RetnValue = ZJ_BUY_SCUCCESS;
				new TJ_IPTV_log().propLog(this.userItv, PropCodes, this.KindID);

			} else if (str.equals(RETN_PROP_ORDER)) {// ���鲻�ܹ���
				setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWORDER);
				RetnValue = ZJ_BUY_ORDER;
			} else if (str.equals(RETN_PROP_TOP)) {// �ⶥ
				setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWTOP);
				RetnValue = ZJ_BUY_TOP;
			} else {
				setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWLOSE);
				RetnValue = ZJ_BUY_LOSE;
				System.out.println("����ʧ��");
				// ����ʧ��
			}
			jmpUser(true);
			break;
		case TJ_IPTV_Server.BUY_PROP_ISTEST:// ����״ֱ̬�ӳɹ�
			// System.out.println("��ǰ���ڲ���״̬");
			setZJ_NOW_PROP_STATE(ZJ_PROP_STATE_SHOWSUCCESS);
			RetnValue = ZJ_BUY_SCUCCESS;
			PropCodes = this.PropCode;

			jmpUser(true);
			break;
		}

	}

	// �û�ѡ�� ��ȡ��
	private void callEsc() {
		setStopInputKey();
		removeImg();
		zj_iptv_listener.TJ_PROP_listener(ZJ_BUY_ESC, PropCode, Custom);// ȡ��
		Display.getDisplay(myMidlet).setCurrent(myDisplayable);
	}

	private Timer timeHand;

	/**
	 * @param isCallInterface
	 *            ����ʱ�Ƿ���Ҫ����zj_iptv_listener�ӿ�
	 */
	private void jmpUser(final boolean isCallInterface) {
		if (timeHand == null) {
			timeHand = new Timer();
			timeHand.schedule(new TimerTask() {

				public void run() {
					// ���ص�ԭ����
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

	// ��ȡ ���򷵻�֮��ķ���ֵ
	private String getHret(String s) {
		String str;
		int beginIndex;
		int endIndex;
		int headLength;
		beginIndex = s.indexOf("<hret>");
		endIndex = s.indexOf("</hret>");
		headLength = "<hret>".length();// ���� ��� ��Ϊ�˼�ȥ ԭ���ĳ���
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
		headLength = "<propsCode>".length();// ���� ��� ��Ϊ�˼�ȥ ԭ���ĳ���
		str = retnString(s, beginIndex, headLength, endIndex);
		return str;
	}

	private String retnString(String s, int beginIndex, int headLength,
			int endIndex) {
		return s.substring(beginIndex + headLength, endIndex);
	}

}
