package fr.scaron.sfreeboxtools.activity;

import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import fr.scaron.sfreeboxtools.*;
import fr.scaron.sfreeboxtools.contexte.*;
import fr.scaron.sfreeboxtools.control.*;
import fr.scaron.sfreeboxtools.model.*;
import java.util.*;
import org.acra.*;
import org.acra.annotation.*;
import org.slf4j.*;

//import android.support.v7.widget.Toolbar;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "tilucifer@gmail.com", customReportContent = {
		ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
		ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
		ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class FreeboxListBoxActivity extends AbstractActivity
{

	private boolean isPaused;
    private TinyDB myDB;

	public static Logger log = LoggerFactory.getLogger(FreeboxListBoxActivity.class);
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
		setContentView(R.layout.freebox_list_box);
		/*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }*/
		super.setBarIcon(R.drawable.freebox_icon);
		
		setRefreshVisible(true);
		final TextView freeboxTitle = (TextView) findViewById(R.id.freebox_title_box);
		freeboxTitle.setText("Initialisation en cours..");
		freeboxTitle.setVisibility(View.GONE);
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
		TextView freeboxTitle = (TextView) findViewById(R.id.freebox_title_box);
		if ("0".equals(title)) {
			freeboxTitle.setVisibility(View.VISIBLE);
			freeboxTitle.setText("Aucune freebox associ\351e");
		} else {
			freeboxTitle.setVisibility(View.GONE);
		}
		this.setTitle("(" + title + ")");
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


	@Override
	public void updateView(final HttpRaster httpRaster) {
		log.info("updateView for response of request "+httpRaster.getClass().getName());
		Object reponse = "L'op\351ration avec la freebox a \351chou\351e sans erreur pr\351cise";
		reponse = FreeboxControler.processResponse((FbxHttpRaster) httpRaster, this);
		if (reponse instanceof String) {
			if (((FbxHttpRaster) httpRaster).getFinalRequestLabel()==null){
				if (httpRaster.isFlagResponseError()) {
					log.error("Erreur "+httpRaster.getErrorCode()+" pour "+httpRaster.getClass().getName());
					handle.sendMessage(handle.obtainMessage(Params.MSG_ERR, reponse));
				}else{
					handle.sendMessage(handle.obtainMessage(Params.MSG_CNF, reponse));
				}
			}
		} else if (reponse instanceof FreeboxBox) {
			setRefreshVisible(false);
			String freeboxName = myDB.getString("FreeboxNameTmp");
			FreeboxBox freeboxBox = (FreeboxBox)reponse;
			freeboxBox.setName(freeboxName);
			freeboxBox.setSelected(true);//TODO
			String app_token = myDB.getString("app_token");
			freeboxBox.setApp_token(app_token);
			handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
						"Ajout de la freebox "+freeboxName+"("+freeboxBox.getIp_public()+":"+freeboxBox.getPort()+") effectué"));
			addToList(freeboxBox);//TODO
			
			//adapter.addFreeboxBox(freeboxBox);//TODO
            //adapter.setNotifyOnChange(true);
		}
	}

	private void addToList(FreeboxBox freebox){
        log.debug("Execution de la méthode addToList");
        myDB = new TinyDB(FreeboxListBoxActivity.this);
        ArrayList<String> boxNames = myDB.getList("FreeboxBoxNames");
        ArrayList<String> boxIps = myDB.getList("FreeboxBoxIps");
        ArrayList<String> boxPorts = myDB.getList("FreeboxBoxPorts");
        ArrayList<String> boxTokens = myDB.getList("FreeboxBoxTokens");
        ArrayList<Boolean> boxSelecteds = myDB.getListBoolean("FreeboxBoxSelecteds");



        log.info("listebbox null ? : "+(boxNames==null));
        if (boxNames !=null) {
            log.info("nb de box trouvees dans addtolist: " + boxNames.size());
            int index = boxNames.size();
			for(int i=0; i<index;i++){
				boxSelecteds.set(i,false);
			}
			log.info("ajout de la freebox : '"+freebox.getName()+"' ('"+freebox.getIp_public()+"':'"+freebox.getPort()+"'\t'"+freebox.getApp_token()+"')");
			boxNames.add(freebox.getName());
			boxIps.add(freebox.getIp_public());
			boxPorts.add(freebox.getPort());
			boxTokens.add(freebox.getApp_token());

			boxSelecteds.add(true);
			myDB.putList("FreeboxBoxNames", boxNames);
            myDB.putList("FreeboxBoxIps", boxIps);
            myDB.putList("FreeboxBoxPorts", boxPorts);
            myDB.putList("FreeboxBoxTokens", boxTokens);
            myDB.putListBoolean("FreeboxBoxSelecteds", boxSelecteds);
			
        }
		setupList();
        log.debug("Fin d'Execution de la méthode addToList");
	}

    private void setupList(){
        log.debug("Execution de la méthode setupList");
        myDB = new TinyDB(FreeboxListBoxActivity.this);
        ArrayList<String> boxNames = myDB.getList("FreeboxBoxNames");
        ArrayList<String> boxIps = myDB.getList("FreeboxBoxIps");
        ArrayList<String> boxPorts = myDB.getList("FreeboxBoxPorts");
        ArrayList<String> boxTokens = myDB.getList("FreeboxBoxTokens");
        ArrayList<Boolean> boxSelecteds = myDB.getListBoolean("FreeboxBoxSelecteds");

        List<FreeboxBox> freeboxBoxes = new ArrayList<FreeboxBox>();

        log.info("listebbox null ? : "+(boxNames==null));
        if (boxNames !=null) {
            log.info("nb de box trouvees: " + boxNames.size());
            int index = 0;
            for (String boxName : boxNames) {
                FreeboxBox box = new FreeboxBox();
                box.setName(boxName);
                box.setIp_public(boxIps.get(index));
                box.setApp_token(boxTokens.get(index));
                box.setPort(boxPorts.get(index));
                box.setSelected(boxSelecteds.get(index));
                box.setIndex(index);
                freeboxBoxes.add(box);
                index++;
            }
        }
        ListView freeboxBoxList = (ListView) findViewById(R.id.freebox_list_box);
        if (adapter == null) {

            adapter = new FreeboxBoxAdapter(this,
                    R.layout.freebox_list_box_item,
                    freeboxBoxes);
            freeboxBoxList.setAdapter(adapter);
            //adapter.setNotifyOnChange(true);
            //freeboxBoxList.setSelection(0);
        } else {
            adapter.setFreeboxBoxs(freeboxBoxes);
            adapter.setNotifyOnChange(true);
            //freeboxBoxList.setSelection(0);
        }

		setRefreshVisible(false);
		handle.sendMessage(handle.obtainMessage(Params.MSG_CNF,
                    "Liste des freebox"));
        log.debug("Fin d'Execution de la méthode setupList");
	}
}
