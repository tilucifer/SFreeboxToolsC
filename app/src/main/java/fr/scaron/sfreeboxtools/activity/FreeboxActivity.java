package fr.scaron.sfreeboxtools.activity;

import java.util.ArrayList;
import java.util.List;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.connect.HttpConnect;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.control.FreeboxControler;
import fr.scaron.sfreeboxtools.model.Download;
import fr.scaron.sfreeboxtools.model.DownloadAdapter;
import fr.scaron.sfreeboxtools.model.FbxHttpRaster;
import fr.scaron.sfreeboxtools.model.HttpRaster;
import fr.scaron.sfreeboxtools.view.AddHttpView;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "tilucifer@gmail.com", customReportContent = {
		ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
		ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
		ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class FreeboxActivity extends AbstractActivity
{

	private Runnable mUpdateTask;
	private boolean isPaused;

	public static Logger log = LoggerFactory.getLogger(FreeboxActivity.class);
	DownloadAdapter adapter;

	@Override
	protected void onPause() {
		super.onPause();
		log.info("est en pause");
		log.info("on met le flag isPaused à true");
		isPaused = true;
	}

	@Override
	public void onBackPressed() {
		handle.removeCallbacks(mUpdateTask);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.info("sort de pause");
		log.info("on met le flag isPaused à false");
		isPaused = false;
        if (!FreeboxControler.checkGlobalPrefs(this)) {
		    launchUpdateTask();
        }else{
            setRefreshVisible(false);
        }
	}
	
	private void launchUpdateTask(){
		log.info("on lance la tache de mise à jour");
		handle.removeCallbacks(mUpdateTask);
		if (!isFinishing()&&!isPaused){
			handle.postDelayed(mUpdateTask, 3000);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.init(this);
		AndroidDashboardDesignActivity.addActivity(this);
		log.info("est en creation");
		setContentView(R.layout.freebox_list_layout);
		super.setProgressIcon(R.drawable.freebox_connect_icon_default);
		super.setBarIcon(R.drawable.btn_freebox);
		if (FreeboxControler.checkGlobalPrefs(this)) {
			super.showDialogConfig();
			return;
		}
		
		setRefreshVisible(true);
		mUpdateTask  = new Runnable() {
			public void run() {
				FbxHttpRaster fbxGetDownloads = (FbxHttpRaster) FreeboxControler.createRequest(
					Params.FBX_REQ_DOWNLOADS, FreeboxActivity.this);
				HttpConnect.getInstance().execute(fbxGetDownloads);
			}
		};
		final TextView freeboxTitle = (TextView) findViewById(R.id.freebox_title);
		freeboxTitle.setText("Initialisation en cours..");
		freeboxTitle.setVisibility(View.GONE);

		HttpConnect connection = HttpConnect.getInstance();
		FbxHttpRaster fbxHttpRaster = (FbxHttpRaster) FreeboxControler.createRequest(Params.FBX_REQ_DOWNLOADS, this);
		this.sendMessage(Params.MSG_IND, Params.getMessageAttente(fbxHttpRaster.requestLabel));	
		connection.execute(fbxHttpRaster);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.removeGroup(R.id.group_torrent_navigation);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		//SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		MenuItem searchMenuItem = menu.findItem(R.id.search);;
		//TODO : Verifier si le MenuItemCompat couvre toutes les versions d'android
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(true);

		SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String newText) {
				// this is your adapterFiles that will be filtered
				adapter.getFilter().filter(newText);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// this is your adapterFiles that will be filtered
				adapter.getFilter().filter(query);
				return true;
			}

		};
		searchView.setOnQueryTextListener(queryTextListener);
		setRefreshVisible(true);
		return true;

	}
	


	@Override
	public void updateTitle(String title) {
		TextView freeboxTitle = (TextView) findViewById(R.id.freebox_title);
		if ("0".equals(title)) {
			freeboxTitle.setVisibility(View.VISIBLE);
			freeboxTitle.setText("T\351l\351chs.(Aucun)");
		} else {
			freeboxTitle.setVisibility(View.GONE);
		}
		this.setTitle("T\351l\351chs. (" + title + ")");
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		log.trace("item selectionn\351 de l'actionbar : " + item.getItemId()
				+ " | title " + item.getTitle());
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_refresh:
			log.info("Vous avez appuy\351 sur la commande d'actualisation donc on actualise");
			setRefreshVisible(true);
			//item.setActionView(R.layout.actionbar_indeterminate_progress);
			FbxHttpRaster fbxHttpRaster = (FbxHttpRaster) FreeboxControler.createRequest(Params.FBX_REQ_DOWNLOADS, this);
			HttpConnect connection = HttpConnect.getInstance();
			this.sendMessage(Params.MSG_IND, Params.getMessageAttente(fbxHttpRaster.requestLabel));
			connection.execute(fbxHttpRaster);
			return true;

		case R.id.menu_freebox_add_url:

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			final AddHttpView addHttpView = new AddHttpView(this);
			alertDialogBuilder.setView(addHttpView);
			alertDialogBuilder.setTitle("Ajouter un lien");
			// // set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("Ajouter",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									setRefreshVisible(true);
									String url = addHttpView.getUrl();
									SharedPreferences settings = PreferenceManager
											.getDefaultSharedPreferences(FreeboxActivity.this);
									SharedPreferences.Editor editor = settings.edit();
									editor.putString("add_dwl_url", url);
									editor.apply();
									FbxHttpRaster fbxHttpRaster = (FbxHttpRaster) FreeboxControler.createRequest(Params.FBX_REQ_ADD_URL, FreeboxActivity.this);
									HttpConnect connection = HttpConnect.getInstance();
									FreeboxActivity.this.sendMessage(Params.MSG_IND, Params.getMessageAttente(fbxHttpRaster.requestLabel));
									connection.execute(fbxHttpRaster);
								}
							})
					.setNegativeButton("Annuler",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			return true;
		case R.id.menu_freebox_add_torrent:
			return true;
			case android.R.id.home:			
				handle.removeCallbacks(mUpdateTask);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateView(final HttpRaster httpRaster) {
		log.info("updateView for response of request "+httpRaster.getClass().getName());
		Object reponse = "L'op\351ration avec la freebox a \351chou\351e sans erreur pr\351cise";
		reponse = FreeboxControler.processResponse((FbxHttpRaster) httpRaster, this);
		if (reponse instanceof Boolean){
			if (((FbxHttpRaster)httpRaster).getFinalRequestLabel()==null){
				finish();
			}
		}else if (reponse instanceof Integer){
			handle.sendMessage(handle.obtainMessage(Params.MSG_CNF, "T\351l\351chargement (id="+reponse+") ajout\351 à la freebox"));
		}else if (reponse instanceof String) {
			if (((FbxHttpRaster) httpRaster).getFinalRequestLabel()==null){
				if (httpRaster.isFlagResponseError()) {
					log.error("Erreur "+httpRaster.getErrorCode()+" pour "+httpRaster.getClass().getName());
					handle.sendMessage(handle.obtainMessage(Params.MSG_ERR, reponse));
				}else{
					handle.sendMessage(handle.obtainMessage(Params.MSG_CNF, reponse));
				}
			}
		} else if (reponse instanceof ArrayList<?>) {
			ArrayList<?> reponseArray = (ArrayList<?>) reponse;
			if (!reponseArray.isEmpty()
					&& reponseArray.get(0) instanceof Download) {
					ListView freeboxList = (ListView) findViewById(R.id.freebox_list);
				if (adapter == null) {
					
					adapter = new DownloadAdapter(this,
							R.layout.list_item_notifstyle_small,
							(List<Download>) reponse);
					freeboxList.setAdapter(adapter);
					adapter.setNotifyOnChange(true);
					//freeboxList.setSelection(0);
				} else {
					adapter.setDownloads((List<Download>) reponse);
					adapter.setNotifyOnChange(true);
					//freeboxList.setSelection(0);
				}
			}
			setRefreshVisible(false);
			handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
					"Liste des t\351l\351chargements"));
			launchUpdateTask();
		}
	}
}
