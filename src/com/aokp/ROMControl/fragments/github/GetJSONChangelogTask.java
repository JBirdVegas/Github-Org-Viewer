package com.aokp.ROMControl.fragments.github;

/*
 * Copyright (C) 2012 The Android Open Kang Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * Displays parsed changelog from our changelog generator
 * located @ ./vendor/aokp/bot/denseChangelog.sh
 */
public class GetJSONChangelogTask extends AsyncTask<Void, Void, Void> {
    private final boolean DEBUG = false;
    private final boolean STATIC_DEBUG = true;
    private final String TAG = getClass().getSimpleName();
    private final Context mContext;
    private final PreferenceCategory mCategory;

    Config mConfig;

    /**
     * parses our changelog from json to Preferences
     * @param context application context
     * @param category container to hold commit views
     */
    public GetJSONChangelogTask(Context context, PreferenceCategory category) {
        mContext = context;
        mCategory = category;
        mConfig = new Config();
    }

    // UI thread
    protected void onPreExecute() {
        // meh...
    }

    // worker thread
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
            if (Config.StaticVars.JSON_SPEW)
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

    // UI thread
    protected void onPostExecute(Void unused) {

    }
}