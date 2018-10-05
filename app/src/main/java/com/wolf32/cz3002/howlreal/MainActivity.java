package com.wolf32.cz3002.howlreal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.wolf32.cz3002.howlreal.admin.ReportedNewsFragment;
import com.wolf32.cz3002.howlreal.fragments.NewsFragment;
import com.wolf32.cz3002.howlreal.fragments.SettingsFragment;
import com.wolf32.cz3002.howlreal.model.Admin;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewsFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        ReportedNewsFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private String name;
    private String email;
    private Uri profilePicUrl;
    private Toolbar toolbar;
    private com.wolf32.cz3002.howlreal.model.User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //give permission to perform a networking operation in the main thread
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Admin admin = new Admin();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            profilePicUrl = user.getPhotoUrl();
            String uid = user.getUid();
            Uri mUri = user.getPhotoUrl();
            String uri = "";
            if (mUri != null) {
                uri = mUri.toString();
            }
            boolean emailVerified = user.isEmailVerified();
            mUser = new com.wolf32.cz3002.howlreal.model.User(name, uid, email, uri, emailVerified);
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mUser.isAdmin()) {
            toolbar.setTitle("Reported News");

        } else {
            toolbar.setTitle("General");
        }
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //start general category
        if (mUser.isAdmin()) {
            Log.e(TAG, "onCreate start item 0");
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));

            // hide other menu items
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_entertainment).setVisible(false);

        } else {
            Log.e(TAG, "onCreate start item 1");
            navigationView.getMenu().getItem(1).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(1));

        }

        //receive intent data after logging in
        //mUser = (com.wolf32.cz3002.howlreal.model.User) getIntent().getSerializableExtra("user");


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.e(TAG, "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // display user information on navigation drawer
        TextView textViewName = findViewById(R.id.textView_nav_name);
        TextView textViewEmail = findViewById(R.id.textView_nav_email);
        ImageView imgViewProfilePic = findViewById(R.id.imageView_profile_photo);
        textViewName.setText(name);
        textViewEmail.setText(email);
        Picasso.get().load(profilePicUrl).resize(128, 128).into(imgViewProfilePic);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;
        Bundle args = new Bundle();

        if (!mUser.isAdmin()) {
            Log.e(TAG, "user");
            if (id == R.id.nav_saved_news) {
                // view saved news
                toolbar.setTitle("Saved News");
            } else if (id == R.id.nav_general) {
                // view news fragment
                toolbar.setTitle("General");
                fragmentClass = NewsFragment.class;
                args.putString("type", "general");
            } else if (id == R.id.nav_health) {
                toolbar.setTitle("Health");
                fragmentClass = NewsFragment.class;
                args.putString("type", "health");
            } else if (id == R.id.nav_sports) {
                toolbar.setTitle("Sports");
                fragmentClass = NewsFragment.class;
                args.putString("type", "sports");
            } else if (id == R.id.nav_science) {
                toolbar.setTitle("Science");
                fragmentClass = NewsFragment.class;
                args.putString("type", "science");
            } else if (id == R.id.nav_technology) {
                toolbar.setTitle("Technology");
                fragmentClass = NewsFragment.class;
                args.putString("type", "technology");
            } else if (id == R.id.nav_business) {
                toolbar.setTitle("Business");
                fragmentClass = NewsFragment.class;
                args.putString("type", "business");
            } else if (id == R.id.nav_entertainment) {
                toolbar.setTitle("Entertainment");
                fragmentClass = NewsFragment.class;
                args.putString("type", "entertainment");
            } else if (id == R.id.nav_settings) {
                //change layout
                //change preferences
                fragmentClass = SettingsFragment.class;

            } else if (id == R.id.nav_logout) {
                //logout implementation
                logout(this);

            }
        } else { //is admin account
            Log.e(TAG, "admin");

            if (id == R.id.nav_saved_news) { // change this to smth else
                // view saved news
                toolbar.setTitle("Reported News");
                fragmentClass = ReportedNewsFragment.class;
                args.putString("type", "reportedNews");

            }
            /*else if (id == R.id.nav_general) {
                // view news fragment
                toolbar.setTitle("General");
                fragmentClass = NewsFragment.class;
                args.putString("type", "general");
            } else if (id == R.id.nav_health) {
                toolbar.setTitle("Health");
                fragmentClass = NewsFragment.class;
                args.putString("type", "health");
            } else if (id == R.id.nav_sports) {
                toolbar.setTitle("Sports");
                fragmentClass = NewsFragment.class;
                args.putString("type", "sports");
            } else if (id == R.id.nav_science) {
                toolbar.setTitle("Science");
                fragmentClass = NewsFragment.class;
                args.putString("type", "science");
            } else if (id == R.id.nav_technology) {
                toolbar.setTitle("Technology");
                fragmentClass = NewsFragment.class;
                args.putString("type", "technology");
            } else if (id == R.id.nav_business) {
                toolbar.setTitle("Business");
                fragmentClass = NewsFragment.class;
                args.putString("type", "business");
            } else if (id == R.id.nav_entertainment) {
                toolbar.setTitle("Entertainment");
                fragmentClass = NewsFragment.class;
                args.putString("type", "entertainment");

            } */
            else if (id == R.id.nav_settings) {
                //change layout
                //change preferences
                fragmentClass = SettingsFragment.class;

            } else if (id == R.id.nav_logout) {
                //logout implementation
                logout(this);
            }
        }


        try {
            fragment = (Fragment) Objects.requireNonNull(fragmentClass).newInstance();
            fragment.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e(TAG, "uri: " + uri);
    }

    public void logout(final Context cxt) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(cxt, LoginActivity.class));
                        finish();
                    }
                });
    }
}
