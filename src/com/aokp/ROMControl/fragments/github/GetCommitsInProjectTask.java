package com.aokp.ROMControl.fragments.github;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jbird
 * Date: 11/1/12
 * Time: 4:25 PM
 */
public class GetCommitsInProjectTask extends AsyncTask<Void, Void, Void> {
    private final String TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private final PreferenceCategory mCategory;
    private final Context mContext;
    private final Config mConfig;

    private Changelog mChangelog;

    int PAGE_ = - 1;
    String BRANCH_;
    String PROJECT_;

    public GetCommitsInProjectTask(Context context, PreferenceCategory preferenceCategory) {
        mContext = context;
        mCategory = preferenceCategory;
        mChangelog = new Changelog();
        mConfig = new Config();
    }
    // inner class constants
    final String DEFAULT_BRANCH = "jb"; // TODO find a way to handle 'jellybean' branches
                                        // at the same time


    protected void onPreExecute() {
        // show commit after we load next set
        mCategory.setTitle(mContext.getString(R.string.loading_commits));
        if (PAGE_ <= 1)
            mCategory.removeAll();
    }

    protected Void doInBackground(Void... unused) {
        // so we don't acidentally crash the ui
        if (PROJECT_ == null || PAGE_ == - 1)
            return null;

        // TODO: deal with branches later
        String requestCommits = String.format(mConfig.COMMITS_REQUEST_FORMAT, PROJECT_, PAGE_);

        if (BRANCH_ == null) BRANCH_ = DEFAULT_BRANCH;
        try {
            HttpClient httpClient = new DefaultHttpClient();

            Log.i(TAG, "attempting to connect to: " + requestCommits);
            HttpGet requestWebsite = new HttpGet(requestCommits);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            JSONArray projectCommitsArray = new JSONArray(httpClient.execute(requestWebsite, responseHandler));

            // debugging
            if (DEBUG)
                Log.d(TAG, "projectCommitsArray.length() is: " + projectCommitsArray.length());
            if (mConfig.JSON_SPEW)
                Log.d(TAG, "projectCommitsArray.toString() is: " + projectCommitsArray.toString());

            // make a PreferenceScreen for all commits in package
            for (int i = 0; i < projectCommitsArray.length(); i++) {
                PreferenceScreen mCommit = mCategory.getPreferenceManager().createPreferenceScreen(mContext);
                // make an object of each commit
                JSONObject projectsObject = (JSONObject) projectCommitsArray.get(i);

                // some fields are just plain strings we can parse
                final String commitSsh = projectsObject.getString("sha"); // for setKey
                final String commitWebPath = projectsObject.getString("url"); // JSON commit path

                // author could possible be null so use a try block to prevent failures
                // (merges have committers not authors, authors exist for the parent commits)
                try {
                    // this is slightly different as we have many values for fields
                    // therefor each of these fields will be an object to itself (for each commit)
                    // author; committer; parents and commit
                    JSONObject authorObject = (JSONObject) projectsObject.getJSONObject("author");
                    JSONObject commitObject = (JSONObject) projectsObject.getJSONObject("commit");
                    if (mConfig.JSON_SPEW)
                        Log.d(TAG, "authorObject: " + authorObject.toString());

                    // pull needed info from our new objects (for each commit)
                    final String authorName = authorObject.getString("login"); // github screen name
                    final String authorAvatar = authorObject.getString("avatar_url"); // author's avatar url
                    final String commitMessage = commitObject.getString("message"); // commit message

                    // to grab the date we need to make a new object from
                    // the commit object and collect info from there
                    JSONObject innerAuthorObject = (JSONObject) commitObject.getJSONObject("author");
                    JSONObject innerCommitterObject = (JSONObject) projectsObject.getJSONObject("committer");
                    final String commitDate = innerAuthorObject.getString("date"); // commit date
                    final String committerAvatar = innerCommitterObject.getString("avatar_url");
                    final String committerName = innerCommitterObject.getString("login");

                    // apply info to our preference screen
                    mCommit.setKey(commitSsh + "");
                    mCommit.setTitle(commitMessage);
                    mCommit.setSummary(authorName);
                    mCommit.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference p) {
                            // set variables
                            Config.StaticVars.AUTHOR_GRAVATAR_URL = authorAvatar;
                            Config.StaticVars.COMMITTER_GRAVATAR_URL = committerAvatar;
                            Config.StaticVars.COMMIT_COMMITTER = committerName;
                            Config.StaticVars.PROJECT = PROJECT_;
                            Config.StaticVars.COMMIT_URL = commitWebPath;
                            Config.StaticVars.COMMIT_AUTHOR = authorName;
                            Config.StaticVars.COMMIT_MESSAGE = commitMessage;
                            Config.StaticVars.COMMIT_DATE = commitDate;
                            Config.StaticVars.COMMIT_SHA = commitSsh + "";

                            // launch dialog
                            Intent intent = new Intent();
                            intent.setClass(mContext, CommitViewerDialog.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            return true;
                        }
                    });

                    mCategory.addPreference(mCommit);
                } catch (JSONException je) {
                    // no author found for commit
                    if (DEBUG) Log.d(TAG, "encountered a null value", je);
                }
            }
            // append next 30 commits onClick()
            final PreferenceScreen mNext = mCategory.getPreferenceManager().createPreferenceScreen(mContext);
            mNext.setTitle(mContext.getString(R.string.next_commits_page_title));
            mNext.setSummary(mContext.getString(R.string.next_commits_page_summary));
            mNext.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference p) {
                    GetCommitsInProjectTask nextList = new GetCommitsInProjectTask(mContext, mCategory);
                    nextList.PAGE_ = PAGE_ + 1; // next page of commits (30)
                    nextList.PROJECT_ = PROJECT_; // stay in same project folder
                    nextList.execute();
                    mCategory.removePreference(mNext); // don't keep in list after we click
                    return true;
                }
            });
            // avoid adding if we don't have commits, prob network fail :-/
            if (mCategory.getPreferenceCount() > 1)
                mCategory.addPreference(mNext);
        } catch (JSONException je) {
            if (DEBUG) Log.e(TAG, "Bad json interaction...", je);
        } catch (IOException ioe) {
            if (DEBUG) Log.e(TAG, "IOException...", ioe);
        } catch (NullPointerException ne) {
            if (DEBUG) Log.e(TAG, "NullPointer...", ne);
        }
        return null;
    }

    protected void onPostExecute(Void unused) {
        mCategory.setTitle(mContext.getString(R.string.commits_title));
    }
}