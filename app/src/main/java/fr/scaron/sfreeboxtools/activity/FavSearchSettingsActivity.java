package fr.scaron.sfreeboxtools.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    FreeboxBoxAdapter adapter;

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
		super.setBarIcon(R.drawable.freebox_icon);
		setRefreshVisible(true);
        setupList();
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
        int nbFreeboxs = Integer.valueOf(title);
		TextView freeboxTitle = (TextView) findViewById(R.id.freebox_title_box);
//		if ("0".equals(title)||"1".equals(title)) {
        if (nbFreeboxs<0){
			freeboxTitle.setVisibility(View.VISIBLE);
			freeboxTitle.setText("Aucune freebox associ\351e");
            this.setTitle("Freeboxs (0)");
		} else {
			freeboxTitle.setVisibility(View.GONE);
		}
		this.setTitle("Freeboxs (" + title + ")");
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

		ArrayList<String> t411Suggestions = myDB.getList("T411Suggestions");
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
				FavSearch favSearch = new FavSearch(index, t411Suggestion);
				favSearches.add(favSearch);
                index++;
            }
        }

		// 1. get a reference to recyclerView
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.favsearchRecyclerView);

		// 2. set layoutManger
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		// 3. create an adapter
		FavSearchAdapter mAdapter = new FavSearchAdapter(favSearches);
		// 4. set adapter
		recyclerView.setAdapter(mAdapter);
		// 5. set item animator to DefaultAnimator
		recyclerView.setItemAnimator(new DefaultItemAnimator());



		setRefreshVisible(false);
		handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
                    "Liste des favSearches"));
        log.debug("Fin d'Execution de la méthode setupList");
	}
}
