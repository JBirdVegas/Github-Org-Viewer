package com.aokp.ROMControl.fragments.github;

import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: jbird
 * Date: 11/2/12
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangelogObject extends CommitObject {
    /* Constants */

    // changelog constants
    private String TEAM_CREDIT = "team_credit";
    private String PATH = "path";
    private String COMMIT_HASH = "commit_hash";
    private String PARENT_HASHES = "parent_hashes";
    private String AUTHOR_NAME = "author_name";
    private String AUTHOR_DATE = "author_date";
    private String COMMITTER_NAME = "committer_name";
    private String COMMITTER_DATE = "committer_date";
    private String SUBJECT = "subject";
    private String BODY = "body";

    private Config mConfig;

    public ChangelogObject(JSONObject jsonObject) {
        super(jsonObject);
    }

    protected void parseObject(JSONObject jsonObject) {
        mJsonObject = jsonObject;
        mTeamCredit = parseValue(TEAM_CREDIT);
        mPath = parseValue(PATH);
        mCommitHash = parseValue(COMMIT_HASH);
        mUrl = mConfig.GITHUB_JSON + "repos/" + mPath + "/commits/" + mCommitHash;
        mParentHashes = parseValue(PARENT_HASHES);
        mAuthorName = parseValue(AUTHOR_NAME);
        mAuthorDate = parseValue(AUTHOR_DATE);
        mCommitterName = parseValue(COMMITTER_NAME);
        mCommitterDate = parseValue(COMMITTER_DATE);
        mSubject = parseValue(SUBJECT);
        mBody = parseValue(BODY);
    }




}