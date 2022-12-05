package edu.unina.natour21.adapter;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

import edu.unina.natour21.R;
import edu.unina.natour21.model.FavCollection;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.utility.NatourFileHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.view.activity.PostCreationActivity;
import edu.unina.natour21.view.activity.PostDetailsActivity;

public class FavCollectionAdapter extends RecyclerView.Adapter<FavCollectionAdapter.ViewHolder> {

    private FavCollection[] localDataSet;
    private FragmentManager fragmentManager;

    /*
    MODE:
    Use 0 for community and 1 for post fav list.
    */
    private static final String ARG_NAME_COMMUNITY_1 = "MODE";

    private static final String ARG_NAME_COMMUNITY_2 = "collectionId";

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            titleTextView = (TextView) view.findViewById(R.id.collectionCardTitleTextView);
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

    }

    public FavCollectionAdapter(FavCollection[] dataSet, FragmentManager fragmentManager) {
        ArrayList<FavCollection> dataSetArrayList = new ArrayList<FavCollection>(Arrays.asList(dataSet));
        FavCollection createNewCollection = new FavCollection();
        createNewCollection.setTitle("Create new collection");
        dataSetArrayList.add(createNewCollection);
        localDataSet = dataSetArrayList.toArray(new FavCollection[dataSetArrayList.size()]);
        this.fragmentManager = fragmentManager;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.favcollection_cards_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer position = viewHolder.getAdapterPosition();
                if(localDataSet[position].getId() != null) {
                    Bundle args = new Bundle();
                    args.putInt(ARG_NAME_COMMUNITY_1, 1);
                    args.putLong(ARG_NAME_COMMUNITY_2, localDataSet[position].getId());
                    Navigation.findNavController(fragmentManager.getPrimaryNavigationFragment().getView()).navigate(R.id.action_global_community, args);
                } else {
                    Toast.makeText(view.getContext(), "STUB: Feature yet to be implemented", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        TextView titleTextView = viewHolder.getTitleTextView();

        titleTextView.setText(localDataSet[position].getTitle());
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setTextGradient(titleTextView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

    public void setLocalDataSet(FavCollection[] localDataSet) {
        this.localDataSet = localDataSet;
    }

}