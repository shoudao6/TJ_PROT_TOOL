import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import com.zj.zn.prop.TJ_IPTV_PORP_TOOL;
import com.zj.zn.prop.TJ_IPTV_listener;
import com.zj.zn.prop.TJ_IPTV_log;

public class MMcanvas extends Canvas implements TJ_IPTV_listener
{
	public final static int	KEY_UP		= -1;

	public final static int	KEY_DOWN	= -2;

	public final static int	KEY_LEFT	= -3;

	public final static int	KEY_RIGHT	= -4;

	public final static int	KEY_FIRE	= -5;

	TJ_IPTV_PORP_TOOL		zj			= null;

	Image					img;

	Image					img1;

	public MMcanvas(MIDlet m)
	{
		// TODO Auto-generated constructor stub
		zj = new TJ_IPTV_PORP_TOOL(m, this, "P10266", false,"157210495599");
		setFullScreenMode(true);
		try
		{
			img = Image.createImage("/test/pabout.png");
			img1 = Image.createImage("/test/pname.png");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    new TJ_IPTV_log().stratGame("11111", "111111111", "0");
	}

	protected void paint(Graphics g)
	{
		// TODO Auto-generated method stub
		g.setColor(0);
		g.fillRect(0, 0, 640, 530);

	}

	protected void keyPressed(int keyCode)
	{
		// TODO Auto-generated method stub
		super.keyPressed(keyCode);
		switch (keyCode)
		{
			case KEY_UP:
				zj.do_BuyProp(img1, img, "DJ11995", 10);

				break;
			case KEY_DOWN:
				zj.do_BuyProp(img1, img, "DJ11995", 10,3);
				break;
			case KEY_RIGHT:
				zj.do_BuyShowTop3();
				break;
			case KEY_FIRE:
				break;
			case KEY_LEFT:
				break;
		}

		this.repaint();
	}

	public void TJ_PROP_listener(int RetnValue, String PropCodes, int custom)
	{
		// TODO Auto-generated method stub
		System.out.println(PropCodes);
		switch (RetnValue)
		{

			case TJ_IPTV_PORP_TOOL.ZJ_BUY_SCUCCESS:
				if (PropCodes.equals("DJ32005"))//DJ32005 ��Ѫֵ�ĵ�����
				{
					//����Ѫֵ�ɹ�
				}
				else if (PropCodes.equals("DJ32006"))//DJ32006 ����ɱ�ĵ�����
				{
					//������ɱ�ɹ�
				}

				break;
			//������� �������ʧ�ܴ���
				//艹
			case TJ_IPTV_PORP_TOOL.ZJ_BUY_LOSE://����ʧ�� 
				break;
			case TJ_IPTV_PORP_TOOL.ZJ_BUY_TOP://�ⶥ
				break;
			case TJ_IPTV_PORP_TOOL.ZJ_BUY_ORDER://���ܹ���
				break;
			case TJ_IPTV_PORP_TOOL.ZJ_BUY_ESC:
				System.out.println("ȡ��");
				break;
			default:
				break;
		}
	
	}

}
