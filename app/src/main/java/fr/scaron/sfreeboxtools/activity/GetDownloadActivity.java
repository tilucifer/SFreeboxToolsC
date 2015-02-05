package fr.scaron.sfreeboxtools.activity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.TabHost;
import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.connect.HttpConnect;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.control.FreeboxControler;
import fr.scaron.sfreeboxtools.model.Download;
import fr.scaron.sfreeboxtools.model.DownloadFile;
import fr.scaron.sfreeboxtools.model.DownloadFileAdapter;
import fr.scaron.sfreeboxtools.model.DownloadTracker;
import fr.scaron.sfreeboxtools.model.FbxHttpRaster;
import fr.scaron.sfreeboxtools.model.HttpRaster;
import fr.scaron.sfreeboxtools.view.DownloadView;

public class GetDownloadActivity extends AbstractActivity implements Follower {

	public static Logger log = LoggerFactory.getLogger(GetDownloadActivity.class);
	private Download download;
	private DownloadView downloadView;
	private DownloadFileAdapter adapter;
	private Runnable mUpdateTask;
	TabHost tabHost;
	private boolean isPaused;

	@Override
	protected void onPause() {
		super.onPause();
		log.info("est en pause");
		log.info("on met le flag isPaused à true");
		isPaused = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.info("sort de pause");
		log.info("on met le flag isPaused à false");
		isPaused = false;
		launchUpdateTask();
	}
	
	private void launchUpdateTask(){
		log.info("on lance la tache de mise à jour");
		handle.removeCallbacks(mUpdateTask);
		if (!isFinishing()&&!isPaused){//download.getRx_pct()<10000){
			handle.postDelayed(mUpdateTask, 3000);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.init(this);
		download = (Download)getIntent().getSerializableExtra("Download");
		mUpdateTask  = new Runnable() {
			   public void run() {
		    		SharedPreferences settings = PreferenceManager
		    				.getDefaultSharedPreferences(GetDownloadActivity.this);
		    		SharedPreferences.Editor editor = settings.edit();
		    		editor.putInt("dwl_id", download.getId());
		    		editor.apply();
					FbxHttpRaster fbxGetDownload = (FbxHttpRaster) FreeboxControler.createRequest(
							Params.FBX_REQ_DOWNLOAD, GetDownloadActivity.this);
					HttpConnect.getInstance().execute(fbxGetDownload);
			   }
		};
		
		
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("dwl_id", download.getId());
		editor.apply();
		downloadView = new DownloadView(this, download);
		setContentView(downloadView);
//		//On lance la tache de mise à jour de la fiche détail
//		launchUpdateTask(); // On récupère les fichiers d'abord maintenant
		
		//Récupération des fichiers inclus dans le téléchargement
		FbxHttpRaster fbxGetDownloadFiles = (FbxHttpRaster) FreeboxControler.createRequest(
				Params.FBX_REQ_DOWNLOAD_FILES, this);
		HttpConnect.getInstance().execute(fbxGetDownloadFiles);
//		//TODO Test de notif
//		T411DwlNotify t411DwlNotify = new T411DwlNotify(this, download);
//		t411DwlNotify.execute();
//		//TODO Fin de test de notif
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.removeGroup(R.id.group_torrent_navigation);
		menu.removeGroup(R.id.group_seedbox);
		menu.removeItem(R.id.search);
		menu.removeItem(R.id.action_refresh);
		return true;
	}

	@Override
	public void onBackPressed() {
		handle.removeCallbacks(mUpdateTask);
		finish();
	}

	@Override
	public void updateView(HttpRaster httpRaster) {
        log.trace("Mise à jour de la vue..");
		Object reponse = FreeboxControler.processResponse((FbxHttpRaster) httpRaster, this);
		if (reponse instanceof Download){
			download = (Download)reponse;
			downloadView.update(download);
			launchUpdateTask();
			return;
		} else if (reponse instanceof ArrayList<?>) {
			ArrayList<?> reponseArray = (ArrayList<?>) reponse;
			if (!reponseArray.isEmpty()
					&& reponseArray.get(0) instanceof DownloadFile) {
				downloadView.setDownloadFiles((List<DownloadFile>) reponse);
//				adapterFiles = downloadView.getListViewAdapter();
//				adapterFiles.setDownloadFiles((List<DownloadFile>) reponse);
//				adapterFiles.setNotifyOnChange(true);
			}else if (!reponseArray.isEmpty()
						&& reponseArray.get(0) instanceof DownloadTracker) {

				downloadView.setDownloadTrackers((List<DownloadTracker>) reponse);
//					adapterFiles = downloadView.getListViewAdapter();
//					adapterFiles.setDownloadFiles((List<DownloadTracker>) reponse);
//					adapterFiles.setNotifyOnChange(true);
				}
			setRefreshVisible(false);
			launchUpdateTask();
			handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
					"Liste des fichiers"));
		}
        log.trace("Aucune action sur la mise à jour de la vue..");
	}
	
	public void setDownload(final Download pDownload){
		this.download = pDownload;
	}
}
