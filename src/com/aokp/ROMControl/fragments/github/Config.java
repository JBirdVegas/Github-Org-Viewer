package com.aokp.ROMControl.fragments.github;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jbird
 * Date: 11/1/12
 * Time: 4:14 PM
 */
public class Config {
    /**
     * used by the applet to track variables
     */
    static class StaticVars {
        public static Date LAST_UPDATE;
        public static boolean REMOVE_AUTHOR_LAYOUT;
        public static boolean REMOVE_COMMITTER_LAYOUT;
        public static String AUTHOR_GRAVATAR_URL;
        public static String COMMITTER_GRAVATAR_URL;
        public static String COMMIT_AUTHOR;
        public static String COMMIT_COMMITTER;
        public static String COMMIT_MESSAGE;
        public static String COMMIT_DATE;
        public static String COMMIT_SHA;
        public static String COMMIT_URL;
        public static String PROJECT;
    }

    public Config() {
        // thats it
    }

    // example of commit list from parser
    // https://api.github.com/repos/aokp/frameworks_base/commits?page=1
    // example of repo list from parser
    // https://api.github.com/orgs/aokp/repos?page=1&per_page=100

    // github json api addresses
    public final String GITHUB_JSON = "https://api.github.com/";
    public final String ORGANIZATION = "AOKP/";
    public final String REPO_URL = GITHUB_JSON + "orgs/" + ORGANIZATION + "repos";
    public final String COMMITS_PAGE = "commits?page="; //later... + PAGE_NUMBER (30 returns by default)
    public final String COMMITS_REQUEST_FORMAT = GITHUB_JSON
        + "repos/" + ORGANIZATION + "%s/" + COMMITS_PAGE + "%s";
    public final String CHANGELOG_JSON = "https://raw.github.com/JBirdVegas/tests/master/example.json";

    // fling speed of scroll
    public static final int DEFAULT_FLING_SPEED = 60;

    // Dialogs (1001+)
    public static final int COMMIT_INFO_DIALOG = 1001;

    public static final boolean JSON_SPEW = false;
}
