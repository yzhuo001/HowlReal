package com.wolf32.cz3002.howlreal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wolf32.cz3002.howlreal.model.News;
import com.wolf32.cz3002.howlreal.model.ReportedNews;

public class ReadNewsActivity extends AppCompatActivity {

    public static final String TAG = "ReadNewsActivity";
    public DialogInterface.OnClickListener dialogClickListener;
    public News news = null;
    public String userId;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

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

            }
        }



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_save_news:
                if (!isSaved){
                    isSaved = true;
                    item.setIcon(R.drawable.ic_menu_star_black);
                    Toast.makeText(getApplicationContext(), "Article Saved.", Toast.LENGTH_SHORT).show();
                }
                else {
                    isSaved = false;
                    item.setIcon(R.drawable.ic_menu_star);
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
                                Log.e(TAG,"no");

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
