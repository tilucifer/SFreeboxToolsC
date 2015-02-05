package fr.scaron.sfreeboxtools.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import fr.scaron.sfreeboxtools.control.FreeboxControler;
import fr.scaron.sfreeboxtools.model.Download;
import fr.scaron.sfreeboxtools.model.FbxHttpRaster;
import fr.scaron.sfreeboxtools.model.HttpRaster;
import fr.scaron.sfreeboxtools.view.DownloadMiniView;

public class GetDownloadMiniActivity extends AbstractActivity {

	public static Logger log = LoggerFactory.getLogger(GetDownloadMiniActivity.class);
	private Download download;
//	private Integer downloadId;
	private DownloadMiniView downloadView;

//	private ProgressDialog progressDialog;
//	private Handler handle;
	long mStartTime;

	
	private Runnable mUpdateTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.init(this);
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("dwl_id", download.getId());
		editor.apply();
		downloadView = new DownloadMiniView(this, download);
		setContentView(downloadView);
		if (mStartTime == 0L) {
            mStartTime = System.currentTimeMillis();
            handle.removeCallbacks(mUpdateTask);
        	handle.postDelayed(mUpdateTask, 2000);
		}
	}
	@Override
	public void onBackPressed() {
		handle.removeCallbacks(mUpdateTask);
		finish();
//		super.onBackPressed();
	}

	@Override
	public void updateView(HttpRaster httpRaster) {

        log.trace("Mise à jour de la vue..");
		Object reponse = FreeboxControler.processResponse((FbxHttpRaster) httpRaster, this);
		if (reponse instanceof Download){
			download = (Download)reponse;
			downloadView.update(download);

	        log.trace("Action sur la mise à jour de la vue dans downloadView..");     
			handle.removeCallbacks(mUpdateTask);
			if (!isFinishing()){
				handle.postDelayed(mUpdateTask, 2000);
			}
			return;
		}

        log.debug("Aucune action sur la mise à jour de la vue..");
	}

	
	public void setDownload(final Download pDownload){
		this.download = pDownload;
	}
	
	

}
