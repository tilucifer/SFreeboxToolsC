package fr.scaron.sfreeboxtools.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import fr.scaron.sfreeboxtools.model.T411GetTorrent;
import fr.scaron.sfreeboxtools.model.T411Search;
import fr.scaron.sfreeboxtools.model.T411Torrent;
import fr.scaron.sfreeboxtools.model.T411Torrrents;
import fr.scaron.sfreeboxtools.model.TinyDB;
import fr.scaron.sfreeboxtools.view.AvatarFactory;
import fr.scaron.sfreeboxtools.view.Ratio;
//import android.widget.*;
import android.support.v4.widget.*;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "tilucifer@gmail.com", customReportContent = {
		ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
		ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
		ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class T411Activity extends AbstractActivity {// implements
													// LoaderCallbacks<Cursor>{
	public static Logger log = LoggerFactory.getLogger(T411Activity.class);

	String id;
	ListView maListViewPerso;
	ArrayList<HashMap<String, String>> listItem;
	SimpleAdapter mTorrentsAdapter;
	String strPrev = null;
	String strNext = null;
	RelativeLayout contentFrame;
	TinyDB myDB;

	T411Search t411Search;

	MenuItem searchMenuItem = null;
	SearchView searchView = null;
	String lastSearch = "";
	// This is the Adapter being used to display the list's data.
	SimpleCursorAdapter mAdapter;
	MatrixCursor mCursor;
	private MenuItem btnPrevItem;

	private MenuItem btnNextItem;

	ArrayList<String> t411Suggestions;

	@Override
	protected void onResume() {
		super.onResume();
		log.info("sort de pause");
	}

	@Override
	public void updateTitle(String title) {
		TextView t411Title = (TextView) findViewById(R.id.t411_title);
		if (t411Title != null) {
			if ("0".equals(title)) {
				t411Title.setVisibility(View.VISIBLE);
				t411Title.setText("Aucun torrent");
			} else {
				t411Title.setVisibility(View.GONE);
			}
			this.setTitle("(" + title + ")");
		}
	}

	@Override
	public void onBackPressed() {
		storeSuggestions();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.removeGroup(R.id.group_seedbox);

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchMenuItem = menu.findItem(R.id.search);
		searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
		// Assumes current activity is the searchable activity
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		setupSearchView();

		// searchMenuItem = menu.findItem(R.id.search);
		// setupSearchView(searchMenuItem);
		refreshItem = menu.findItem(R.id.action_refresh);
		btnPrevItem = menu.findItem(R.id.menu_torrent_prev_page);
		btnNextItem = menu.findItem(R.id.menu_torrent_next_page);
		setRefreshVisible(true);
		return true;

	}

	private void setupSearchView() {

		if (searchView == null) {
			log.error("La barre de recherche n'a pas pu etre initialiser, veuillez quitter l'application");
			log.error("searchMenuItem is null ? " + (searchMenuItem == null));
			return;
		}
		searchView.setIconifiedByDefault(true);
		searchView.setSaveEnabled(true);
		searchView.setQueryHint("Votre recherche ...");
		searchView.setQuery(lastSearch, true); // Avant ct false

		setupSuggestions();

		SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
			@Override
			public boolean onClose() {
				if (!TextUtils.isEmpty(searchView.getQuery())) {
					searchView.setQuery(lastSearch, true);
				}
				return true;
			}
		};
		SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				lastSearch = query;
				t411Search = new T411Search(Params.T411_URL_SEARCH, Params.GET,
						T411Activity.this);
				addSuggestion(query);
				t411Search.setKeywords(query.replace(' ', '+'));
				t411Search.setFlagParamsString(true);
				T411Controler.requestProcess(t411Search, T411Activity.this);
				return true;
			}

		};
		searchView.setOnCloseListener(closeListener);
		searchView.setOnQueryTextListener(queryTextListener);
	}

	private void collapseSearch() {
		if (searchView != null && searchView.isShown()) {
			searchView.onActionViewCollapsed();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}

	}

	private void addSuggestion(String name) {
		String[] temp = new String[2];
		temp[0] = Integer.toString(mCursor.getCount());
		temp[1] = name;

		for (int index = 0; index < mCursor.getCount(); index++) {
			mCursor.moveToPosition(index);
			final String label = mCursor.getString(1);
			if (name.equals(label)) {
				return;
			}
		}
		mCursor.addRow(temp);
		t411Suggestions.add(name);
	}

	private void storeSuggestions() {
		myDB.putList("T411Suggestions", t411Suggestions);
	}

	private void setupSuggestions() {
		myDB = new TinyDB(T411Activity.this);
		t411Suggestions = myDB.getList("T411Suggestions");
		if (t411Suggestions == null) {
			t411Suggestions = new ArrayList<String>();
		}
		String[] columnNames = { "_id", "text" };
		mCursor = new MatrixCursor(columnNames);
		String[] from = { "text" };
		int[] to = { android.R.id.text1 };
		// CursorAdapter ad = new SimpleCursorAdapter(this.getActivity(),
		// android.R.layout.simple_list_item_1, cursor, from, to);

		mAdapter = new SimpleCursorAdapter(T411Activity.this,
				android.R.layout.simple_list_item_1, mCursor, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		//searchView.setSuggestionsAdapter(mAdapter);
		//android.support.v4.widget.CursorAdapter monAdapter = null;
		//searchView.setSuggestionsAdapter(monAdapter);
		SearchView.OnSuggestionListener suggestListener = new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionClick(int id) {
				mCursor.moveToPosition(id);
				final String label = mCursor.getString(1);
				// Toast.makeText(T411Activity.this,"Suggest : "+label,Toast.LENGTH_SHORT);
				searchView.setQuery(label, true);
				return true;
			}

			@Override
			public boolean onSuggestionSelect(int id) {
				return false;
			}
		};
		searchView.setOnSuggestionListener(suggestListener);

		int indexSuggestion = 0;
		for (String t411Suggestion : t411Suggestions) {
			String[] temp = new String[2];
			temp[0] = Integer.toString(indexSuggestion);
			temp[1] = t411Suggestion;
			mCursor.addRow(temp);
		}
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
				T411Controler.requestProcess(t411Search, T411Activity.this);
			}
			return true;
		case R.id.menu_torrent_next_page:
			if (strNext != null && !"".equals(strNext)) {
				setRefreshVisible(true);
				t411Search.setPaginator(strNext);
				t411Search.setFlagParamsString(true);
				T411Controler.requestProcess(t411Search, T411Activity.this);
			}
			return true;
		case R.id.action_refresh:
			setRefreshVisible(true);
			log.info("Vous avez appuy\351 sur la commande d'actualisation donc on actualise");
			// item.setActionView(R.layout.actionbar_indeterminate_progress);
			// TODO : compat
			MenuItemCompat.setActionView(item,
					R.layout.actionbar_indeterminate_progress);
			t411Search.setPaginator(null);
			T411Controler.requestProcess(t411Search, T411Activity.this);
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
		super.init(this);
		super.setProgressIcon(R.drawable.t411_icon_default);
		super.setBarIcon(R.drawable.btn_menu_t411);
		log.info("est en creation");
		AndroidDashboardDesignActivity.addActivity(this);

		if (T411Controler.checkGlobalPrefs(this)) {
			super.showDialogConfig();
			return;
		}
		setRefreshVisible(true);
		setContentView(R.layout.t411_list_layout);

		contentFrame = (RelativeLayout) findViewById(R.id.content_frame);
		if (contentFrame != null) {
			contentFrame.setVisibility(View.GONE);
		}
		maListViewPerso = (ListView) findViewById(R.id.malistviewperso);
		listItem = new ArrayList<HashMap<String, String>>();

		id = getIntent().getStringExtra("id");

		maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				setRefreshVisible(true);
				HashMap<String, String> map = (HashMap<String, String>) maListViewPerso
						.getItemAtPosition(position);

				String torrent_id = map.get("ID");
				String torrent_name = map.get("nomComplet");
				String uploader = map.get("uploader");
				T411GetTorrent t411GetTorrent = new T411GetTorrent(
						Params.T411_URL_GET_PREZ, Params.GET, T411Activity.this);
				String icon_id = map.get("icon");
				if (icon_id != null && !"".equals(icon_id)) {// FIXME TODO BUG
																// T411 ou
																// CHANGEMENT DU
																// SITE ?
					try {
						t411GetTorrent.setIcon(Integer.valueOf(icon_id));
					} catch (Throwable thr) {
						log.info("Icon_id is not int : '" + icon_id + "'");
					}
				}
				t411GetTorrent.setId(torrent_id);
				t411GetTorrent.setName(torrent_name);
				t411GetTorrent.setUploader(uploader);
				t411GetTorrent.setFlagParamsString(true);
				T411Controler.requestProcess(t411GetTorrent, T411Activity.this);

			}
		});
		registerForContextMenu(maListViewPerso);

		mTorrentsAdapter = new SimpleAdapter(getBaseContext(), listItem,
				R.layout.item_torrent, new String[] { "nomComplet", "age",
						"taille", "avis", "seeders", "leechers", "uploader",
						"ratio", "completed", "ratioBase", "icon" }, new int[] {
						R.id.tNom, R.id.tAge, R.id.tTaille, R.id.tComments,
						R.id.tSeeders, R.id.tLeechers, R.id.tUploader,
						R.id.tRatio, R.id.tCompleted, R.id.tRatioBase,
						R.id.tIcon });

		maListViewPerso.setAdapter(mTorrentsAdapter);
		setBtnNextVisible(false);
		setBtnPrevVisible(false);

		int viewKey = 0;

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		if (settings.contains("pref_view_public_torrent")) {
			String viewKeyString = settings.getString(
					"pref_view_public_torrent", "0");
			log.info("Preference pref_view_public_torrent = " + viewKeyString);
			try {
				viewKey = Integer.valueOf(viewKeyString);
			} catch (Exception e) {
			}
		}
		log.info("Preference viewKey = " + viewKey);
		switch (viewKey) {
		case Params.T411_TOP100:
			t411Search = new T411Search(Params.T411_URL_TOP100, Params.GET,
					this);
			break;
		case Params.T411_TOPTODAY:
			t411Search = new T411Search(Params.T411_URL_TOPTODAY, Params.GET,
					this);
			break;
		case Params.T411_TOPWEEK:
			t411Search = new T411Search(Params.T411_URL_TOPWEEK, Params.GET,
					this);
			break;
		case Params.T411_TOPMONTH:
			t411Search = new T411Search(Params.T411_URL_TOPMONTH, Params.GET,
					this);
			break;
		default:
			t411Search = new T411Search(Params.T411_URL_TOP100, Params.GET,
					this);
			break;
		}

		t411Search.setFlagParamFile(false);
		t411Search.setFlagParamsJson(false);
		t411Search.setFlagParamsString(false);
		T411Controler.requestProcess(t411Search, this);
	}

	@Override
	public void updateView(final HttpRaster httpRaster) {
		log.info("updateView");
		Object reponse = "L'op\351ration avec t411 a \351chou\351e sans erreur pr\351cise";

		final TextView t411Title = (TextView) findViewById(R.id.t411_title);
		if (httpRaster instanceof FbxHttpRaster) {
			reponse = "L'op\351ration avec la freebox a \351chou\351e sans erreur pr\351cise";
			reponse = FreeboxControler.processResponse(
					(FbxHttpRaster) httpRaster, this);
			if (reponse instanceof Boolean) {
				if (((FbxHttpRaster) httpRaster).getFinalRequestLabel() == null) {
					// handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
					// "deconnection ? "+reponse));
					finish();
				}
			} else if (reponse instanceof Integer) {
				handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
						"Téléchargement (id=" + reponse
								+ ") ajouté à la freebox"));

				Download download = new Download();
				if (httpRaster instanceof FbxAddFileDownload) {
					FbxAddFileDownload fbxAddFileDownload = (FbxAddFileDownload) httpRaster;
					download.setName(fbxAddFileDownload.getDownload_file_name());
				}
				download.setId((Integer) reponse);
				// //Create notification to show download progress
				// //if notifications flag is on
				// //TODO FIXME
				// Boolean notifActive =
				// myDB.getBoolean("pref_notifactive_torrent");
				// log.info("Notifications actives ? "+notifActive);
				// if (notifActive!=null && notifActive==true){
				// log.info("Notifications actives on cree la notification");
				// createNotification(download);
				// }

			} else if (reponse instanceof String) {
				if (!httpRaster.isFlagResponseError()) {
					if (((FbxHttpRaster) httpRaster).getFinalRequestLabel() == null) {
						handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
								reponse));
					}
				} else {
					log.error("Erreur " + httpRaster.getErrorCode() + " pour "
							+ httpRaster.getClass().getName());
					handle.sendMessage(handle.obtainMessage(Params.MSG_ERR,
							reponse));
				}
			}
		} else {
			reponse = T411Controler.responseProcess(httpRaster, this);

			if (reponse instanceof String) {

				if (httpRaster.isFlagResponseError()) {
					log.error("Erreur " + httpRaster.getErrorCode() + " pour "
							+ httpRaster.getClass().getName());
					handle.sendMessage(handle.obtainMessage(Params.MSG_ERR,
							reponse));
				}
			} else if (reponse instanceof T411Torrrents) {
				handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
						"Affichage de la liste..."));
				listItem.clear();
				T411Torrrents t411Reponse = (T411Torrrents) reponse;
				ArrayList<HashMap<String, String>> map = t411Reponse.getMap();
				showInfosUser();

				if (!map.isEmpty()) {
					collapseSearch();
				}

				strPrev = t411Reponse.getStrPrev();
				if (strPrev != null && !"".equals(strPrev)) {
					setBtnPrevVisible(true);
				} else {
					setBtnPrevVisible(false);
				}
				strNext = t411Reponse.getStrNext();
				if (strNext != null && !"".equals(strNext)) {
					setBtnNextVisible(true);
				} else {
					setBtnNextVisible(false);
				}
				log.trace("Liste de torrents\n----------\n" + map);
				listItem.addAll(map);

				mTorrentsAdapter.notifyDataSetChanged();

				// maListViewPerso.clearChoices();
				// maListViewPerso.setItemChecked(0, true);
				maListViewPerso.setSelection(0);
				updateTitle(String.valueOf(listItem.size()));
			} else if (reponse instanceof ArrayList<?>) {
				listItem.clear();
				ArrayList<HashMap<String, String>> map = (ArrayList<HashMap<String, String>>) reponse;
				log.trace("Liste de torrents\n----------\n" + map);
				listItem.addAll(map);
				mTorrentsAdapter.notifyDataSetChanged();
				// maListViewPerso.clearChoices();

				updateTitle(String.valueOf(listItem.size()));
			} else if (reponse instanceof T411Torrent) {
				final T411Torrent t411Torrent = (T411Torrent) reponse;
				// Launching T411Detail Screen
				Intent i = new Intent(this, T411DetailActivity.class);
				i.putExtra("T411Detail", t411Torrent);
				startActivity(i);
			} else {
				log.error("La réponse n'a pas pu être traitée correctement("
						+ reponse.getClass().getName() + ")\n----------\n"
						+ reponse);
			}
		}
		setRefreshVisible(false);

	}

	// private void createNotification(Download pDownload){
	// log.debug("Creation de la tache de notificatio pour le download "+
	// pDownload.getId());
	// T411DwlNotify t411DwlNotify = new T411DwlNotify(this, pDownload);
	// log.trace("Lancement de la tache de notificatio pour le download "+
	// pDownload.getId());
	// t411DwlNotify.execute();
	// log.trace("Tache de notification lancee pour le download "+
	// pDownload.getId());
	//
	// }

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
		// hAvatar.setImageResource(R.drawable.avatar);
		hAvatar.setImageBitmap(new AvatarFactory().getFromPrefs(prefs));

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
