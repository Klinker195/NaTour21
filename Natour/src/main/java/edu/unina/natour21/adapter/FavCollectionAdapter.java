package edu.unina.natour21.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import edu.unina.natour21.R;
import edu.unina.natour21.model.FavCollection;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.view.activity.PostFilteringMapsActivity;

public class FavCollectionAdapter extends RecyclerView.Adapter<FavCollectionAdapter.ViewHolder> {

    private static final String TAG = FavCollectionAdapter.class.getSimpleName();

    private FavCollection[] localDataSet;
    private final FragmentManager fragmentManager;

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

        private FirebaseAnalytics firebaseAnalytics;

        private final TextView titleTextView;
        private final ImageView iconImageView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            firebaseAnalytics = FirebaseAnalytics.getInstance(view.getContext());
            titleTextView = (TextView) view.findViewById(R.id.collectionCardTitleTextView);
            iconImageView = (ImageView) view.findViewById(R.id.collectionCardIconImageView);
        }

        public FirebaseAnalytics getFirebaseAnalytics() {
            return firebaseAnalytics;
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public ImageView getIconImageView() {
            return iconImageView;
        }

    }

    public FavCollectionAdapter(FavCollection[] dataSet, FragmentManager fragmentManager) {
        ArrayList<FavCollection> dataSetArrayList = new ArrayList<FavCollection>(Arrays.asList(dataSet));
        FavCollection createNewCollection = new FavCollection();
        createNewCollection.setTitle("Create new collection");
        dataSetArrayList.add(createNewCollection);
        this.localDataSet = dataSetArrayList.toArray(new FavCollection[dataSetArrayList.size()]);
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
                if (localDataSet[position].getId() != null) {
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
        ImageView iconImageView = viewHolder.getIconImageView();

        String title = localDataSet[position].getTitle().charAt(0) + localDataSet[position].getTitle().toLowerCase(Locale.ROOT).substring(1);
        titleTextView.setText(title);

        iconImageView.setImageBitmap(null);

        if (localDataSet[position].getTitle().toUpperCase(Locale.ROOT).equals("FAVORITES")) {
            iconImageView.setBackgroundResource(R.drawable.natour_fav_collection_button);
        } else if (localDataSet[position].getTitle().toUpperCase(Locale.ROOT).equals("TO VISIT")) {
            iconImageView.setBackgroundResource(R.drawable.natour_visit_collection_button);
        } else if (localDataSet[position].getTitle().toUpperCase(Locale.ROOT).equals("CREATE NEW COLLECTION")) {
            iconImageView.setBackgroundResource(R.drawable.natour_create_collection_button);
        } else {
            iconImageView.setBackgroundResource(R.drawable.natour_create_collection_button);
        }

        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setTextGradient(titleTextView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

    public void setLocalDataSet(FavCollection[] localDataSet) {
        ArrayList<FavCollection> dataSetArrayList = new ArrayList<FavCollection>(Arrays.asList(localDataSet));
        FavCollection createNewCollection = new FavCollection();
        createNewCollection.setTitle("Create new collection");
        dataSetArrayList.add(createNewCollection);
        this.localDataSet = dataSetArrayList.toArray(new FavCollection[dataSetArrayList.size()]);
    }

}