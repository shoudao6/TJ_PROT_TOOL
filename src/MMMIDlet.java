import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.zj.zn.prop.TJ_IPTV_PORP_TOOL;
import com.zj.zn.prop.TJ_IPTV_listener;

public class MMMIDlet extends MIDlet
{
	//ZJ_IPTV_PORP_TOOL	prop	= new ZJ_IPTV_PORP_TOOL();
	MMcanvas	mc;

	public MMMIDlet()
	{
		// TODO Auto-generated constructor stub
		mc = new MMcanvas(this);
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException
	{
		// TODO Auto-generated method stub

	}

	protected void pauseApp()
	{
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException
	{
		// TODO Auto-generated method stub

		Display.getDisplay(this).setCurrent(mc);

	}

}
