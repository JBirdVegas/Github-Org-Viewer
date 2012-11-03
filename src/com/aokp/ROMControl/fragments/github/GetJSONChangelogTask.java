package com.aokp.ROMControl.fragments.github;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jbird
 * Date: 11/1/12
 * Time: 5:51 PM
 */
public class GetJSONChangelogTask extends AsyncTask<Void, Void, Void> {
    private final boolean DEBUG = false;
    private final boolean STATIC_DEBUG = true;
    private final String TAG = getClass().getSimpleName();
    private final Context mContext;
    private final PreferenceCategory mCategory;
    private ArrayList<ChangelogObject> mChangelogObjects;

    Config mConfig;

    public GetJSONChangelogTask(Context context, PreferenceCategory category) {
        mContext = context;
        mCategory = category;
        mConfig = new Config();
        mChangelogObjects = new ArrayList<ChangelogObject>(0);
    }
    protected void onPreExecute() {
        // meh...
    }

    protected Void doInBackground(Void... noused) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet requestWebsite = new HttpGet(STATIC_DEBUG
                ? "https://raw.github.com/JBirdVegas/tests/master/example.json"
                : mConfig.CHANGELOG_JSON);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            JSONArray projectCommitsArray = null;
            projectCommitsArray = new JSONArray(httpClient.execute(requestWebsite, responseHandler));

            // debugging
            if (DEBUG)
                Log.d(TAG, "projectCommitsArray.length() is: " + projectCommitsArray.length());
            if (mConfig.JSON_SPEW)
                Log.d(TAG, "projectCommitsArray.toString() is: " + projectCommitsArray.toString());

            final ChangelogObject commitObject = new ChangelogObject(new JSONObject());
            for (int i = 0; i < projectCommitsArray.length(); i++) {
                JSONObject projectsObject =
                    (JSONObject) projectCommitsArray.get(i);
                PreferenceScreen newCommitPreference = mCategory.getPreferenceManager()
                    .createPreferenceScreen(mContext);
                Preference preference = new Preference(mContext);
                commitObject.reParse(projectsObject);
                preference.setTitle(commitObject.getSubject());
                preference.setSummary(commitObject.getBody());
                preference.setSummary(commitObject.getCommitHash());
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent webView = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(commitObject.getUrl()));
                        mContext.startActivity(webView);
                        return false;
                    }
                });
                newCommitPreference.addPreference(preference);
            }
            return null;
        } catch (HttpResponseException httpError) {
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void unused) {

    }
}
