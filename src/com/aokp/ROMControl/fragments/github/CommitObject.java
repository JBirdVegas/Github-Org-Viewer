package com.aokp.ROMControl.fragments.github;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: jbird
 * Date: 11/2/12
 * Time: 10:26 PM
 */
public class CommitObject {
    private String DEFAULT = "";
    public JSONObject mJsonObject;
    public String mTeamCredit;
    public String mPath;
    public String mCommitHash;
    public String mUrl;
    public String mParentHashes;
    public String mAuthorName;
    public String mAuthorGravatar;
    public String mAuthorDate;
    public String mCommitterName;
    public String mCommitterGravatar;
    public String mCommitterDate;
    public String mSubject;
    public String mBody;

    public CommitObject(JSONObject jsonObject) {
        parseObject(jsonObject);
    }

    public CommitObject reParse(JSONObject jsonObject) {
        parseObject(jsonObject);
        return this;
    }

    protected void parseObject(JSONObject jsonObject) {
        // you shouldn't be here
    }

    public static String parseToString(JSONArray array, String key) {
        String out = "";
        for (int i = 0; array.length() > i; i++) {
            try {
                out += array.getJSONObject(i).getString(key);
            } catch (JSONException e) {
                return out;
            }
        }
        return out;
    }

    public String parseValue(String queryReference) {
        String str;
        try {
            str = mJsonObject.getString(queryReference);
        } catch (JSONException e) {
            str = DEFAULT;
        }
        return str;
    }

    public String getTeamCredit() {
        return mTeamCredit;
    }

    public String getPath() {
        return mPath;
    }

    public String getCommitHash() {
        return mCommitHash;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getDefault() {
        return DEFAULT;
    }

    public String getParentHashes() {
        return mParentHashes;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public String getAuthorDate() {
        return mAuthorDate;
    }

    public String getCommitterName() {
        return mCommitterName;
    }

    public String getCommitterDate() {
        return mCommitterDate;
    }

    public String getSubject() {
        return mSubject;
    }

    public String getBody() {
        return mBody;
    }

    public String getCommitterGravatar() {
        return mCommitterGravatar;
    }

    public String getAuthorGravatar() {
        return mAuthorGravatar;
    }
}
