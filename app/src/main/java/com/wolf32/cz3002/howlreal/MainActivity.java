package com.wolf32.cz3002.howlreal;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.wolf32.cz3002.howlreal.fragments.NewsFragment;

import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewsFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private String name;
    private String email;
    private Uri profilePicUrl;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //give permission to perform a networking operation in the main thread
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set on startup start view news item
        Log.e(TAG, "onCreate start item 1");
        navigationView.getMenu().getItem(1).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(1));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Saved News");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            profilePicUrl = user.getPhotoUrl();
        }




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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // display user information on navigation drawer
        TextView textViewName = findViewById(R.id.textView_nav_name);
        TextView textViewEmail = findViewById(R.id.textView_nav_email);
        ImageView imgViewProfilePic = findViewById(R.id.imageView_profile_photo);
        textViewName.setText(name);
        textViewEmail.setText(email);
        Picasso.get().load(profilePicUrl).resize(128,128).into(imgViewProfilePic);

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

        if (id == R.id.nav_saved_news){
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

        } else if (id == R.id.nav_logout) {
            //logout implementation
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e(TAG, "uri: "+uri);
    }

}
