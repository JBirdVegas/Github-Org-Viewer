package com.aokp.ROMControl.fragments.github;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;

public class GithubViewer extends PreferenceFragment {
    private static final boolean DEBUG = true;
    private static final boolean JSON_SPEW = true;
    private static final String TAG = "DynamicChangelog";

    // classwide constants
    private static final String PREF_CAT = "dynamic_changelog";

    // Dialogs (1001+)
    private static final int COMMIT_INFO_DIALOG = 1001;
    private Context mContext;
    private static PreferenceCategory mCategory;

    // Menu item ids (101+)
    private static final int MENU_ID_PACKAGES = 101;
    private static final int MENU_ID_COMMITLOG = 102;
    private boolean ARE_IN_PROJECT_PATH;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.dynamic_changelog);
        mContext = getActivity().getApplicationContext();
        mCategory = (PreferenceCategory) findPreference(PREF_CAT);
        // important to set ordering before we populate screen
        mCategory.setOrderingAsAdded(false);
        mCategory.setTitle(getString(R.string.dynamic_changelog_cat_title));
        if (savedInstanceState == null) {
            new DisplayProjectsList(mContext, mCategory).execute();
        }
        ARE_IN_PROJECT_PATH = true;
        setHasOptionsMenu(true);
    }

    // this is the only method called right before every display of the menu
    // here we choose what dynamic content to display for the menu
    public void onPrepareOptionsMenu(Menu menu) {
        // remove old menu items
        menu.clear();

        // cant change branch if we are not viewing a project folder's commits
        if (ARE_IN_PROJECT_PATH)
            menu.add(0, MENU_ID_COMMITLOG, 0, getString(R.string.changelog_menu_commitlog_title));
        else
            menu.add(0, MENU_ID_PACKAGES, 0, getString(R.string.changelog_menu_projects_title));
    }

    // XXX remove if not required to bring menu into view XXX
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // pass the method on we don't need it our work was
        // done in onPrepareOptionsMenu(Menu)
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * handle Menu onClick actions
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case MENU_ID_PACKAGES:
                new DisplayProjectsList(mContext, mCategory).execute();
                // reset menu tracker variable
                ARE_IN_PROJECT_PATH = true;
                return true;
            case MENU_ID_COMMITLOG:
                new GetJSONChangelogTask(mContext, mCategory).execute();
                ARE_IN_PROJECT_PATH = false;
                return true;

            // This should never happen but just in case let the system handle the return
            default:
                return super.onContextItemSelected(item);
        }
    }
}
