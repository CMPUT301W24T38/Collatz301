package com.example.collatzcheckin.admin.controls.images;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.collatzcheckin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Fragment responsible for displaying an image with options to delete or navigate back.
 */
public class ImageDisplayFragment extends Fragment { // The listview does not currently update after deletion

    /**
     * Inflates the layout for this fragment and sets up the UI components
     * Retrieves the image URI from the fragment arguments and displays the image
     * Handles button clicks for delete and back actions
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState A Bundle containing the saved state of the fragment
     * @return The View of the inflated layout for the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image_display, container, false);

        // Retrieve the image URI from arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("imageUri")) {
            String imageUriString = args.getString("imageUri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                ImageView imageView = root.findViewById(R.id.imageView);
                Glide.with(requireContext())
                        .load(imageUri)
                        .placeholder(R.drawable.bg) // Placeholder image
                        .into(imageView);
            }
        }

        Button backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        Button deleteButton = root.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the image URI from arguments
                Bundle args = getArguments();
                if (args != null && args.containsKey("imageUri")) {
                    String imageUriString = args.getString("imageUri");
                    if (imageUriString != null) {
                        // Get a reference to the image in Firebase Storage
                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUriString);

                        // Delete the image from Firebase Storage
                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Image deleted successfully
                                Toast.makeText(requireContext(), "Image deleted successfully", Toast.LENGTH_SHORT).show();

                                // Navigate back to the previous fragment
                                requireActivity().onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors that occur during deletion
                                Toast.makeText(requireContext(), "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
        return root;
    }
}
