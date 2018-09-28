package com.wolf32.cz3002.howlreal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AccountInfoActivity extends AppCompatActivity {

    private static final String TAG = "AccountInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);


        EditText current_pwd = findViewById(R.id.current_pwd);
        current_pwd.setHint("Current Password");
        final EditText new_email = findViewById(R.id.new_email);
        new_email.setHint("New Email");
        final EditText old_pwd = findViewById(R.id.old_pwd);
        old_pwd.setHint("Old Password");
        final EditText new_pwd = findViewById(R.id.new_pwd);
        new_pwd.setHint("New Password");
        final EditText new_name = findViewById(R.id.new_name);
        new_name.setHint("New Username");






        final Button btn_change_pwd = findViewById(R.id.btn_change_pwd);
        btn_change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"btn_change_pwd clicked.");
                changePassword();
            }
        });

        final Button btn_change_email = findViewById(R.id.btn_change_email);
        btn_change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"btn_change_email clicked.");
                changeEmail();
            }
        });

        final Button btn_change_name = findViewById(R.id.btn_change_name);
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"btn_change_name clicked.");
                changeName();

                final FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.getCurrentUser()
                        .reload()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseUser user = auth.getCurrentUser();
                                Log.e(TAG, "name: " + user.getDisplayName());
                                Intent drawerIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(drawerIntent);
                            }
                        });


            }
        });



        // expand buttons
        Button btn_expand_name = findViewById(R.id.btn_name_expand);
        btn_expand_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_name.setVisibility(View.VISIBLE);
                btn_change_name.setVisibility(View.VISIBLE);
            }
        });
        Button btn_expand_password = findViewById(R.id.btn_pwd_expand);
        btn_expand_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_pwd.setVisibility(View.VISIBLE);
                old_pwd.setVisibility(View.VISIBLE);
                btn_change_pwd.setVisibility(View.VISIBLE);
            }
        });
        Button btn_expand_email = findViewById(R.id.btn_email_expand);
        btn_expand_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_email.setVisibility(View.VISIBLE);
                btn_change_email.setVisibility(View.VISIBLE);
            }
        });
    }


    public void changePassword() {
        final FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String email = user.getEmail();
        Log.e(TAG, "email: " + email);

        EditText old_pwd = findViewById(R.id.old_pwd);
        old_pwd.setHint("Old Password");
        String oldPass = old_pwd.getText().toString();
        Log.e(TAG, "oldPass: " + oldPass);
        EditText new_pwd = findViewById(R.id.new_pwd);
        new_pwd.setHint("New Password");
        final String newPass = new_pwd.getText().toString();
        Log.e(TAG, "newPass: " + newPass);

        AuthCredential credential;
        credential = EmailAuthProvider.getCredential(email, oldPass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            View parentLayout = findViewById(R.id.account_info_layout);

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "Authenticate onComplete");
                if(task.isSuccessful()){
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "updatePassword onComplete");

                            if(!task.isSuccessful()){
                                Snackbar snackbar_fail = Snackbar
                                        .make(parentLayout, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                snackbar_fail.show();
                            }else {
                                Snackbar snackbar_su = Snackbar
                                        .make(parentLayout, "Password Successfully Modified", Snackbar.LENGTH_LONG);
                                snackbar_su.show();
                            }
                        }
                    });
                }else {
                    Snackbar snackbar_su = Snackbar
                            .make(parentLayout, "Authentication Failed", Snackbar.LENGTH_LONG);
                    snackbar_su.show();
                }
            }
        });


    }


    public void changeEmail() {
        final FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String email = user.getEmail();
        Log.e(TAG, "email: " + email);

        EditText current_pwd = findViewById(R.id.current_pwd);
        current_pwd.setHint("Current Password");
        String currPass = current_pwd.getText().toString();
        Log.e(TAG, "currPass: " + currPass);
        EditText new_email = findViewById(R.id.new_email);
        new_email.setHint("New Email");
        final String newEmail = new_email.getText().toString();
        Log.e(TAG, "newEmail: " + newEmail);

        AuthCredential credential;
        credential = EmailAuthProvider.getCredential(email, currPass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            View parentLayout = findViewById(R.id.account_info_layout);

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "Authenticate onComplete");
                if(task.isSuccessful()){
                    user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "updateEmail onComplete");

                            if(!task.isSuccessful()){
                                Snackbar snackbar_fail = Snackbar
                                        .make(parentLayout, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                snackbar_fail.show();
                            }else {
                                Snackbar snackbar_su = Snackbar
                                        .make(parentLayout, "Email Successfully Modified", Snackbar.LENGTH_LONG);
                                snackbar_su.show();
                            }
                        }
                    });
                }else {
                    Snackbar snackbar_su = Snackbar
                            .make(parentLayout, "Authentication Failed", Snackbar.LENGTH_LONG);
                    snackbar_su.show();
                }
            }
        });

    }

    public void changeName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        EditText new_name = findViewById(R.id.new_name);
        new_name.setHint("New Username");

        final String newName = new_name.getText().toString();
        Log.e(TAG, "newName: " + newName);

        assert user != null;
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .setPhotoUri(user.getPhotoUrl())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });

    }


}
