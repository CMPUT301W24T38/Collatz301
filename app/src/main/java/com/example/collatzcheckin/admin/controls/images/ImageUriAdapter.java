package com.example.collatzcheckin.admin.controls.images;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * ArrayAdapter subclass responsible for mapping a list of image URIs to a ListView,
 * displaying corresponding image names as text in each list item
 */
public class ImageUriAdapter extends ArrayAdapter<Uri> {

    private List<String> imageNames;

    /**
     * Constructs a new ImageUriAdapter instance
     * @param context The current context
     * @param uris The list of image URIs to be displayed
     * @param names The list of image names corresponding to the URIs
     */
    public ImageUriAdapter(Context context, List<Uri> uris, List<String> names) {
        super(context, 0, uris);
        this.imageNames = names;
    }

    /**
     * Get a View that displays the data at the specified position in the data set
     * @param position The position of the item within the adapter's data set
     * @param convertView The old view to reuse, if possible
     * @param parent The parent that this view will be attached to
     * @return A View corresponding to the data at the position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = listItemView.findViewById(android.R.id.text1);
        Uri currentUri = getItem(position);
        if (currentUri != null) {
            String imageName = imageNames.get(position); // Get the image name corresponding to the URI
            textView.setText(imageName); // Set the image name as the text
            listItemView.setTag(currentUri); // Set the URI as the tag of the view
        }

        return listItemView;
    }
}
