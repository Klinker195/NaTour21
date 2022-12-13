package edu.unina.natour21.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import edu.unina.natour21.R;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.utility.NatourFileHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.view.activity.PostDetailsActivity;

public class PostCardAdapter extends RecyclerView.Adapter<PostCardAdapter.ViewHolder> {

    private static final String TAG = PostCardAdapter.class.getSimpleName();

    private Post[] localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private FirebaseAnalytics firebaseAnalytics;

        private final ImageView picImageView;
        private final ImageView reportImageView;
        private final ImageView accessibilityImageView;
        private final TextView titleTextView;
        private final TextView authorTextView;
        private final TextView durationTextView;
        private final LinearLayout difficultyLinearLayout;
        private final FloatingActionButton reportBackgroundButton;
        private final FloatingActionButton accessibilityBackgroundButton;
        private final Button rateBackgroundButton;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            firebaseAnalytics = FirebaseAnalytics.getInstance(view.getContext());

            picImageView = (ImageView) view.findViewById(R.id.postCardPicImageView);
            accessibilityImageView = (ImageView) view.findViewById(R.id.postCardAccessibilityImageView);
            reportImageView = (ImageView) view.findViewById(R.id.postCardReportImageView);
            titleTextView = (TextView) view.findViewById(R.id.postCardTitleTextView);
            authorTextView = (TextView) view.findViewById(R.id.postCardAuthorTextView);
            durationTextView = (TextView) view.findViewById(R.id.postCardDurationTextView);
            difficultyLinearLayout = (LinearLayout) view.findViewById(R.id.postCardDifficultyLinearLayout);
            reportBackgroundButton = (FloatingActionButton) view.findViewById(R.id.postCardReportBackgroundButton);
            accessibilityBackgroundButton = (FloatingActionButton) view.findViewById(R.id.postCardAccessibilityBackgroundButton);
            rateBackgroundButton = (Button) view.findViewById(R.id.postCardRateButton);

            NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
            designHandler.setTextGradient(rateBackgroundButton);
        }

        public FirebaseAnalytics getFirebaseAnalytics() {
            return firebaseAnalytics;
        }

        public ImageView getPicImageView() {
            return picImageView;
        }

        public ImageView getReportImageView() {
            return reportImageView;
        }

        public ImageView getAccessibilityImageView() {
            return accessibilityImageView;
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getAuthorTextView() {
            return authorTextView;
        }

        public TextView getDurationTextView() {
            return durationTextView;
        }

        public LinearLayout getDifficultyLinearLayout() {
            return difficultyLinearLayout;
        }

        public FloatingActionButton getReportBackgroundButton() {
            return reportBackgroundButton;
        }

        public FloatingActionButton getAccessibilityBackgroundButton() {
            return accessibilityBackgroundButton;
        }

        public Button getRateBackgroundButton() {
            return rateBackgroundButton;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public PostCardAdapter(Post[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cards_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer position = viewHolder.getAdapterPosition();

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, localDataSet[position].getId().toString());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, localDataSet[position].getTitle());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Post details");
                viewHolder.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Intent switchActivityIntent = new Intent(view.getContext(), PostDetailsActivity.class);
                switchActivityIntent.putExtra("postDetails", localDataSet[position]);

                String[] picsArray = new String[localDataSet[position].getPics().size()];
                NatourFileHandler fileHandler = new NatourFileHandler();
                for (int i = 0; i < localDataSet[position].getPics().size(); i++) {
                    picsArray[i] = fileHandler.createImageFromBitmap(view.getContext(), localDataSet[position].getPics().get(0));
                }
                switchActivityIntent.putExtra("postPics", picsArray);

                String userPropic;
                userPropic = fileHandler.createImageFromBitmap(view.getContext(), localDataSet[position].getAuthor().getPropic());
                switchActivityIntent.putExtra("userPropic", userPropic);

                view.getContext().startActivity(switchActivityIntent);
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.getTitleTextView().setText(localDataSet[position].getTitle());

        if(!localDataSet[position].getPics().isEmpty() && localDataSet[position].getPics().get(0) != null) {
            viewHolder.getPicImageView().setImageBitmap(localDataSet[position].getPics().get(0));
        } else {
            viewHolder.getPicImageView().setImageResource(R.drawable.standard_route_pic);
        }

        if (localDataSet[position].getReported()) {
            viewHolder.getReportImageView().setVisibility(View.VISIBLE);
            viewHolder.getReportBackgroundButton().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getReportImageView().setVisibility(View.GONE);
            viewHolder.getReportBackgroundButton().setVisibility(View.GONE);
        }

        if (localDataSet[position].getAccessibility()) {
            viewHolder.getAccessibilityImageView().setVisibility(View.VISIBLE);
            viewHolder.getAccessibilityBackgroundButton().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getAccessibilityImageView().setVisibility(View.GONE);
            viewHolder.getAccessibilityBackgroundButton().setVisibility(View.GONE);
        }

        viewHolder.getAuthorTextView().setText("@" + localDataSet[position].getAuthor().getNickname());

        viewHolder.getDurationTextView().setText(localDataSet[position].getDuration() + "'");

        for (int i = 0; i < viewHolder.getDifficultyLinearLayout().getChildCount(); i++) {
            if (i < localDataSet[position].getDifficulty()) {
                viewHolder.getDifficultyLinearLayout().getChildAt(i).setVisibility(View.VISIBLE);
            } else {
                viewHolder.getDifficultyLinearLayout().getChildAt(i).setVisibility(View.GONE);
            }
        }

        /*
        if (localDataSet[position].getReported()) {
            // Could add report button on post details
        }
        */

        viewHolder.getRateBackgroundButton().setVisibility(View.GONE);
        viewHolder.getRateBackgroundButton().setText(localDataSet[position].getRate().toString());
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setTextGradient(viewHolder.getRateBackgroundButton());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

    public void setLocalDataSet(Post[] localDataSet) {
        this.localDataSet = localDataSet;
    }

}