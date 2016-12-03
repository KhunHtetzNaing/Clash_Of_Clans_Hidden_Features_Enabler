Type=Activity
Version=6.3
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
Dim t,t1 As Timer
End Sub

Sub Globals
Dim b1,b2 As Button
Dim b1bg,b2bg,ibg As BitmapDrawable
Dim ml As MLfiles
Dim p As Phone
Dim Banner As AdView
Dim Interstitial As mwAdmobInterstitial
Dim imv As ImageView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	ibg.Initialize(LoadBitmap(File.DirAssets,"bg.png"))
	imv.Initialize("imv")
	Activity.AddView(imv,2%x,2%y,96%x,40%y)
	imv.Background = ibg
	
	If p.SdkVersion > 19 Then
		Banner.Initialize("Banner","ca-app-pub-4173348573252986/9546639356")
		Banner.LoadAd
		Activity.AddView(Banner,0%x,90%y,100%x,10%y)
		
		Interstitial.Initialize("Interstitial","ca-app-pub-4173348573252986/3500105755")
		Interstitial.LoadAd
		t.Initialize("t",500)
		t.Enabled = False
		t1.Initialize("t1",15000)
		t1.Enabled = True
	End If
	
	b1bg.Initialize(LoadBitmap(File.DirAssets,"install.png"))
	b2bg.Initialize(LoadBitmap(File.DirAssets,"restore.png"))
	
 b1.Initialize("b1")
b1.Background = b1bg

b2.Initialize("b2")
b2.Background = b2bg	

Activity.AddView(b1,20%x,50%y,60%x,13%y)
Activity.AddView(b2,20%x,(b1.Top+b1.Height)+5%y,60%x,13%y)

t.Initialize("t",500)
t.Enabled = False
t1.Initialize("t1",15000)
t1.Enabled = True
End Sub

Sub b1_Click
	If p.SdkVersion > 19 Then
		t.Enabled = True
	End If
	ml.GetRoot
	If ml.HaveRoot Then
    If	File.Exists(File.DirRootExternal,"traps.csv") = False Then
	File.Copy(File.DirAssets,"traps.csv",File.DirRootExternal,"traps.csv")
	End If
	
	If File.Exists(File.DirRootExternal,"buildings.csv") = False Then
	File.Copy(File.DirAssets,"buildings.csv",File.DirRootExternal,"buildings.csv")
	End If
	
	'Install
	ml.RootCmd("mount -o rw,remount /data","",Null,Null,False)
	ml.RootCmd("cp -r "&File.DirRootExternal&"/traps.csv data/data/com.supercell.clashofclans/update/logic/traps.csv","",Null,Null,False)
	ml.RootCmd("cp -r "&File.DirRootExternal&"/buildings.csv data/data/com.supercell.clashofclans/update/logic/buildings.csv","",Null,Null,False)
	ml.chmod("/data/data/com.supercell.clashofclans/update/logic/traps.csv",666)
	ml.chmod("/data/data/com.supercell.clashofclans/update/logic/buildings.csv",666)
	
	File.Delete(File.DirRootExternal,"traps.csv")
	File.Delete(File.DirRootExternal,"buildings.csv")
	
	Msgbox("COC Show Traps and Teslas Installed!" & CRLF & "Now, You can see all Traps and Teslas Before Attack","Completed")
	Else
	Msgbox("Your Device is not have Root Access","Attention!")
	End If
End Sub

Sub b2_Click
	If p.SdkVersion > 19 Then
		t.Enabled = True
	End If
	ml.GetRoot
	If ml.HaveRoot Then
	ml.rm("data/data/com.supercell.clashofclans/update/logic/traps.csv")
	ml.rm("data/data/com.supercell.clashofclans/update/logic/buildings.csv")
	Msgbox("Original Restore Finished" & CRLF & "You can Install again!","Completed!")
	Else
	Msgbox("Your Device not have Root Access","Attention!")
		End If
End Sub

Sub t_Tick
	If Interstitial.Status=Interstitial.Status_AdReadyToShow Then
		Interstitial.Show
	End If
	
	If Interstitial.Status=Interstitial.Status_Dismissed Then
		Interstitial.LoadAd
	End If
	t.Enabled = False
End Sub

Sub t1_Tick
	If Interstitial.Status=Interstitial.Status_AdReadyToShow Then
		Interstitial.Show
	End If
	
	If Interstitial.Status=Interstitial.Status_Dismissed Then
		Interstitial.LoadAd
	End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
