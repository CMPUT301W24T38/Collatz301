package com.example.collatzcheckin.admin.controls.images;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.admin.controls.images.FirebaseImageUriFetcher;
import com.example.collatzcheckin.admin.controls.images.ImageDisplayFragment;

import java.util.List;

/**
 * Fragment responsible for displaying a list of image IDs fetched from Firebase Storage for an admin to browse
 */
public class ImageIdListFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<String> adapter; // Define the adapter for your ListView
    private FirebaseImageUriFetcher imageUriFetcher;

    /**
     * Inflates the layout for this fragment and sets up the UI components
     * Initializes the ListView and its adapter
     * Fetches image URIs and names from Firebase Storage
     * Sets up click listener for ListView items to display the corresponding image
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState A Bundle containing the saved state of the fragment
     * @return The View of the inflated layout for the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image_id_list, container, false);
        listView = root.findViewById(R.id.image_list_View);

        // Create an adapter for the ListView
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        imageUriFetcher = new FirebaseImageUriFetcher(requireContext(), listView);
        imageUriFetcher.fetchImageUrisAndNames("posters"); // Fetch image URIs from Firebase Storage

        setupListViewClickListener(); // Setup ListView item click listener
        return root;
    }

    /**
     * Sets up a click listener for ListView items
     * When an item is clicked, the matching image URI is passed to ImageDisplayFragment for display
     */
    private void setupListViewClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri selectedImageUri = (Uri) parent.getItemAtPosition(position);
                // Pass the selected image URI to a new fragment for display
                ImageDisplayFragment fragment = new ImageDisplayFragment();
                Bundle args = new Bundle();
                args.putString("imageUri", selectedImageUri.toString());
                fragment.setArguments(args);

                // Replace the current fragment with the new one
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     * Currently unused
     * Updates the image list displayed in the ListView adapter
     * Clears the existing data and adds new image IDs to the adapter
     * Notifies the adapter that the dataset has changed
     * @param imageList The list of image IDs to be displayed
     */
    public void updateImageList(List<String> imageList) {
        adapter.clear(); // Clear the existing data
        adapter.addAll(imageList); // Add new data to the adapter
        adapter.notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }
}
