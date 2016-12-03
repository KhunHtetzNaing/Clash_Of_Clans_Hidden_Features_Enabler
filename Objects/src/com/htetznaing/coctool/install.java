package com.htetznaing.coctool;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class install extends Activity implements B4AActivity{
	public static install mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.coctool", "com.htetznaing.coctool.install");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (install).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.htetznaing.coctool", "com.htetznaing.coctool.install");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.coctool.install", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (install) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (install) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return install.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (install) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (install) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _t = null;
public static anywheresoftware.b4a.objects.Timer _t1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b2 = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b1bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b2bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _ibg = null;
public MLfiles.Fileslib.MLfiles _ml = null;
public anywheresoftware.b4a.phone.Phone _p = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _banner = null;
public mobi.mindware.admob.interstitial.AdmobInterstitialsAds _interstitial = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imv = null;
public com.htetznaing.coctool.main _main = null;
public com.htetznaing.coctool.starter _starter = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 23;BA.debugLine="ibg.Initialize(LoadBitmap(File.DirAssets,\"bg.png\"";
mostCurrent._ibg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bg.png").getObject()));
 //BA.debugLineNum = 24;BA.debugLine="imv.Initialize(\"imv\")";
mostCurrent._imv.Initialize(mostCurrent.activityBA,"imv");
 //BA.debugLineNum = 25;BA.debugLine="Activity.AddView(imv,2%x,2%y,96%x,40%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._imv.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (2),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (96),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (40),mostCurrent.activityBA));
 //BA.debugLineNum = 26;BA.debugLine="imv.Background = ibg";
mostCurrent._imv.setBackground((android.graphics.drawable.Drawable)(mostCurrent._ibg.getObject()));
 //BA.debugLineNum = 28;BA.debugLine="If p.SdkVersion > 19 Then";
if (mostCurrent._p.getSdkVersion()>19) { 
 //BA.debugLineNum = 29;BA.debugLine="Banner.Initialize(\"Banner\",\"ca-app-pub-417334857";
mostCurrent._banner.Initialize(mostCurrent.activityBA,"Banner","ca-app-pub-4173348573252986/9546639356");
 //BA.debugLineNum = 30;BA.debugLine="Banner.LoadAd";
mostCurrent._banner.LoadAd();
 //BA.debugLineNum = 31;BA.debugLine="Activity.AddView(Banner,0%x,90%y,100%x,10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._banner.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 33;BA.debugLine="Interstitial.Initialize(\"Interstitial\",\"ca-app-p";
mostCurrent._interstitial.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/3500105755");
 //BA.debugLineNum = 34;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd(mostCurrent.activityBA);
 //BA.debugLineNum = 35;BA.debugLine="t.Initialize(\"t\",500)";
_t.Initialize(processBA,"t",(long) (500));
 //BA.debugLineNum = 36;BA.debugLine="t.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 37;BA.debugLine="t1.Initialize(\"t1\",15000)";
_t1.Initialize(processBA,"t1",(long) (15000));
 //BA.debugLineNum = 38;BA.debugLine="t1.Enabled = True";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 41;BA.debugLine="b1bg.Initialize(LoadBitmap(File.DirAssets,\"instal";
mostCurrent._b1bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"install.png").getObject()));
 //BA.debugLineNum = 42;BA.debugLine="b2bg.Initialize(LoadBitmap(File.DirAssets,\"restor";
mostCurrent._b2bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"restore.png").getObject()));
 //BA.debugLineNum = 44;BA.debugLine="b1.Initialize(\"b1\")";
mostCurrent._b1.Initialize(mostCurrent.activityBA,"b1");
 //BA.debugLineNum = 45;BA.debugLine="b1.Background = b1bg";
mostCurrent._b1.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b1bg.getObject()));
 //BA.debugLineNum = 47;BA.debugLine="b2.Initialize(\"b2\")";
mostCurrent._b2.Initialize(mostCurrent.activityBA,"b2");
 //BA.debugLineNum = 48;BA.debugLine="b2.Background = b2bg";
