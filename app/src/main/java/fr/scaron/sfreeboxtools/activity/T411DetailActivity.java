package fr.scaron.sfreeboxtools.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.control.FreeboxControler;
import fr.scaron.sfreeboxtools.control.T411Controler;
import fr.scaron.sfreeboxtools.model.BSize;
import fr.scaron.sfreeboxtools.model.Download;
import fr.scaron.sfreeboxtools.model.FbxAddFileDownload;
import fr.scaron.sfreeboxtools.model.FbxHttpRaster;
import fr.scaron.sfreeboxtools.model.HttpRaster;
import fr.scaron.sfreeboxtools.model.T411Search;
import fr.scaron.sfreeboxtools.model.T411Torrent;
import fr.scaron.sfreeboxtools.model.T411Torrrents;
import fr.scaron.sfreeboxtools.model.TinyDB;
import fr.scaron.sfreeboxtools.task.T411DwlNotify;
import fr.scaron.sfreeboxtools.view.Ratio;
import fr.scaron.sfreeboxtools.view.TorrentView;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "tilucifer@gmail.com", customReportContent = {
		ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
		ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
		ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class T411DetailActivity extends AbstractActivity {
	public static Logger log = LoggerFactory.getLogger(T411DetailActivity.class);

	String id;
	ListView maListViewPerso;
	ArrayList<HashMap<String, String>> listItem;
	SimpleAdapter mTorrentsAdapter;
	String strPrev = null;
	String strNext = null;
	RelativeLayout contentFrame;
	T411Torrent t411Detail;
    TinyDB myDB;

	T411Search t411Search;
	
	MenuItem searchMenuItem = null;
	SearchView searchView = null;

	private MenuItem btnPrevItem;

	private MenuItem btnNextItem;

	@Override
	protected void onResume() {
		super.onResume();
		log.info("sort de pause");
	}

	@Override
	public void updateTitle(String title) {
		this.setTitle(title);
	}

	@Override
	public void onBackPressed() {
		finish();
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
	public boolean onOptionsItemSelected(MenuItem item) {

		log.trace("item selectionné de l'actionbar : " + item.getItemId()
				+ " | title " + item.getTitle());
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_torrent_prev_page:
			if (strPrev != null && !"".equals(strPrev)) {
				setRefreshVisible(true);
				t411Search.setPaginator(strPrev);
				t411Search.setFlagParamsString(true);
				T411Controler.requestProcess(t411Search, T411DetailActivity.this);
			}
			return true;
		case R.id.menu_torrent_next_page:
			if (strNext != null && !"".equals(strNext)) {
				setRefreshVisible(true);
				t411Search.setPaginator(strNext);
				t411Search.setFlagParamsString(true);
				T411Controler.requestProcess(t411Search, T411DetailActivity.this);
			}
			return true;
		case R.id.action_refresh:
			setRefreshVisible(true);
			log.info("Vous avez appuy\351 sur la commande d'actualisation donc on actualise");
			setRefreshVisible(true);
			t411Search.setPaginator(null);
			T411Controler.requestProcess(t411Search, T411DetailActivity.this);
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setBtnPrevVisible(boolean visible) {
		if (btnPrevItem != null) {
			btnPrevItem.setEnabled(visible);
			btnPrevItem.setVisible(visible);
		}
	}

	private void setBtnNextVisible(boolean visible) {
		if (btnNextItem != null) {
			btnNextItem.setEnabled(visible);
			btnNextItem.setVisible(visible);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        myDB = new TinyDB(T411DetailActivity.this);
		t411Detail = (T411Torrent)getIntent().getSerializableExtra("T411Detail");
		super.init(this);
		super.setProgressIcon(R.drawable.t411_icon_default);
		if(t411Detail.getIcon()==0){
			super.setBarIcon(R.drawable.t411_icon_default);//R.drawable.btn_menu_t411);
		}else{
			super.setBarIcon(t411Detail.getIcon());//R.drawable.btn_menu_t411);
		}
		
		setContentView(new TorrentView(this,t411Detail));
		updateTitle(t411Detail.getTorrent_Name());

		setBarSubtitle(t411Detail.getTduploader());
//		AndroidDashboardDesignActivity.addActivity(this);
//		log.info("est en creation");
//		if (T411Controler.checkGlobalPrefs(this)) {
//			super.showDialogConfig();
//			return;
//		}
//		setRefreshVisible(true);
//		setContentView(R.layout.t411_list_layout);
//
//		contentFrame = (RelativeLayout) findViewById(R.id.content_frame);
//		if (contentFrame != null) {
//			contentFrame.setVisibility(View.GONE);
//		}
//		maListViewPerso = (ListView) findViewById(R.id.malistviewperso);
//		listItem = new ArrayList<HashMap<String, String>>();
//
//		id = getIntent().getStringExtra("id");
//
//		maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			@SuppressWarnings("unchecked")
//			public void onItemClick(AdapterView<?> a, View v, int position,
//					long id) {
//
//				setRefreshVisible(true);
//				HashMap<String, String> map = (HashMap<String, String>) maListViewPerso
//						.getItemAtPosition(position);
//
//				String torrent_id = map.get("ID");
//				String torrent_name = map.get("nomComplet");
//				String uploader = map.get("uploader");
//				T411GetTorrent t411GetTorrent = new T411GetTorrent(
//						Params.T411_URL_GET_PREZ, Params.GET, T411DetailActivity.this);
//				t411GetTorrent.setIcon(Integer.valueOf(map.get("icon")));
//				t411GetTorrent.setId(torrent_id);
//				t411GetTorrent.setName(torrent_name);
//				t411GetTorrent.setUploader(uploader);
//				t411GetTorrent.setFlagParamsString(true);
//				T411Controler.requestProcess(t411GetTorrent, T411DetailActivity.this);
//
//			}
//		});
//		registerForContextMenu(maListViewPerso);
//
//		mTorrentsAdapter = new SimpleAdapter(getBaseContext(), listItem,
//				R.layout.item_torrent, new String[] { "nomComplet", "age",
//						"taille", "avis", "seeders", "leechers", "uploader",
//						"ratio", "completed", "ratioBase", "icon" }, new int[] {
//						R.id.tNom, R.id.tAge, R.id.tTaille, R.id.tComments,
//						R.id.tSeeders, R.id.tLeechers, R.id.tUploader,
//						R.id.tRatio, R.id.tCompleted, R.id.tRatioBase,
//						R.id.tIcon });
//
//		maListViewPerso.setAdapter(mTorrentsAdapter);
//		setBtnNextVisible(false);
//		setBtnPrevVisible(false);
//
//		t411Search = new T411Search(Params.T411_URL_TOP100, Params.GET, this);
//		t411Search.setFlagParamFile(false);
//		t411Search.setFlagParamsJson(false);
//		t411Search.setFlagParamsString(false);
//		T411Controler.requestProcess(t411Search, this);
	}

	@Override
	public void updateView(final HttpRaster httpRaster) {
		log.info("updateView");
		Object reponse = "L'op\351ration avec t411 a \351chou\351e sans erreur pr\351cise";

		final TextView t411Title = (TextView) findViewById(R.id.t411_title);
		if (httpRaster instanceof FbxHttpRaster){
			reponse = "L'op\351ration avec la freebox a \351chou\351e sans erreur pr\351cise";
			reponse = FreeboxControler.processResponse((FbxHttpRaster) httpRaster, this);
			if (reponse instanceof Boolean){
				if (((FbxHttpRaster)httpRaster).getFinalRequestLabel()==null){
//					handle.sendMessage(handle.obtainMessage(Params.MSG_CNF, "deconnection ? "+reponse));
					finish();
				}
			}else if (reponse instanceof Integer){
				handle.sendMessage(handle.obtainMessage(Params.MSG_CNF, "Téléchargement (id="+reponse+") ajouté à la freebox"));

				Download download = new Download();
				if (httpRaster instanceof FbxAddFileDownload){
					FbxAddFileDownload fbxAddFileDownload = (FbxAddFileDownload)httpRaster;
					download.setName(fbxAddFileDownload.getDownload_file_name());
				}
				download.setId((Integer)reponse);
				//Create notification to show download progress
				//if notifications flag is on
				//TODO FIXME
				Boolean notifActive = myDB.getBoolean("pref_notifactive_torrent");

				log.info("Notifications actives ? "+notifActive);
				if (notifActive!=null && notifActive==true){
					log.info("Notifications actives on cree la notification");
					createNotification(download);
				}
				
				
			}else if (reponse instanceof String) {
				if (!httpRaster.isFlagResponseError()) {
					if (((FbxHttpRaster) httpRaster).getFinalRequestLabel()==null){
						handle.sendMessage(handle.obtainMessage(Params.MSG_CNF, reponse));
					}
				} else {
					log.error("Erreur "+httpRaster.getErrorCode()+" pour "+httpRaster.getClass().getName());
					handle.sendMessage(handle.obtainMessage(Params.MSG_ERR, reponse));
				}
			}
		}else{
			reponse = T411Controler.responseProcess(httpRaster, this);
		
			if (reponse instanceof String) {
				
				if (httpRaster.isFlagResponseError()) {
					log.error("Erreur "+httpRaster.getErrorCode()+" pour "+httpRaster.getClass().getName());
					handle.sendMessage(handle.obtainMessage(Params.MSG_ERR, reponse));
				}
			} else if (reponse instanceof T411Torrrents) {
				handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
						"Affichage de la liste..."));
				listItem.clear();
				T411Torrrents t411Reponse = (T411Torrrents) reponse;
				ArrayList<HashMap<String, String>> map = t411Reponse.getMap();
				showInfosUser();
				
				if (!map.isEmpty()){
					if (searchView != null && searchView.isShown()){
			            searchView.onActionViewCollapsed();
			            InputMethodManager imm = (InputMethodManager)getSystemService(
							      Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
					}
				}

				strPrev = t411Reponse.getStrPrev();
				if (strPrev != null && !strPrev.isEmpty()) {
					setBtnPrevVisible(true);
				} else {
					setBtnPrevVisible(false);
				}
				strNext = t411Reponse.getStrNext();
				if (strNext != null && !strNext.isEmpty()) {
					setBtnNextVisible(true);
				} else {
					setBtnNextVisible(false);
				}
				log.trace("Liste de torrents\n----------\n" + map);
				listItem.addAll(map);
				
				mTorrentsAdapter.notifyDataSetChanged();
				
				//maListViewPerso.clearChoices();
				//maListViewPerso.setItemChecked(0, true);
				maListViewPerso.setSelection(0);
				updateTitle(String.valueOf(listItem.size()));
			} else if (reponse instanceof ArrayList<?>) {
				listItem.clear();
				ArrayList<HashMap<String, String>> map = (ArrayList<HashMap<String, String>>) reponse;
				log.trace("Liste de torrents\n----------\n" + map);
				listItem.addAll(map);
				mTorrentsAdapter.notifyDataSetChanged();
				//maListViewPerso.clearChoices();
				
				updateTitle(String.valueOf(listItem.size()));
			} else if (reponse instanceof T411Torrent) {
				final T411Torrent t411Torrent = (T411Torrent) reponse;
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle("D\351tail du torrent");
				TorrentView t411Detail = new TorrentView(this, t411Torrent);
				setContentView(t411Detail);
//				adb.setView(t411Detail);
//				adb.setPositiveButton("Télécharger",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								log.debug("Lancement du téléchargement de ("
//										+ t411Torrent.getTorrent_ID() + ") "
//										+ t411Torrent.getTorrent_Name());
//	
//								T411GetTorrentFile t411GetTorrentFile = new T411GetTorrentFile(
//										Params.T411_URL_GET_TORRENT, Params.GET,
//										T411DetailActivity.this);
//								t411GetTorrentFile.setId(t411Torrent
//										.getTorrent_ID());
//								t411GetTorrentFile.setName(t411Torrent
//										.getTorrent_Name());
//								t411GetTorrentFile.setFlagResponseFile(true);
//								t411GetTorrentFile.setFlagParamsString(true);
//								T411Controler.requestProcess(t411GetTorrentFile,
//										T411DetailActivity.this);
//	
//								dialog.dismiss();
//							}
//						});
//				adb.setNegativeButton("Retour",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								dialog.dismiss();
//							}
//						});
//				adb.show();
			} else {
				log.error("La réponse n'a pas pu être traitée correctement("
						+ reponse.getClass().getName() + ")\n----------\n"
						+ reponse);
			}
		}
		setRefreshVisible(false);

	}
	
	private void createNotification(Download pDownload){
		log.debug("Creation de la tache de notificatio pour le download "+ pDownload.getId());
		T411DwlNotify t411DwlNotify = new T411DwlNotify(this, pDownload);
		log.trace("Lancement de la tache de notificatio pour le download "+ pDownload.getId());
		t411DwlNotify.execute();
		log.trace("Tache de notification lancee pour le download "+ pDownload.getId());
		
	}
	
//	@Override
//	public void sendMessage(int type, String message){
//		handle.sendMessage(handle.obtainMessage(type, message));
//	}
	
	private void showInfosUser() {

		if (contentFrame != null) {
			contentFrame.setVisibility(View.VISIBLE);
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		setBarSubtitle(prefs.getString("lastDate", "???"));

		TextView hUP24 = (TextView) findViewById(R.id.hUP24);
		hUP24.setText(" "
				+ new BSize(prefs.getString("up24", "0.00 GB")).convert());

		TextView hDL24 = (TextView) findViewById(R.id.hDL24);
		hDL24.setText(" "
				+ new BSize(prefs.getString("dl24", "0.00 GB")).convert());

		TextView hLogin = (TextView) findViewById(R.id.hLogin);
		hLogin.setText(prefs.getString("lastUsername", "???"));

		TextView hUP = (TextView) findViewById(R.id.upload);
		hUP.setText(new BSize(prefs.getString("lastUpload", "0.00 GB"))
				.convert());

		TextView hDOWN = (TextView) findViewById(R.id.download);
		hDOWN.setText(new BSize(prefs.getString("lastDownload", "0.00 GB"))
				.convert());

		TextView hRatio = (TextView) findViewById(R.id.hRatio);
		hRatio.setText(String.format("%.2f",
				Double.valueOf(prefs.getString("lastRatio", "0.00"))));

		hLogin.setTextColor(getResources().getColor(R.color.t411_blue));
		hLogin.setTextColor(new Ratio(this).getTitleColor());

		Log.v("widget t411", "mise à jour du smiley");

		ImageView hSmiley = (ImageView) findViewById(R.id.homeSmiley);
		hSmiley.setImageResource(new Ratio(this).getSmiley());

		ImageView hAvatar = (ImageView) findViewById(R.id.hAvatar);
		hAvatar.setImageResource(R.drawable.avatar);
		// hAvatar.setImageBitmap(new AvatarFactory().getFromPrefs(prefs));

		try {
			String classe = prefs.getString("classe", "???");
			String titre = prefs.getString("titre", "");
			String status = " (" + classe
					+ ((titre.length() > 1) ? ", " + titre : "") + ")";
			TextView wclasse = (TextView) findViewById(R.id.wClasse);
			wclasse.setText(status);
		} catch (Exception e) {
		}
	}

}
