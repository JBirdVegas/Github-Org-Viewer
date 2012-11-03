package com.aokp.ROMControl.fragments.github;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: jbird
 * Date: 11/2/12
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class GithubObject extends CommitObject {
    Config mConfig;
    public GithubObject(JSONObject jsonObject) {
        super(jsonObject);
        mConfig = new Config();
    }

    protected void parseObject(JSONObject jsonObject) {
        mJsonObject = jsonObject;
        mTeamCredit = mConfig.ORGANIZATION.substring(0,
            mConfig.ORGANIZATION.length() - 1);
        mUrl = parseValue("url");
        mCommitHash = parseValue("sha");
        // parent hashes
        try {
            mParentHashes = parseToString(mJsonObject.getJSONArray("parents"), "sha");
        } catch (JSONException e) {
            // failed to get parent hashes
        }

        // author && committer from commit JSONObject
        // also subject and body
        try {
            JSONObject commitJson = mJsonObject.getJSONObject("commit");
            // author
            try {
                mAuthorName = commitJson.getJSONObject("author").getString("name");
                mAuthorDate = commitJson.getJSONObject("author").getString("date");
            } catch (JSONException e) {
                // failed to find author name|date
            }

            // committer
            try {
                mCommitterName = commitJson.getJSONObject("committer").getString("name");
                mCommitterDate = commitJson.getJSONObject("committer").getString("date");
            } catch (JSONException e) {
                // failed to find committer|date
            }

            try {
                mBody = commitJson.getString("message");
                mSubject = new StringTokenizer(mBody, "\n\n").nextToken();
            } catch (JSONException e) {
                // failed to find body|subject
            }
        } catch (JSONException e) {
            // failed to get commit object
        }
        // author avatar url
        try {
            mAuthorGravatar =
                mJsonObject.getJSONObject("author")
                    .getString("avatar_url");
        } catch (JSONException e) {
            // failed to find author avatar
        }
        // committer avatar url
        try {
            mCommitterGravatar =
                mJsonObject.getJSONObject("committer")
                    .getString("avatar_url");
        } catch (JSONException e) {
            // failed to find committer
        }
        // path
        StringTokenizer pathChunks = new StringTokenizer(mUrl, "/");
        while (pathChunks.hasMoreTokens()) {
            String str = pathChunks.nextToken();
            if (str.equals(new String(mConfig.ORGANIZATION))) {
                mPath = pathChunks.nextToken();
            }
        }
    }
}
