package fr.scaron.sfreeboxtools.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.model.FavSearch;
import fr.scaron.sfreeboxtools.model.FavSearchAdapter;
import fr.scaron.sfreeboxtools.model.FreeboxBox;
import fr.scaron.sfreeboxtools.model.FreeboxBoxAdapter;
import fr.scaron.sfreeboxtools.model.TinyDB;

//import android.support.v7.widget.Toolbar;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "tilucifer@gmail.com", customReportContent = {
		ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
		ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
		ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class FavSearchSettingsActivity extends AbstractActivity
{

	private boolean isPaused;
    private TinyDB myDB;

	public static Logger log = LoggerFactory.getLogger(FavSearchSettingsActivity.class);
	RecyclerView recyclerView;
	FavSearchAdapter mAdapter;
	FloatingActionButton favsearchAddToList;

	@Override
	protected void onPause() {
		super.onPause();
		log.info("est en pause");
		log.info("on met le flag isPaused à true");
		isPaused = true;
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.info("sort de pause");
		log.info("on met le flag isPaused à false");
		isPaused = false;

        setRefreshVisible(false);

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.init(this);
		AndroidDashboardDesignActivity.addActivity(this);
		log.info("est en creation");
		setContentView(R.layout.favsearch_list_layout);
		super.setBarIcon(android.R.drawable.ic_menu_search);
		favsearchAddToList = (FloatingActionButton)findViewById(R.id.favsearchAddToList);
		favsearchAddToList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching Adding Favs dialog
				showAddFavDialog();
			}
		});
		setRefreshVisible(true);
        setupList();
		showInfoFavDialog();
	}

	private void showInfoFavDialog(){
		boolean favSearchInfoNotified = myDB.getBoolean("FavSearchInfoNotified");

		if (!favSearchInfoNotified) {
			final View alertDialogView = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.favsearch_list_delete, null);

			TextView tv = (TextView) alertDialogView.findViewById(R.id.favsearchListDelete_title);
			tv.setText("Swipez à gauche pour supprimer, à droite pour modifier !");
			//Création de l'AlertDialog
			AlertDialog.Builder adb = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

			//On affecte la vue personnalisé que l'on a crée à notre AlertDialog
			adb.setView(alertDialogView);

			//On donne un titre à l'AlertDialog
			adb.setTitle("Information !");

			//On modifie l'icône de l'AlertDialog pour le fun ;)
			adb.setIcon(android.R.drawable.ic_dialog_info);

			//On affecte un bouton "OK" à notre AlertDialog et on lui affecte un évènement
			adb.setPositiveButton("J'ai compris !", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					myDB.putBoolean("FavSearchInfoNotified", true);

				}
			});
			adb.show();
		}
	}

	private void showAddFavDialog(){

		final View alertDialogView = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.favsearch_list_addmodify, null);

		//Création de l'AlertDialog
		AlertDialog.Builder adb = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

		//On affecte la vue personnalisé que l'on a crée à notre AlertDialog
		adb.setView(alertDialogView);

		//On donne un titre à l'AlertDialog
		adb.setTitle("Ajouter un favori ?");

		//On modifie l'icône de l'AlertDialog pour le fun ;)
		adb.setIcon(android.R.drawable.ic_dialog_alert);

		//On affecte un bouton "OK" à notre AlertDialog et on lui affecte un évènement
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				//Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
				EditText et = (EditText) alertDialogView.findViewById(R.id.favsearchListAddModify_name);
				mAdapter.getItemsData().add(new FavSearch(mAdapter.getItemsData().size(), et.getText().toString()));
				mAdapter.sayDataSetChanged();

			}
		});

		//On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
		adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//notifyItemChanged(itemPosition);
				mAdapter.sayDataSetChanged();
				//Lorsque l'on cliquera sur annuler on quittera l'application
				dialog.dismiss();
			}
		});
		adb.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.removeGroup(R.id.group_torrent_navigation);
        menu.removeGroup(R.id.group_seedbox);
        menu.removeItem(R.id.action_refresh);
        menu.removeItem(R.id.search);
		setRefreshVisible(true);
		return true;

	}

	@Override
	public void updateTitle(String title) {
		this.setTitle("Favoris (" + title + ")");
		setRefreshVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		log.trace("item selectionné de l'actionbar : " + item.getItemId()
				+ " | title " + item.getTitle());
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_refresh:
            setupList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addToList(FreeboxBox freebox){
        log.debug("Execution de la méthode addToList");
        myDB = new TinyDB(FavSearchSettingsActivity.this);

        log.debug("Fin d'Execution de la méthode addToList");
	}

    private void setupList(){
        log.debug("Execution de la méthode setupList");
        myDB = new TinyDB(FavSearchSettingsActivity.this);

		/*ArrayList<String> t411Suggestions = myDB.getList("T411Suggestions");
		if (t411Suggestions == null) {
			t411Suggestions = new ArrayList<String>();
		}
        List<FavSearch> favSearches = new ArrayList<FavSearch>();

        log.info("t411Suggestions null ? : "+(t411Suggestions==null));
        if (t411Suggestions !=null) {
            log.info("nb de favsearch trouvees: " + t411Suggestions.size());
            int index = 0;

			// this is data fro recycler view
            for (String t411Suggestion : t411Suggestions) {
				FavSearch favSearch = new FavSearch(index+1, t411Suggestion);
				log.info("favsearch n°"+index+" : " + t411Suggestion);
				favSearches.add(favSearch);
                index++;
            }
        }*/

		// 1. get a reference to recyclerView
		recyclerView = (RecyclerView) findViewById(R.id.favsearchRecyclerView);
		// 2. create an adapter
		mAdapter = new FavSearchAdapter(this, recyclerView); //, favSearches
		// 3. set layoutManger
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		// 4. set item animator to DefaultAnimator
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		// 5. set adapter
		recyclerView.setAdapter(mAdapter);
		// 6. notify adapter with new datas
		mAdapter.notifyDataSetChanged();


		setRefreshVisible(false);
		handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
                    "Liste des favSearches"));
        log.debug("Fin d'Execution de la méthode setupList");
	}
}
