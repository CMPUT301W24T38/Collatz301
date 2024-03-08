package com.example.collatzcheckin.admin.controls.images;

import android.content.Context;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;

import com.example.collatzcheckin.admin.controls.images.ImageUriAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

/**
 * This class fetches image URIs and names from Firebase Storage and adds them to ListViews
 */
public class FirebaseImageUriFetcher {

    private Context context;
    private ListView listView;
    private List<Uri> imageUris;
    private List<String> imageNames;
    private ImageUriAdapter adapter;

    /**
     * Constructor for FirebaseImageUriFetcher
     *
     * @param context The context of the activity or fragment
     * @param listView The ListView to populate with image URIs and names
     */
    public FirebaseImageUriFetcher(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;
        this.imageUris = new ArrayList<>();
        this.imageNames = new ArrayList<>();
        this.adapter = new ImageUriAdapter(context, imageUris, imageNames);
        this.listView.setAdapter(adapter);
    }

    /**
     * Fetches image URIs and names from Firebase Storage
     *
     * @param folderPath The path of the folder in Firebase Storage containing the images
     */
    public void fetchImageUrisAndNames(String folderPath) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(folderPath);

        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    for (StorageReference item : task.getResult().getItems()) {
                        final String imageName = item.getName();
                        item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> uriTask) {
                                if (uriTask.isSuccessful()) {
                                    Uri imageUri = uriTask.getResult();
                                    imageUris.add(imageUri);
                                    imageNames.add(imageName);
                                    updateListView();
                                } else {
                                    // Handle failure
                                }
                            }
                        });
                    }
                } else {
                    // Handle errors
                }
            }
        });
    }

    /**
     * Updates the ListView
     */
    private void updateListView() {
        adapter.notifyDataSetChanged();
    }

    /**
     * Currently unused
     * Deletes an image from the list
     *
     * @param position The position of the image in the list to delete
     */
    public void deleteImage(int position) {

        imageUris.remove(position);
        imageNames.remove(position);
        updateListView();
    }
}
