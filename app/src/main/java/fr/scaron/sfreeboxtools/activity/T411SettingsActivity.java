package fr.scaron.sfreeboxtools.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import fr.scaron.sfreeboxtools.R;

/**
 * Created by Sébastien on 08/02/2015.
 */
public class T411SettingsActivity  extends AbstractActivity {

    @Override
    public void updateTitle(String title) {
        this.setTitle(R.string.pref_header_torrent);
        setRefreshVisible(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragement_prefs);

        AndroidDashboardDesignActivity.addActivity(this);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_prefs, new MyPreferenceFragment ())
                .commit();
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
            addPreferencesFromResource(R.xml.t411settings);
        }
    }
}
