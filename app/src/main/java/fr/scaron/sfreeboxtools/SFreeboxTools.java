package fr.scaron.sfreeboxtools;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import fr.scaron.sfreeboxtools.connect.HttpConnect;
import fr.scaron.sfreeboxtools.model.TinyDB;
@ReportsCrashes(
    formKey = "", // This is required for backward compatibility but not used
	formUri = "https://collector.tracepot.com/301ca578",
	applicationLogFile = "/sdcard/Android/data/fr.scaron.sfreeboxtools/files/console.log"
)

//@ReportsCrashes(formKey = "", // will not be used
//mailTo = "tilucifer@gmail.com",
//customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },                
//mode = ReportingInteractionMode.TOAST,
//resToastText = R.string.crash_toast_text)
public class SFreeboxTools extends Application {

    public static Logger log = LoggerFactory.getLogger(SFreeboxTools.class);

	public static boolean bouchon=false;
    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = settings.edit();



        TinyDB myDB = new TinyDB(SFreeboxTools.this);
        ArrayList<String> boxNames = myDB.getList("FreeboxBoxNames");
        ArrayList<String> boxIps = myDB.getList("FreeboxBoxIps");
        ArrayList<String> boxPorts = myDB.getList("FreeboxBoxPorts");
        ArrayList<String> boxTokens = myDB.getList("FreeboxBoxTokens");
        ArrayList<Boolean> boxSelecteds = myDB.getListBoolean("FreeboxBoxSelecteds");
        log.info("listebbox null ? : "+(boxNames==null));

		log.info("boxNames found before : '"+boxNames.toString()+"'");
		log.info("boxIps found before : '"+boxIps.toString()+"'");
		log.info("boxPorts found before : '"+boxPorts.toString()+"'");
		log.info("boxTokens found before : '"+boxTokens.toString()+"'");
		log.info("boxSelecteds found before : '"+boxSelecteds.toString()+"'");
		
		if (bouchon){
		//BOUCHON SEB
		boxNames=null;
		//FIN BOUCHON SEB
		}
		
       if (boxNames==null||boxNames.isEmpty()){
            boxNames = new ArrayList<String>();
            boxIps = new ArrayList<String>();
            boxPorts = new ArrayList<String>();
            boxTokens = new ArrayList<String>();
            boxSelecteds = new ArrayList<Boolean>();

            
			
            //boxNames.add("Ma Freebox Locale");
           // boxIps.add("mafreebox.free.fr");
           // boxPorts.add("80");
           // boxTokens.add(myDB.getString("app_token"));
            //boxSelecteds.add(true);
            //log.info("Freebox n°"+"2"+";"+"Ma Freebox Locale"+";"+"mafreebox.free.fr"+";"+"80"+";"+myDB.getString("app_token")+";"+"true");


           boxNames.add("Ajouter une freebox");
           boxIps.add("mafreebox.free.fr");
           boxPorts.add("80");
           boxTokens.add("TODO");



		   String ip=myDB.getString("pref_ip_public_freebox");
		   
		   if (bouchon){
           //BOUCHON SEB
		   boxSelecteds.add(false);
           boxNames.add("Ma freebox bouch");
		   boxIps.add("88.162.206.4");
		   boxPorts.add("80");
		   boxTokens.add("ZV3PA3iKPGSMTyxnNN/z+WKykVhIBmHFbKAg39kDTcqixHndqpqudIvpdPkJhuKA");
		   boxSelecteds.add(true);
		   ip=null;
           //FIN BOUCHON SEB
           }

		   if (ip !=null && !ip.isEmpty()){
			   String port = myDB.getString("pref_port_public_freebox");
			   log.info("port : " + port);
			   if (port == null || port.isEmpty()) {
				   port = "80";
			   }

			   String token = myDB.getString("app_token");
			   if (token!=null && !token.isEmpty()){
				   //Deselection de l'elt ajouter une freebox
		           boxSelecteds.add(false);
				   boxNames.add("Ma Freebox");
				   boxIps.add(ip);
				   boxPorts.add(port);
				   boxTokens.add(token);
				   boxSelecteds.add(true);

				   log.info("Freebox n°"+"1"+";"+"Ajouter une freebox"+";"+"mafreebox.free.fr"+";"+"80"+";"+"TODO"+";"+"false");
				   log.info("Freebox n°"+"2"+";"+"Ma Freebox"+";"+ip+";"+"80"+";"+token+";"+"true");
			   }else{
				   //Selection de l'elt ajouter une freebox
				   boxSelecteds.add(true);
				   log.info("Freebox n°"+"1"+";"+"Ajouter une freebox"+";"+"mafreebox.free.fr"+";"+"80"+";"+"TODO"+";"+"true");
			   }

		   }else{
			   //Selection de l'elt ajouter une freebox
			   boxSelecteds.add(true);
			   log.info("Freebox n°"+"1"+";"+"Ajouter une freebox"+";"+"mafreebox.free.fr"+";"+"80"+";"+"TODO"+";"+"true");
		   }
		   
		   log.info("boxNames found after: '"+boxNames.toString()+"'");
		   
		   
            myDB.putList("FreeboxBoxNames", boxNames);
            myDB.putList("FreeboxBoxIps", boxIps);
            myDB.putList("FreeboxBoxPorts", boxPorts);
            myDB.putList("FreeboxBoxTokens", boxTokens);
            myDB.putListBoolean("FreeboxBoxSelecteds", boxSelecteds);

        }

		//SEB
		//editor.putString("app_token","Nb4yLA7rI5Lmksad212jOgQgFbzudw+/SGiAZWQckndJWgKLW0W7Jp0HBmFvRnlC");
		//editor.putString("app_token","pHNP9L4U3P4AUk1f+YtQUUUQbarLj4UQGwCg5+5vgkpx1t31vH+OtFUNCpRIhoUh");
		//editor.putString("app_token","ZV3PA3iKPGSMTyxnNN/z+WKykVhIBmHFbKAg39kDTcqixHndqpqudIvpdPkJhuKA");
		
		//YOHAN
		//editor.putString("app_token","4rh0c94tbj4aC689pfGziX6kO4Xx8PXfbbL9Ql7rDkT/23Q1584YL/XnorBsvKgB");
		editor.apply();
        //
        HttpConnect.setInstance(getBaseContext());
    }
	
	
}
