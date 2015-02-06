package fr.scaron.sfreeboxtools.activity;

import java.util.ArrayList;
import java.util.List;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import fr.scaron.sfreeboxtools.R;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "tilucifer@gmail.com", customReportContent = {
		ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
		ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
		ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class AndroidDashboardDesignActivity  extends AbstractActivity  {
	private static final List<Activity> activities = new ArrayList<Activity>();
	public static Logger log = LoggerFactory.getLogger(AndroidDashboardDesignActivity.class);
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		FreeboxControler.delOldPrefs(this);
		setContentView(R.layout.dashboard_layout);

		/**
		 * Creating all buttons instances
		 * */

		// Dashboard Freebox button
		Button btn_freebox = (Button) findViewById(R.id.btn_freebox);

		// Dashboard T411 button
		Button btn_t411 = (Button) findViewById(R.id.btn_t411);
		
		// Dashboard Friends button
		Button btn_settings = (Button) findViewById(R.id.btn_settings);

		/**
		 * Handling all button click events
		 * */
		// Listening to Freebox button click
		btn_freebox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching Freebox Screen
				Intent i = new Intent(getApplicationContext(),
						FreeboxActivity.class);
				i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});

		// Listening T411 button click
		btn_t411.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching T411 Screen
				Intent i = new Intent(getApplicationContext(),
						T411Activity.class);
				i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
		
		// Listening to Settings button click
		btn_settings.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			// Launching Settings Screen
//			Intent i = new Intent(getApplicationContext(),
//					SettingsActivity.class);
			Intent i = new Intent(getApplicationContext(),
					UserPrefsActivity.class);
			

			i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			}
		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.removeItem(R.id.action_refresh);
        menu.removeItem(R.id.search);
        menu.removeGroup(R.id.group_torrent_navigation);
        menu.removeGroup(R.id.group_seedbox);
        return true;
    }

	@Override
	public void onBackPressed() {
		for (Activity activity : activities) {
			activity.finish();
		}
		finish();
		 
	}
	public static void addActivity(Activity pActivity) {
		int indexExist = -1;
		int index = 0;
		for (Activity activity : activities) {
			if (activity.getClass() == pActivity.getClass()){
				indexExist = index;
				activity.finish();
				continue;
			}
			index++;
		}
		if (indexExist>-1){
			activities.remove(indexExist);
		}
		activities.add(pActivity);
	}

	
}
