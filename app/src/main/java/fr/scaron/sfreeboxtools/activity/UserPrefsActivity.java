package fr.scaron.sfreeboxtools.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;

import fr.scaron.sfreeboxtools.R;

/**
 * Created by gregory on 23/10/2013.
 */
public class UserPrefsActivity extends AbstractActivity{//PreferenceActivity {
/*
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //get the new value from Intent data
        SharedPreferences.Editor editor = prefs.edit();

        if (resultCode == Activity.RESULT_OK) {
            editor.commit();
        }
    }
*/    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragement_prefs);

        AndroidDashboardDesignActivity.addActivity(this);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_prefs, new MyPreferenceFragment ())
                .commit();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
////            onCreatePreferenceActivity();
//        } else {
//            onCreatePreferenceFragment();
//        }
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
//    /**
//     * Wraps legacy {@link #onCreate(Bundle)} code for Android < 3 (i.e. API lvl
//     * < 11).
//     */
//    @SuppressWarnings("deprecation")
//    private void onCreatePreferenceActivity() {
//        //addPreferencesFromResource(R.xml.preferences);
//		addPreferencesFromResource(R.xml.settings);
//    }

    /**
     * Wraps {@link #onCreate(Bundle)} code for Android >= 3 (i.e. API lvl >=
     * 11).
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void onCreatePreferenceFragment() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment ())
                .commit();
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) public static class MyPreferenceFragment extends PreferenceFragment
    {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
