package edu.unina.natour21.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;

import edu.unina.natour21.R;
import edu.unina.natour21.adapter.PostCardAdapter;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.model.User;
import edu.unina.natour21.view.activity.PostFilteringMapsActivity;
import edu.unina.natour21.viewmodel.RouteExplorationViewModel;

public class PostRecyclerFragment extends Fragment {

    private static final String TAG = PostRecyclerFragment.class.getSimpleName();

    private FirebaseAnalytics firebaseAnalytics;

    private RouteExplorationViewModel viewModel;

    /*
        MODE:
        Use 0 for community and 1 for post fav list.
    */
    private static final String ARG_NAME_1 = "MODE";
    private static final String ARG_NAME_2 = "collectionId";
    private Integer mode;
    private Long collectionId;

    private RecyclerView recyclerView;
    private ImageView nothingToSeeImageView;

    private Dialog loadingDialog;

    private Integer currentRecyclerViewPage = 0;
    private final Integer recyclerViewPageSize = 10;

    public PostRecyclerFragment() {
        // Required empty public constructor
    }

    public static PostRecyclerFragment newInstance(Integer MODE) {
        PostRecyclerFragment fragment = new PostRecyclerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NAME_1, MODE);
        args.putLong(ARG_NAME_2, -1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        if (getArguments() != null) {
            mode = getArguments().getInt(ARG_NAME_1);
            collectionId = getArguments().getLong(ARG_NAME_2);
        }

        // Find Views
        recyclerView = (RecyclerView) view.findViewById(R.id.fragmentPostRecyclerRecyclerView);
        nothingToSeeImageView = (ImageView) view.findViewById(R.id.fragmentPostRecyclerNothingToSeeImageView);

        // Set ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(RouteExplorationViewModel.class);
        viewModel.setPosts(new Post[0]);
        viewModel.setUsers(new User[0]);

        // Set RecyclerView LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set Scroll Listeners
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!recyclerView.canScrollVertically(1) && layoutManager.getChildCount() != 1 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    showLoadingDialog();

                    if (mode == 0) {
                        viewModel.getPostsWithPaging(
                                currentRecyclerViewPage,
                                recyclerViewPageSize,
                                " ",
                                -100d,
                                -200d,
                                -1,
                                0,
                                false
                        );
                    } else if (mode == 1) {

                        viewModel.getPostsByCollectionIdWithPaging(
                                currentRecyclerViewPage,
                                recyclerViewPageSize,
                                collectionId
                        );
                    }

                    Log.i(TAG, currentRecyclerViewPage.toString());

                    currentRecyclerViewPage += 1;
                }
            }
        });

        // Set ViewModel Observers
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "RESUME!");
        currentRecyclerViewPage = 0;

        recyclerView.setAdapter(new PostCardAdapter(new Post[0]));

        viewModel.setPosts(new Post[0]);

        showLoadingDialog();

        if (mode == 0) {
            viewModel.getPostsWithPaging(
                    currentRecyclerViewPage,
                    recyclerViewPageSize,
                    " ",
                    -100d,
                    -200d,
                    -1,
                    0,
                    false
            );
        } else if (mode == 1) {
            viewModel.getPostsByCollectionIdWithPaging(
                    currentRecyclerViewPage,
                    recyclerViewPageSize,
                    collectionId
            );
        }

        currentRecyclerViewPage += 1;
    }

    private void observeViewModel() {
        viewModel.getOnPostFetchSuccess().observe(this.getActivity(), new Observer<Post[]>() {
            @Override
            public void onChanged(Post[] posts) {
                if (posts.length == 0) {
                    nothingToSeeImageView.setVisibility(View.VISIBLE);
                } else {
                    nothingToSeeImageView.setVisibility(View.GONE);
                }
                if (recyclerView.getAdapter() != null) {
                    ((PostCardAdapter) recyclerView.getAdapter()).setLocalDataSet(posts);
                } else {
                    recyclerView.setAdapter(new PostCardAdapter(posts));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                dismissLoadingDialog();
            }
        });

        viewModel.getOnPostFetchFailure().observe(this.getActivity(), new Observer<Post[]>() {
            @Override
            public void onChanged(Post[] posts) {
                if (posts.length == 0) {
                    nothingToSeeImageView.setVisibility(View.VISIBLE);
                } else {
                    nothingToSeeImageView.setVisibility(View.GONE);
                }
                dismissLoadingDialog();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_recycler, container, false);
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new Dialog(this.getContext());
        }
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.popup_loading);

        ProgressBar loadingDialogProgressBar = (ProgressBar) getView().findViewById(R.id.popupLoadingProgressBar);

        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}