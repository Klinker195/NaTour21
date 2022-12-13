package edu.unina.natour21.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;

import edu.unina.natour21.R;
import edu.unina.natour21.model.User;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.ViewHolder> {

    private static final String TAG = UserCardAdapter.class.getSimpleName();

    private User[] localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private FirebaseAnalytics firebaseAnalytics;

        private final ImageView propicImageView;
        private final TextView nicknameTextView;
        private final TextView nameSurnameTextView;
        private final ImageView followImageViewButton;

        public ViewHolder(View view) {
            super(view);

            firebaseAnalytics = FirebaseAnalytics.getInstance(view.getContext());

            // Define click listener for the ViewHolder's View
            propicImageView = (ImageView) view.findViewById(R.id.userCardPropicImageView);
            nicknameTextView = (TextView) view.findViewById(R.id.userCardNicknameTextView);
            nameSurnameTextView = (TextView) view.findViewById(R.id.userCardNameSurnameTextView);
            followImageViewButton = (ImageView) view.findViewById(R.id.userCardFollowImageView);
        }

        public FirebaseAnalytics getFirebaseAnalytics() {
            return firebaseAnalytics;
        }

        public ImageView getPropicImageView() {
            return propicImageView;
        }

        public TextView getNicknameTextView() {
            return nicknameTextView;
        }

        public TextView getNameSurnameTextView() {
            return nameSurnameTextView;
        }

        public ImageView getFollowImageView() {
            return followImageViewButton;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    /*
    public UserCardAdapter(User[] dataSet) {
        localDataSet = dataSet;
    }
    */
    public UserCardAdapter(User[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_cards_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        if(localDataSet[position].getPropic() != null) {
            viewHolder.getPropicImageView().setImageBitmap(localDataSet[position].getPropic());
        } else {
            viewHolder.getPropicImageView().setImageResource(R.drawable.standard_propic);
        }

        viewHolder.getNicknameTextView().setText("@" + localDataSet[position].getNickname());

        Log.i(TAG, localDataSet[position].getName() + " " + localDataSet[position].getSurname());
        viewHolder.getNameSurnameTextView().setText(localDataSet[position].getName() + " " + localDataSet[position].getSurname());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

    public void setLocalDataSet(User[] localDataSet) {
        this.localDataSet = localDataSet;
    }

}