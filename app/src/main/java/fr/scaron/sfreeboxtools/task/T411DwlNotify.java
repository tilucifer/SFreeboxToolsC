package fr.scaron.sfreeboxtools.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import fr.scaron.sfreeboxtools.connect.HttpConnect;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.control.FreeboxControler;
import fr.scaron.sfreeboxtools.model.Download;
import fr.scaron.sfreeboxtools.model.FbxHttpRaster;
import fr.scaron.sfreeboxtools.model.HttpRaster;

public class T411DwlNotify extends AbstractTask {
	public static Logger log = LoggerFactory.getLogger(T411DwlNotify.class);

    private NotificationHelper mNotificationHelper;
    private Download download;
    public T411DwlNotify(Context context, Download download){
    	super(context);
        mNotificationHelper = new NotificationHelper(context);
        this.download = download;
    }
    protected void onPreExecute(){
		
		log.debug("Pre execution pour la tache de notificatio pour le download "+ download.getId());
        //Create the notification in the statusbar
        mNotificationHelper.createNotification(download);
        log.debug("fin de Pre execution pour la tache de notificatio pour le download "+ download.getId());
    }
    
    
	@Override
	public void updateView(HttpRaster httpRaster) {
		log.debug("Mise à jour de la vue -> notif "+mNotificationHelper.getNotifId()+" ..");
		Object reponse = FreeboxControler.processResponse((FbxHttpRaster) httpRaster, this);
		if (reponse instanceof Download){
			download = (Download)reponse;
//			publishProgress((int)(download.getRx_pct()/100));
	        mNotificationHelper.progressUpdate((int)(download.getRx_pct()/100));
	        log.debug("Action maj downloadView -> notif "+mNotificationHelper.getNotifId()+" ..");
		}
	}
    protected void onProgressUpdate(Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
    }
    protected void onPostExecute(Void result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
    }
	
	
	@Override
	protected Object doInBackground(Object... params) {
//		try{
		log.debug("Téléchargement de taille "+download.getSize()+" "+(download.getRx_pct()/100)+" effectué");
		while (((int)(download.getRx_pct()/100)<100)&&(download.getSize()<1047527424)){
			
			SharedPreferences settings = PreferenceManager
    				.getDefaultSharedPreferences(context);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putInt("dwl_id", download.getId());
    		editor.apply();
			FbxHttpRaster fbxGetDownload = (FbxHttpRaster) FreeboxControler.createRequest(
					Params.FBX_REQ_DOWNLOAD, T411DwlNotify.this);
            log.debug("Requete de maj dans liste d'attente -> notif "+mNotificationHelper.getNotifId()+" ...");
			HttpConnect.getInstance().execute(fbxGetDownload);
			
			
			
			try {
	            Thread.sleep(3000);
//	            handle.removeCallbacks(mUpdateTask);
//	            log.debug("Requete de maj dans liste d'attente -> notif "+mNotificationHelper.getNotifId()+" ...");
//	            handle.postDelayed(mUpdateTask, 2000);
	
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
		}
		mNotificationHelper.completed();
		return null;
	}
	@Override
	public void setRefreshVisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateTitle(String title) {
		// TODO Auto-generated method stub
		
	}

}
