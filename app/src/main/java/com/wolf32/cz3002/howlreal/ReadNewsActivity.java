package com.wolf32.cz3002.howlreal;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.wolf32.cz3002.howlreal.model.BlacklistedNews;
import com.wolf32.cz3002.howlreal.model.News;
import com.wolf32.cz3002.howlreal.model.ReportedNews;
import com.wolf32.cz3002.howlreal.model.User;

import java.util.HashMap;
import java.util.Map;

public class ReadNewsActivity extends AppCompatActivity {

    public static final String TAG = "ReadNewsActivity";
    public DialogInterface.OnClickListener dialogClickListener;
    private News news = null;
    private String userId;
    private boolean isSaved = false;
    private boolean isAdmin;
    private String name;
    private String email;
    private String uri;
    private boolean emailVerified;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            name = user.getDisplayName();
            email = user.getEmail();
            Uri mUri = user.getPhotoUrl();
            if (mUri != null) {
                uri = mUri.toString();
            }
            emailVerified = user.isEmailVerified();
            mUser = new User(name, userId, email, uri, emailVerified);


            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                news = (News) bundle.getSerializable("news");

                if (news == null) {
                    Log.e(TAG, "error: news == null");
                } else {
                    WebView webView = (WebView) findViewById(R.id.webView);
                    webView.setWebViewClient(new WebViewClient()); // dont open using browser
                    webView.loadUrl(news.getUrl());

                    Log.e(TAG, "displayed webview");

                    //get toolbar
                    ActionBar supportActionBar = getSupportActionBar();
                    supportActionBar.setTitle(news.getSourceName().toLowerCase());

                    //set menu item display on/off

                }
            }


        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_news, menu);

        if (mUser.isAdmin()) {
            MenuItem item_sn = menu.findItem(R.id.item_save_news);
            MenuItem item_rn = menu.findItem(R.id.item_report_news);
            item_sn.setVisible(false);
            item_rn.setVisible(false);
        } else {
            MenuItem item_sn = menu.findItem(R.id.item_delete_news);
            item_sn.setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_save_news:
                if (!isSaved) {
                    isSaved = true;
                    item.setIcon(R.drawable.ic_menu_star_black);

                    //save news offline in local storage or defaultsharedpref.


                    Toast.makeText(getApplicationContext(), "Article Saved.", Toast.LENGTH_SHORT).show();
                } else {
                    isSaved = false;
                    item.setIcon(R.drawable.ic_menu_star);

                    //delete saved news

                    Toast.makeText(getApplicationContext(), "Article Unsaved.", Toast.LENGTH_SHORT).show();

                }
                return true;

            case R.id.item_report_news:

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Report this news")
                        .setMessage("Are you sure this is a fake news?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with report news
                                item.setIcon(R.drawable.ic_menu_warning_black);
                                ReportedNews reportedNews = new ReportedNews(news.getNewsId(), userId);
                                reportedNews.addToDatabase();
                                Toast.makeText(getApplicationContext(), "This article has been flagged.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                Log.e(TAG, "no");

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;

            case R.id.item_delete_news:
                AlertDialog.Builder builder2;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder2 = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder2 = new AlertDialog.Builder(this);
                }
                builder2.setTitle("Removing this news")
                        .setMessage("Are you sure this is a fake news?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getApplicationContext(), "This article has been removed.", Toast.LENGTH_SHORT).show();

                                //add to blacklisted news
                                // Access a Cloud Firestore instance from your Activity
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                        .setTimestampsInSnapshotsEnabled(true)
                                        .build();
                                db.setFirestoreSettings(settings);

                                // Update one field, creating the document if it does not already exist.
                                Map<String, Object> data = new HashMap<>();
                                data.put("newsID", news.getNewsId());

                                db.collection("blacklistedNews").document(news.getNewsId())
                                        .set(data);
                                Log.e(TAG, "blacklisted news added to database");

                                Toast.makeText(getApplicationContext(), "This article has been blacklisted.", Toast.LENGTH_SHORT).show();

                                //remove news from reported news


                                //redirect to reported news fragment.
                                //finish();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                Log.e(TAG, "no");

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;

            case R.id.item_push_news:
                item.setIcon(R.drawable.ic_menu_send_white);
                //start push notif
                startService(new Intent(this, NotificationService.class));

                Toast.makeText(getApplicationContext(), "Notification sent.", Toast.LENGTH_SHORT).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
