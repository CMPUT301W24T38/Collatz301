package com.example.collatzcheckin.admin.controls.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.User;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying a list of Users
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;

    /**
     * Constructor for UserArrayAdapter
     * @param context The current context
     * @param users The list of users to be displayed
     */
    public UserArrayAdapter(Context context, ArrayList<User> users) {
        super(context,0, users);
        this.users = users;
        this.context = context;
    }

    /**
     * Get a View that displays the data at the specified position in the data set
     * @param position The position of the item within the adapter's data set
     * @param convertView The old view to reuse, if possible
     * @param parent The parent that this view will eventually be attached to
     * @return A View for the data at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent,false);
        }
        User user = users.get(position);

        TextView userName = view.findViewById(R.id.user_name_display);

        userName.setText(user.getName());
        return view;
    }
}
