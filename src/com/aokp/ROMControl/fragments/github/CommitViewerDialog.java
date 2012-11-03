package com.aokp.ROMControl.fragments.github;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: jbird
 * Date: 11/1/12
 * Time: 4:13 PM
 */
public class CommitViewerDialog extends AlertDialog {
    private final Context mContext;
    private final Config mConfig;

    private AlertDialog mDialog;
    /**
     * Create a Dialog window that uses the default dialog frame style.
     *
     * @param context The Context the Dialog is to run it.  In particular, it
     * uses the window manager and theme in this context to
     * present its UI.
     */
    public CommitViewerDialog(Context context) {
        super(context);
        mConfig = new Config();
        mContext = context;

        // get service and inflate our dialog
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View commitExtendedInfoLayout = inflater.inflate(R.layout.extended_commit_info_layout, null);

        // references for our objects
        ScrollView scroller = (ScrollView) commitExtendedInfoLayout.findViewById
            (R.id.extended_commit_info_layout_scrollview);
        // so we scroll smoothly if commit message is large
        scroller.setSmoothScrollingEnabled(true);
        scroller.fling(Config.DEFAULT_FLING_SPEED);

        // gain object references
        LinearLayout authorContainer = (LinearLayout) commitExtendedInfoLayout.findViewById
            (R.id.author_container);
        LinearLayout committerContainer = (LinearLayout) commitExtendedInfoLayout.findViewById
            (R.id.committer_container);
        ImageView authorAvatar = (ImageView) commitExtendedInfoLayout.findViewById
            (R.id.author_avatar);
        ImageView committerAvatar = (ImageView) commitExtendedInfoLayout.findViewById
            (R.id.committer_avatar);
        TextView author_header = (TextView) commitExtendedInfoLayout.findViewById
            (R.id.author_header);
        TextView committer_header = (TextView) commitExtendedInfoLayout.findViewById
            (R.id.committer_header);
        TextView author_tv = (TextView) commitExtendedInfoLayout.findViewById
            (R.id.commit_author);
        TextView committer_tv = (TextView) commitExtendedInfoLayout.findViewById
            (R.id.commit_committer);
        TextView message_tv = (TextView) commitExtendedInfoLayout.findViewById
            (R.id.commit_message);
        TextView date_tv = (TextView) commitExtendedInfoLayout.findViewById
            (R.id.commit_date);
        TextView sha_tv = (TextView) commitExtendedInfoLayout.findViewById
            (R.id.commit_sha);

        // remove any LinearLayouts we don't have values for
        if (Config.StaticVars.REMOVE_AUTHOR_LAYOUT) {
            authorContainer.setVisibility(View.GONE);
            author_header.setVisibility(View.GONE);
            // reset our watcher
            Config.StaticVars.REMOVE_AUTHOR_LAYOUT = false;
        } else {
            // since we have this value we don't hide and we load our images
            // this way we don't waste bandwidth loading legacy values
            if (Config.StaticVars.AUTHOR_GRAVATAR_URL != null)
                new DownloadImageTask(authorAvatar).execute(Config.StaticVars.AUTHOR_GRAVATAR_URL);
            else
                // this is important because if we have null value
                // it won't fail till too late for us to use responsibly (sp? ...fuck it I don't care)
                Config.StaticVars.REMOVE_AUTHOR_LAYOUT = true;
        }

        if (Config.StaticVars.REMOVE_COMMITTER_LAYOUT) {
            committerContainer.setVisibility(View.GONE);
            committer_header.setVisibility(View.GONE);
            // reset our watcher
            Config.StaticVars.REMOVE_COMMITTER_LAYOUT = false;
        } else {
            // try to populate the image from gravatar
            // @link http://stackoverflow.com/a/9288544
            if (Config.StaticVars.COMMITTER_GRAVATAR_URL != null)
                new DownloadImageTask(committerAvatar).execute(Config.StaticVars.COMMITTER_GRAVATAR_URL);
            else
                Config.StaticVars.REMOVE_COMMITTER_LAYOUT = true;
        }

        // setText for TextViews
        author_tv.setText(Config.StaticVars.COMMIT_AUTHOR);
        committer_tv.setText(Config.StaticVars.COMMIT_COMMITTER);
        message_tv.setText(Config.StaticVars.COMMIT_MESSAGE);
        date_tv.setText(Config.StaticVars.COMMIT_DATE);

        // we split the sha-1 hash into two strings because
        // it looks horrible by default display and smaller
        // size text is hard to read
        int halfHashLength = Config.StaticVars.COMMIT_SHA.length() / 2;
        StringBuilder splitHash = new StringBuilder();
        splitHash.append(Config.StaticVars.COMMIT_SHA.substring(0, halfHashLength));
        splitHash.append("-\n"); // to seperate the strings
        splitHash.append(Config.StaticVars.COMMIT_SHA.substring(halfHashLength));
        splitHash.trimToSize();
        // set the text from our StringBuilder
        sha_tv.setText(splitHash.toString());

        // make a builder to helps construct our dialog
        this.setTitle(mContext.getString(R.string.commit_extended_info_title));

        // the order we place the buttons in is important
        // standard is:			| CANCEL | OK |
        // per our needs we use:	| CLOSE | WEBVIEW |
        mDialog.setButton(0, mContext.getString(R.string.button_close), new OnClickListener() {
            public void onClick(DialogInterface d, int button) {
                // just let the dialog go
            }
        });
        this.setButton(1, mContext.getString(R.string.button_webview), new OnClickListener() {
            public void onClick(DialogInterface d, int button) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                String webviewUrl = "https://github.com/" + mConfig.ORGANIZATION + Config.StaticVars.PROJECT + "/commit/" + Config.StaticVars.COMMIT_SHA;
                i.setData(Uri.parse(webviewUrl));
                mContext.startActivity(i);
            }
        });

        show();
    }
}