mostCurrent._b2.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b2bg.getObject()));
 //BA.debugLineNum = 50;BA.debugLine="Activity.AddView(b1,20%x,50%y,60%x,13%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (13),mostCurrent.activityBA));
 //BA.debugLineNum = 51;BA.debugLine="Activity.AddView(b2,20%x,(b1.Top+b1.Height)+5%y,60";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b2.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) ((mostCurrent._b1.getTop()+mostCurrent._b1.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (13),mostCurrent.activityBA));
 //BA.debugLineNum = 53;BA.debugLine="t.Initialize(\"t\",500)";
_t.Initialize(processBA,"t",(long) (500));
 //BA.debugLineNum = 54;BA.debugLine="t.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 55;BA.debugLine="t1.Initialize(\"t1\",15000)";
_t1.Initialize(processBA,"t1",(long) (15000));
 //BA.debugLineNum = 56;BA.debugLine="t1.Enabled = True";
_t1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 57;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 128;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 130;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 124;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 126;BA.debugLine="End Sub";
return "";
}
public static String  _b1_click() throws Exception{
 //BA.debugLineNum = 59;BA.debugLine="Sub b1_Click";
 //BA.debugLineNum = 60;BA.debugLine="If p.SdkVersion > 19 Then";
if (mostCurrent._p.getSdkVersion()>19) { 
 //BA.debugLineNum = 61;BA.debugLine="t.Enabled = True";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 63;BA.debugLine="ml.GetRoot";
mostCurrent._ml.GetRoot();
 //BA.debugLineNum = 64;BA.debugLine="If ml.HaveRoot Then";
if (mostCurrent._ml.HaveRoot) { 
 //BA.debugLineNum = 65;BA.debugLine="If	File.Exists(File.DirRootExternal,\"traps.csv";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"traps.csv")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 66;BA.debugLine="File.Copy(File.DirAssets,\"traps.csv\",File.DirRoot";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"traps.csv",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"traps.csv");
 };
 //BA.debugLineNum = 69;BA.debugLine="If File.Exists(File.DirRootExternal,\"buildings.cs";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"buildings.csv")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 70;BA.debugLine="File.Copy(File.DirAssets,\"buildings.csv\",File.Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"buildings.csv",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"buildings.csv");
 };
 //BA.debugLineNum = 74;BA.debugLine="ml.RootCmd(\"mount -o rw,remount /data\",\"\",Null,Nu";
mostCurrent._ml.RootCmd("mount -o rw,remount /data","",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 75;BA.debugLine="ml.RootCmd(\"cp -r \"&File.DirRootExternal&\"/traps.";
mostCurrent._ml.RootCmd("cp -r "+anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/traps.csv data/data/com.supercell.clashofclans/update/logic/traps.csv","",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 76;BA.debugLine="ml.RootCmd(\"cp -r \"&File.DirRootExternal&\"/buildi";
mostCurrent._ml.RootCmd("cp -r "+anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/buildings.csv data/data/com.supercell.clashofclans/update/logic/buildings.csv","",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 77;BA.debugLine="ml.chmod(\"/data/data/com.supercell.clashofclans/u";
mostCurrent._ml.chmod("/data/data/com.supercell.clashofclans/update/logic/traps.csv",BA.NumberToString(666));
 //BA.debugLineNum = 78;BA.debugLine="ml.chmod(\"/data/data/com.supercell.clashofclans/u";
mostCurrent._ml.chmod("/data/data/com.supercell.clashofclans/update/logic/buildings.csv",BA.NumberToString(666));
 //BA.debugLineNum = 80;BA.debugLine="File.Delete(File.DirRootExternal,\"traps.csv\")";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"traps.csv");
 //BA.debugLineNum = 81;BA.debugLine="File.Delete(File.DirRootExternal,\"buildings.csv\")";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"buildings.csv");
 //BA.debugLineNum = 83;BA.debugLine="Msgbox(\"COC Show Traps and Teslas Installed!\" & C";
anywheresoftware.b4a.keywords.Common.Msgbox("COC Show Traps and Teslas Installed!"+anywheresoftware.b4a.keywords.Common.CRLF+"Now, You can see all Traps and Teslas Before Attack","Completed",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 85;BA.debugLine="Msgbox(\"Your Device is not have Root Access\",\"Att";
anywheresoftware.b4a.keywords.Common.Msgbox("Your Device is not have Root Access","Attention!",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 87;BA.debugLine="End Sub";
return "";
}
public static String  _b2_click() throws Exception{
 //BA.debugLineNum = 89;BA.debugLine="Sub b2_Click";
 //BA.debugLineNum = 90;BA.debugLine="If p.SdkVersion > 19 Then";
if (mostCurrent._p.getSdkVersion()>19) { 
 //BA.debugLineNum = 91;BA.debugLine="t.Enabled = True";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 93;BA.debugLine="ml.GetRoot";
mostCurrent._ml.GetRoot();
 //BA.debugLineNum = 94;BA.debugLine="If ml.HaveRoot Then";
if (mostCurrent._ml.HaveRoot) { 
 //BA.debugLineNum = 95;BA.debugLine="ml.rm(\"data/data/com.supercell.clashofclans/updat";
mostCurrent._ml.rm("data/data/com.supercell.clashofclans/update/logic/traps.csv");
 //BA.debugLineNum = 96;BA.debugLine="ml.rm(\"data/data/com.supercell.clashofclans/updat";
mostCurrent._ml.rm("data/data/com.supercell.clashofclans/update/logic/buildings.csv");
 //BA.debugLineNum = 97;BA.debugLine="Msgbox(\"Original Restore Finished\" & CRLF & \"You";
anywheresoftware.b4a.keywords.Common.Msgbox("Original Restore Finished"+anywheresoftware.b4a.keywords.Common.CRLF+"You can Install again!","Completed!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 99;BA.debugLine="Msgbox(\"Your Device not have Root Access\",\"Attent";
anywheresoftware.b4a.keywords.Common.Msgbox("Your Device not have Root Access","Attention!",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 101;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim b1,b2 As Button";
mostCurrent._b1 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._b2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim b1bg,b2bg,ibg As BitmapDrawable";
mostCurrent._b1bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b2bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._ibg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 15;BA.debugLine="Dim ml As MLfiles";
mostCurrent._ml = new MLfiles.Fileslib.MLfiles();
 //BA.debugLineNum = 16;BA.debugLine="Dim p As Phone";
mostCurrent._p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 17;BA.debugLine="Dim Banner As AdView";
mostCurrent._banner = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim Interstitial As mwAdmobInterstitial";
mostCurrent._interstitial = new mobi.mindware.admob.interstitial.AdmobInterstitialsAds();
 //BA.debugLineNum = 19;BA.debugLine="Dim imv As ImageView";
mostCurrent._imv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim t,t1 As Timer";
_t = new anywheresoftware.b4a.objects.Timer();
_t1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _t_tick() throws Exception{
 //BA.debugLineNum = 103;BA.debugLine="Sub t_Tick";
 //BA.debugLineNum = 104;BA.debugLine="If Interstitial.Status=Interstitial.Status_AdRead";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_AdReadyToShow) { 
 //BA.debugLineNum = 105;BA.debugLine="Interstitial.Show";
mostCurrent._interstitial.Show(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 108;BA.debugLine="If Interstitial.Status=Interstitial.Status_Dismis";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_Dismissed) { 
 //BA.debugLineNum = 109;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 111;BA.debugLine="t.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _t1_tick() throws Exception{
 //BA.debugLineNum = 114;BA.debugLine="Sub t1_Tick";
 //BA.debugLineNum = 115;BA.debugLine="If Interstitial.Status=Interstitial.Status_AdRead";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_AdReadyToShow) { 
 //BA.debugLineNum = 116;BA.debugLine="Interstitial.Show";
mostCurrent._interstitial.Show(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 119;BA.debugLine="If Interstitial.Status=Interstitial.Status_Dismis";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_Dismissed) { 
 //BA.debugLineNum = 120;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 122;BA.debugLine="End Sub";
return "";
}
}
