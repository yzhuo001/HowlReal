package com.wolf32.cz3002.howlreal.admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wolf32.cz3002.howlreal.R;
import com.wolf32.cz3002.howlreal.model.User;

import java.util.ArrayList;
import java.util.List;

public class ManagerAccountsAdapter extends ArrayAdapter<User> {
    private static final String TAG = "ManagerAccountsAdapter";
    private Context mContext;
    private static List<User> userList = new ArrayList<>();


    public ManagerAccountsAdapter(@NonNull Context context, ArrayList<User> list) {
        super(context, 0 , list);
        mContext = context;
        userList = list;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.e(TAG,"getView");

        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.manage_account_adapter, parent,false);

        User currentUser = userList.get(position);

        ImageView iv_profile_photo = listItem.findViewById(R.id.profile_photo);

        if (!currentUser.getUri().isEmpty() && !currentUser.getUri().contains("null") && !currentUser.getUri().equals("null")) {
            Log.e(TAG,"exists: " + currentUser.getUri());
            Picasso.get().load(currentUser.getUri()).resize(120,100).into(iv_profile_photo);
        }

        TextView tv_name = listItem.findViewById(R.id.tv_name);
        tv_name.setText(currentUser.getName());

        TextView tv_email = listItem.findViewById(R.id.tv_email);
        tv_email.setText(currentUser.getEmail());

        TextView tv_score = listItem.findViewById(R.id.tv_score);
        tv_score.setText("5");


        return listItem;
    }
}

