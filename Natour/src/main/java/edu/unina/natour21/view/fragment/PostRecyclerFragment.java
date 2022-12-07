package edu.unina.natour21.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.LinkedList;

import edu.unina.natour21.R;
import edu.unina.natour21.adapter.PostCardAdapter;
import edu.unina.natour21.adapter.UserCardAdapter;
import edu.unina.natour21.model.FavCollection;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.model.User;
import edu.unina.natour21.viewmodel.RouteExplorationViewModel;
import io.jenetics.jpx.Link;

public class PostRecyclerFragment extends Fragment {

    public final String TAG = this.getClass().getSimpleName();

    /*
        MODE:
        Use 0 for community and 1 for post fav list.
    */
    private static final String ARG_NAME_1 = "MODE";
    private static final String ARG_NAME_2 = "collectionId";

    private Integer mode;
    private Long collectionId;

    private RouteExplorationViewModel viewModel;

    private RecyclerView recyclerView;
    private ImageView nothingToSeeImageView;

    private Dialog loadingDialog;

    private Integer currentRecyclerViewPage = 0;
    private final Integer recyclerViewPageSize = 10;

    public PostRecyclerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
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

        if(getArguments() != null) {
            mode = getArguments().getInt(ARG_NAME_1);
            collectionId = getArguments().getLong(ARG_NAME_2);
        }

        // Find Views
        recyclerView = (RecyclerView) view.findViewById(R.id.fragmentPostRecyclerRecyclerView);
        nothingToSeeImageView = (ImageView) view.findViewById(R.id.fragmentPostRecyclerNothingToSeeImageView);

        // Set ViewModel
        viewModel = new ViewModelProvider(getActivity()).get(RouteExplorationViewModel.class);
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

                if(!recyclerView.canScrollVertically(1) && layoutManager.getChildCount() != 1 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    showLoadingDialog();

                    if(mode == 0) {
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
                    } else if(mode == 1) {
                        // TODO: Make query instead of using cached objects
                        if(viewModel.getUserFavCollections() != null && !viewModel.getUserFavCollections().isEmpty()) {
                            for(FavCollection favCollection : viewModel.getUserFavCollections()) {
                                if(favCollection.getId() == collectionId) {
                                    if(favCollection.getPosts() != null && !favCollection.getPosts().isEmpty()) {
                                        Integer sublistIndex = currentRecyclerViewPage * recyclerViewPageSize;
                                        if(sublistIndex > favCollection.getPosts().size()) sublistIndex = favCollection.getPosts().size();
                                        LinkedList<Post> favCollectionLinkedList = new LinkedList<Post>(favCollection.getPosts().subList(0, sublistIndex));
                                        if(recyclerView.getAdapter() != null) {
                                            ((PostCardAdapter) recyclerView.getAdapter()).setLocalDataSet(favCollection.getPosts().toArray(new Post[favCollection.getPosts().size()]));
                                        } else {
                                            recyclerView.setAdapter(new PostCardAdapter(favCollection.getPosts().toArray(new Post[favCollection.getPosts().size()])));
                                        }
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                    } else {
                                        currentRecyclerViewPage = 0;
                                    }
                                }
                            }
                        }
                        dismissLoadingDialog();
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

        viewModel.setPosts(new Post[0]);

        showLoadingDialog();

        if(mode == 0) {
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
        } else if(mode == 1) {
            // TODO: Make query instead of using cached objects
            if(viewModel.getUserFavCollections() != null && !viewModel.getUserFavCollections().isEmpty()) {
                for(FavCollection favCollection : viewModel.getUserFavCollections()) {
                    if(favCollection.getId() == collectionId) {
                        if(favCollection.getPosts() != null && !favCollection.getPosts().isEmpty()) {
                            Integer sublistIndex = currentRecyclerViewPage * recyclerViewPageSize;
                            if(sublistIndex > favCollection.getPosts().size()) sublistIndex = favCollection.getPosts().size();
                            LinkedList<Post> favCollectionLinkedList = new LinkedList<Post>(favCollection.getPosts().subList(0, sublistIndex));
                            if(recyclerView.getAdapter() != null) {
                                ((PostCardAdapter) recyclerView.getAdapter()).setLocalDataSet(favCollection.getPosts().toArray(new Post[favCollection.getPosts().size()]));
                            } else {
                                recyclerView.setAdapter(new PostCardAdapter(favCollection.getPosts().toArray(new Post[favCollection.getPosts().size()])));
                            }
                            recyclerView.getAdapter().notifyDataSetChanged();
                        } else {
                            Toast.makeText(this.getContext(), "The collection is empty", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(getParentFragmentManager().getPrimaryNavigationFragment().getView()).navigate(R.id.action_global_profile);
                            currentRecyclerViewPage = 0;
                        }
                    }
                }
            } else {
                Toast.makeText(this.getContext(), "The collection is empty", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getParentFragmentManager().getPrimaryNavigationFragment().getView()).navigate(R.id.action_global_profile);
            }
            dismissLoadingDialog();
        }

        currentRecyclerViewPage += 1;
    }

    private void observeViewModel() {
        viewModel.getOnPostFetchSuccess().observe(this.getActivity(), new Observer<Post[]>() {
            @Override
            public void onChanged(Post[] posts) {
                if(posts.length == 0) {
                    nothingToSeeImageView.setVisibility(View.VISIBLE);
                } else {
                    nothingToSeeImageView.setVisibility(View.GONE);
                }
                if(recyclerView.getAdapter() != null) {
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
                if(posts.length == 0) {
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
        if(loadingDialog == null) {
            loadingDialog = new Dialog(this.getContext());
        }
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.popup_loading);

        ProgressBar loadingDialogProgressBar = (ProgressBar) getView().findViewById(R.id.popupLoadingProgressBar);

        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if(loadingDialog != null) {
            if(loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}