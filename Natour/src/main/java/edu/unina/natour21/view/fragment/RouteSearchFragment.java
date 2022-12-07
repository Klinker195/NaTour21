package edu.unina.natour21.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.slider.Slider;
import com.google.android.material.slider.Slider.OnChangeListener;

import javax.validation.constraints.Null;

import edu.unina.natour21.R;
import edu.unina.natour21.adapter.PostCardAdapter;
import edu.unina.natour21.adapter.UserCardAdapter;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.model.User;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.view.activity.PostFilteringMapsActivity;
import edu.unina.natour21.viewmodel.RouteExplorationViewModel;

public class RouteSearchFragment extends Fragment {

    public final String TAG = this.getClass().getSimpleName();

    private RouteExplorationViewModel viewModel;

    private EditText searchEditText;
    private TextView routesSelectorButton;
    private TextView usersSelectorButton;
    private TextView filterByButton;
    private ImageView searchImageViewButton;
    private ImageView cardsBackgroundImageView;
    private ImageView cardsWhiteBarImageView;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private ImageView nothingToSeeImageView;

    private ImageView popupRouteFilteringBackButtonImageView;
    private TextView popupRouteFilteringDifficultyTextView;
    private TextView popupRouteFilteringDurationTextView;
    private TextView popupRouteFilteringGeographicAreaTextView;
    private TextView popupRouteFilteringAccessibilityTextView;
    private Slider popupRouteFilterDurationSlider;
    private TextView popupRouteFilteringDurationSliderValueTextView;
    private Button popupRouteFilteringGeographicAreaButton;
    private CheckBox popupRouteFilteringAccessibilityCheckBox;
    private ImageView popupRouteFilteringImageViewClearButton;
    private ImageView popupRouteFilteringImageViewDoneButton;
    private LinearLayout popupRouteFilteringDifficultyLinearLayout;

    private Dialog loadingDialog;

    private Boolean filterActive = false;

    private String searchedText = " ";
    private Double areaStartLat = -100d;
    private Double areaStartLng = -200d;
    private Integer difficultyFilterValue = -1;
    private Float durationFilterValue = 0f;
    private Boolean accessibilityFilterValue = false;

    private Integer currentRecyclerViewPage = 0;
    private final Integer recyclerViewPageSize = 10;


    ActivityResultLauncher<Intent> PostFilteringMapActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        LatLng areaPoint = data.getParcelableExtra("areaPoint");
                        //viewModel.writeGPXFile(pointArrayList, getFilesDir().getAbsolutePath());
                        if(areaPoint != null) {
                            Log.i(TAG, areaPoint.latitude + " | " + areaPoint.longitude);
                            areaStartLat = areaPoint.latitude;
                            areaStartLng = areaPoint.longitude;
                            popupRouteFilteringGeographicAreaButton.setText("Area selected, tap to change");
                            popupRouteFilteringGeographicAreaButton.setTextColor(getContext().getColor(R.color.edit_text_color));
                        } else {
                            areaStartLat = -100d;
                            areaStartLng = -200d;
                            popupRouteFilteringGeographicAreaButton.setText("Tap to set geographic area");
                            popupRouteFilteringGeographicAreaButton.setTextColor(getContext().getColor(R.color.lightgrey_text));
                        }
                    }
                }
            }
    );

    public RouteSearchFragment() {
        // Required empty public constructor
    }

    public static RouteSearchFragment newInstance() {
        RouteSearchFragment fragment = new RouteSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_edit_text);
        setSharedElementEnterTransition(transition);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find Views
        searchEditText = (EditText) getView().findViewById(R.id.fragmentRouteSearchEditText);
        searchImageViewButton = (ImageView) getView().findViewById(R.id.fragmentRouteSearchImageViewButton);
        routesSelectorButton = (TextView) getView().findViewById(R.id.fragmentRouteSearchRoutesFilterTextViewButton);
        usersSelectorButton = (TextView) getView().findViewById(R.id.fragmentRouteSearchUserFilterTextViewButton);
        filterByButton = (TextView) getView().findViewById(R.id.fragmentRouteSearchFilterByTextViewButton);
        cardsBackgroundImageView = (ImageView) getView().findViewById(R.id.fragmentRouteSearchWhiteBackgroundBlurImageView);
        cardsWhiteBarImageView = (ImageView) getView().findViewById(R.id.fragmentRouteSearchWhiteBarImageView);
        recyclerView = (RecyclerView) getView().findViewById(R.id.fragmentRouteSearchRecyclerView);
        nothingToSeeImageView = (ImageView) getView().findViewById(R.id.fragmentRouteSearchNothingToSeeImageView);
        //linearLayout = (LinearLayout) getView().findViewById(R.id.routeSearchLinearLayout);

        routesSelectorButton.setEnabled(false);
        usersSelectorButton.setEnabled(false);

        // Set ViewModel
        viewModel = new ViewModelProvider(getActivity()).get(RouteExplorationViewModel.class);
        viewModel.setPosts(new Post[0]);
        viewModel.setUsers(new User[0]);

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setTextGradientTextViewLeftButton(usersSelectorButton);
        designHandler.setTextGradientTextViewRightButtonInverse(routesSelectorButton);
        designHandler.setTextGradientTextViewButton(filterByButton);

        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if(event.getAction() != KeyEvent.ACTION_DOWN) return false;

                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    view.clearFocus();
                    searchButtonAction();
                    return true;
                }
                return false;
            }
        });

        // Set Button Listeners
        searchImageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchButtonAction();
            }
        });

        usersSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usersSelectorButton.getPaint().getShader() != null) {
                    designHandler.setTextGradientTextViewLeftButtonInverse(usersSelectorButton);
                    designHandler.setTextGradientTextViewRightButton(routesSelectorButton);

                    // TODO: Change visual
                    Animation fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_500ms);
                    filterByButton.setEnabled(false);
                    filterByButton.setVisibility(View.INVISIBLE);
                    filterByButton.startAnimation(fadeOutAnimation);

                    searchedText = searchEditText.getText().toString();
                    if(searchedText.replace(" ", "").isEmpty()) searchedText = " ";

                    filterActive = false;
                    areaStartLat = -100d;
                    areaStartLng = -200d;
                    difficultyFilterValue = -1;
                    durationFilterValue = 0f;
                    accessibilityFilterValue = false;
                    currentRecyclerViewPage = 0;

                    // TODO: Reset the post array in ViewModel and reset adapter to users
                    viewModel.setPosts(new Post[0]);
                    viewModel.setUsers(new User[0]);

                    recyclerView.setAdapter(new UserCardAdapter(new User[0]));
                    recyclerView.getAdapter().notifyDataSetChanged();

                    // viewModel.getPostsWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedTitle, -100d, -200d, -1, 0, false);
                    viewModel.getUsersWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText);

                    currentRecyclerViewPage += 1;

                    showLoadingDialog();

                    recyclerView.clearOnScrollListeners();

                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);

                            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                            if(!recyclerView.canScrollVertically(1) && layoutManager.getChildCount() != 1 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                showLoadingDialog();

                                viewModel.getUsersWithPaging(
                                        currentRecyclerViewPage,
                                        recyclerViewPageSize,
                                        searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText
                                );

                                Log.i(TAG, currentRecyclerViewPage.toString());

                                currentRecyclerViewPage += 1;
                            }
                        }
                    });
                }
            }
        });

        routesSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(routesSelectorButton.getPaint().getShader() != null) {
                    designHandler.setTextGradientTextViewRightButtonInverse(routesSelectorButton);
                    designHandler.setTextGradientTextViewLeftButton(usersSelectorButton);

                    // TODO: Change visual
                    Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_500ms);
                    filterByButton.setEnabled(true);
                    filterByButton.setVisibility(View.VISIBLE);
                    filterByButton.startAnimation(fadeInAnimation);

                    searchedText = searchEditText.getText().toString();
                    if(searchedText.replace(" ", "").isEmpty()) searchedText = " ";

                    filterActive = false;
                    areaStartLat = -100d;
                    areaStartLng = -200d;
                    difficultyFilterValue = -1;
                    durationFilterValue = 0f;
                    accessibilityFilterValue = false;
                    currentRecyclerViewPage = 0;

                    // TODO: Reset the user array in ViewModel (make sure to add it before) and reset adapter to routes
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    viewModel.setUsers(new User[0]);
                    viewModel.setPosts(new Post[0]);

                    recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                    recyclerView.getRecycledViewPool().clear();
                    recyclerView.getAdapter().notifyDataSetChanged();

                    viewModel.getPostsWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedText, -100d, -200d, -1, 0, false);

                    currentRecyclerViewPage += 1;

                    showLoadingDialog();

                    recyclerView.clearOnScrollListeners();

                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);

                            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                            if(!recyclerView.canScrollVertically(1) && layoutManager.getChildCount() != 1 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                showLoadingDialog();

                                viewModel.getPostsWithPaging(
                                        currentRecyclerViewPage,
                                        recyclerViewPageSize,
                                        searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText,
                                        areaStartLat == null || areaStartLat == -100d ? -100d : areaStartLat,
                                        areaStartLng == null || areaStartLng == -200d ? -200d : areaStartLng,
                                        difficultyFilterValue == null || difficultyFilterValue == -1 ? -1 : difficultyFilterValue + 1, // difficultyFilterValue == -1 ? null : difficultyFilterValue,
                                        durationFilterValue == null || durationFilterValue == 0 ? 0 : durationFilterValue.intValue(),
                                        accessibilityFilterValue == null || accessibilityFilterValue == false ? false : accessibilityFilterValue
                                );

                                Log.i(TAG, currentRecyclerViewPage.toString());

                                currentRecyclerViewPage += 1;
                            }
                        }
                    });
                }
            }
        });

        filterByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        // Set RecyclerView and make initial post query
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.setPosts(new Post[0]);
        viewModel.setUsers(new User[0]);

        /*
        recyclerView.setAdapter(new PostCardAdapter(new Post[0]));

        viewModel.getPostsWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedText, -100d, -200d, -1, 0, false);

        currentRecyclerViewPage += 1;

        showLoadingDialog();
         */

        // Set Scroll Listeners
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if(!recyclerView.canScrollVertically(1) && layoutManager.getChildCount() != 1 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    showLoadingDialog();

                    viewModel.getPostsWithPaging(
                            currentRecyclerViewPage,
                            recyclerViewPageSize,
                            searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText,
                            areaStartLat == null || areaStartLat == -100d ? -100d : areaStartLat,
                            areaStartLng == null || areaStartLng == -200d ? -200d : areaStartLng,
                            difficultyFilterValue == null || difficultyFilterValue == -1 ? -1 : difficultyFilterValue + 1, // difficultyFilterValue == -1 ? null : difficultyFilterValue,
                            durationFilterValue == null || durationFilterValue == 0 ? 0 : durationFilterValue.intValue(),
                            accessibilityFilterValue == null || accessibilityFilterValue == false ? false : accessibilityFilterValue
                            );

                    Log.i(TAG, currentRecyclerViewPage.toString());

                    currentRecyclerViewPage += 1;
                }
            }
        });

        // Start Animations
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_500ms_offset_300ms);
        searchImageViewButton.startAnimation(fadeInAnimation);
        cardsBackgroundImageView.startAnimation(fadeInAnimation);
        cardsWhiteBarImageView.startAnimation(fadeInAnimation);
        recyclerView.startAnimation(fadeInAnimation);
        routesSelectorButton.startAnimation(fadeInAnimation);
        usersSelectorButton.startAnimation(fadeInAnimation);
        filterByButton.startAnimation(fadeInAnimation);

        // Set Shared Transition
        searchEditText.setTransitionName("searchEditText");

        // Set focus on search edit text and open keyboard
        searchEditText.requestFocus();
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);

        // Set ViewModel Observers
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: If post is reported it should be shown here
        Log.i(TAG, "RESUME!");
        currentRecyclerViewPage = 0;
        if(routesSelectorButton.getPaint().getShader() == null) {
            viewModel.setPosts(new Post[0]);

            recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
            recyclerView.getAdapter().notifyDataSetChanged();

            viewModel.getPostsWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedText, -100d, -200d, -1, 0, false);

            currentRecyclerViewPage += 1;

            // showLoadingDialog();
            /*
            if(viewModel.getPosts().length != 0) {
                if(recyclerView.getAdapter() != null) {
                    if(recyclerView.getAdapter() instanceof UserCardAdapter) {
                        recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                    }
                    ((PostCardAdapter) recyclerView.getAdapter()).setLocalDataSet(viewModel.getPosts());
                } else {
                    recyclerView.setAdapter(new PostCardAdapter(viewModel.getPosts()));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            */
        } else if(usersSelectorButton.getPaint().getShader() == null) {
            viewModel.setUsers(new User[0]);

            recyclerView.setAdapter(new UserCardAdapter(new User[0]));
            recyclerView.getAdapter().notifyDataSetChanged();

            viewModel.getUsersWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText);

            currentRecyclerViewPage += 1;

            // showLoadingDialog();

            recyclerView.getAdapter().notifyDataSetChanged();
            /*
            if(viewModel.getUsers().length != 0) {
                if(recyclerView.getAdapter() != null) {
                    if(recyclerView.getAdapter() instanceof PostCardAdapter) {
                        recyclerView.setAdapter(new UserCardAdapter(new User[0]));
                    }
                    ((UserCardAdapter) recyclerView.getAdapter()).setLocalDataSet(viewModel.getUsers());
                } else {
                    recyclerView.setAdapter(new UserCardAdapter(viewModel.getUsers()));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            */
        }
    }

    /*
    @Override
    public void onStart() {
        super.onStart();

        recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
        recyclerView.getAdapter().notifyDataSetChanged();

        viewModel.getPostsWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedText, -100d, -200d, -1, 0, false);

        currentRecyclerViewPage += 1;

        showLoadingDialog();
    }

     */

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
                    if(recyclerView.getAdapter() instanceof UserCardAdapter) {
                        recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                    }
                    ((PostCardAdapter) recyclerView.getAdapter()).setLocalDataSet(posts);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    if(posts.length == recyclerView.getChildCount()) currentRecyclerViewPage -= 1;
                } else {
                    recyclerView.setAdapter(new PostCardAdapter(posts));
                }
                routesSelectorButton.setEnabled(true);
                usersSelectorButton.setEnabled(true);
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
                routesSelectorButton.setEnabled(true);
                usersSelectorButton.setEnabled(true);
                dismissLoadingDialog();
            }
        });

        viewModel.getOnUserFetchSuccess().observe(this.getActivity(), new Observer<User[]>() {
            @Override
            public void onChanged(User[] users) {
                if(users.length == 0) {
                    nothingToSeeImageView.setVisibility(View.VISIBLE);
                } else {
                    nothingToSeeImageView.setVisibility(View.GONE);
                }
                if(recyclerView.getAdapter() != null) {
                    if(recyclerView.getAdapter() instanceof PostCardAdapter) {
                        recyclerView.setAdapter(new UserCardAdapter(new User[0]));
                    }
                    ((UserCardAdapter) recyclerView.getAdapter()).setLocalDataSet(users);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    if(users.length == recyclerView.getChildCount()) currentRecyclerViewPage -= 1;
                } else {
                    recyclerView.setAdapter(new UserCardAdapter(users));
                }
                routesSelectorButton.setEnabled(true);
                usersSelectorButton.setEnabled(true);
                dismissLoadingDialog();
            }
        });

        viewModel.getOnUserFetchFailure().observe(this.getActivity(), new Observer<User[]>() {
            @Override
            public void onChanged(User[] users) {
                if(users.length == 0) {
                    nothingToSeeImageView.setVisibility(View.VISIBLE);
                } else {
                    nothingToSeeImageView.setVisibility(View.GONE);
                }
                routesSelectorButton.setEnabled(true);
                usersSelectorButton.setEnabled(true);
                dismissLoadingDialog();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_search, container, false);
    }

    private void searchButtonAction() {

        searchedText = searchEditText.getText().toString();
        if(searchedText.replace(" ", "").isEmpty()) searchedText = " ";

        if(routesSelectorButton.getPaint().getShader() == null) {
            if(popupRouteFilterDurationSlider != null) {
                durationFilterValue = popupRouteFilterDurationSlider.getValue();
            }
            if(popupRouteFilteringAccessibilityCheckBox != null) {
                accessibilityFilterValue = popupRouteFilteringAccessibilityCheckBox.isChecked();
            }

            if(filterActive == true) {
                showLoadingDialog();
                currentRecyclerViewPage = 0;

                // Clear recycler view
                if(recyclerView.getAdapter() != null) {
                    if(recyclerView.getAdapter() instanceof UserCardAdapter) {
                        recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                    }
                    ((PostCardAdapter) recyclerView.getAdapter()).setLocalDataSet(new Post[0]);
                    recyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                }
                viewModel.setPosts(new Post[0]);

                viewModel.getPostsWithPaging(
                        currentRecyclerViewPage,
                        recyclerViewPageSize,
                        searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText,
                        areaStartLat == null || areaStartLat == -100d ? -100d : areaStartLat,
                        areaStartLng == null || areaStartLng == -200d ? -200d : areaStartLng,
                        difficultyFilterValue == null || difficultyFilterValue == -1 ? -1 : difficultyFilterValue + 1, // difficultyFilterValue == -1 ? null : difficultyFilterValue,
                        durationFilterValue == null || durationFilterValue == 0 ? 0 : durationFilterValue.intValue(),
                        accessibilityFilterValue == null || accessibilityFilterValue == false ? false : accessibilityFilterValue
                );

                currentRecyclerViewPage += 1;
            } else {
                // TODO: ViewModel for basic search
                showLoadingDialog();
                currentRecyclerViewPage = 0;

                areaStartLat = -100d;
                areaStartLng = -200d;
                difficultyFilterValue = -1;
                durationFilterValue = 0f;
                accessibilityFilterValue = false;

                // Clear recycler view
                if(recyclerView.getAdapter() != null) {
                    if(recyclerView.getAdapter() instanceof UserCardAdapter) {
                        recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                    }
                    ((PostCardAdapter) recyclerView.getAdapter()).setLocalDataSet(new Post[0]);
                    recyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                }
                viewModel.setPosts(new Post[0]);

                viewModel.getPostsWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText, -100d, -200d, -1, 0, false);

                currentRecyclerViewPage += 1;
            }
        } else if(usersSelectorButton.getPaint().getShader() == null) {
            showLoadingDialog();
            currentRecyclerViewPage = 0;

            areaStartLat = -100d;
            areaStartLng = -200d;
            difficultyFilterValue = -1;
            durationFilterValue = 0f;
            accessibilityFilterValue = false;

            if(recyclerView.getAdapter() != null) {
                if(recyclerView.getAdapter() instanceof PostCardAdapter) {
                    recyclerView.setAdapter(new UserCardAdapter(new User[0]));
                }
                ((UserCardAdapter) recyclerView.getAdapter()).setLocalDataSet(new User[0]);
                recyclerView.getAdapter().notifyDataSetChanged();
            } else {
                recyclerView.setAdapter(new UserCardAdapter(new User[0]));
            }
            viewModel.setUsers(new User[0]);

            // viewModel.getPostsWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedTitle, -100d, -200d, -1, 0, false);
            viewModel.getUsersWithPaging(currentRecyclerViewPage, recyclerViewPageSize, searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText);

            currentRecyclerViewPage += 1;

        }
    }

    private void showFilterDialog() {
        final Dialog filterDialog = new Dialog(getActivity());
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setCancelable(true);
        filterDialog.setContentView(R.layout.popup_route_filtering);

        filterDialog.show();

        // Find Views
        popupRouteFilteringBackButtonImageView = (ImageView) filterDialog.findViewById(R.id.popupRouteFilteringBackButtonImageView);
        popupRouteFilteringDifficultyTextView = (TextView) filterDialog.findViewById(R.id.popupRouteFilteringDifficultyTextView);
        popupRouteFilteringDurationTextView = (TextView) filterDialog.findViewById(R.id.popupRouteFilteringDurationTextView);
        popupRouteFilteringGeographicAreaTextView = (TextView) filterDialog.findViewById(R.id.popupRouteFilteringGeographicAreaTextView);
        popupRouteFilteringAccessibilityTextView = (TextView) filterDialog.findViewById(R.id.popupRouteFilteringAccessibilityTextView);
        popupRouteFilterDurationSlider = (Slider) filterDialog.findViewById(R.id.popupRouteFilteringDurationSlider);
        popupRouteFilteringDurationSliderValueTextView = (TextView) filterDialog.findViewById(R.id.popupRouteFilteringDurationSliderValueTextView);
        popupRouteFilteringGeographicAreaButton = (Button) filterDialog.findViewById(R.id.popupRouteFilteringGeographicAreaButton);
        popupRouteFilteringAccessibilityCheckBox = (CheckBox) filterDialog.findViewById(R.id.popupRouteFilteringAccessibilityCheckBox);
        popupRouteFilteringImageViewClearButton = (ImageView) filterDialog.findViewById(R.id.popupRouteFilteringImageViewClearButton);
        popupRouteFilteringImageViewDoneButton = (ImageView) filterDialog.findViewById(R.id.popupRouteFilteringImageViewDoneButton);
        popupRouteFilteringDifficultyLinearLayout = (LinearLayout) filterDialog.findViewById(R.id.popupRouteFilteringDifficultyLinearLayout);

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setTextGradient(popupRouteFilteringDifficultyTextView);
        designHandler.setTextGradient(popupRouteFilteringDurationTextView);
        designHandler.setTextGradient(popupRouteFilteringGeographicAreaTextView);
        designHandler.setTextGradient(popupRouteFilteringAccessibilityTextView);

        // Set Default Data
        redrawDifficultyDots(difficultyFilterValue);
        popupRouteFilterDurationSlider.setValue(durationFilterValue);
        popupRouteFilteringAccessibilityCheckBox.setChecked(accessibilityFilterValue);

        if(durationFilterValue != null && durationFilterValue != 0f) {
            popupRouteFilteringDurationSliderValueTextView.setText(durationFilterValue.intValue() + "'");
        }

        if(areaStartLat != null && areaStartLat != -100d && areaStartLng != null && areaStartLng != -200d) {
            popupRouteFilteringGeographicAreaButton.setText("Area selected, tap to change");
            popupRouteFilteringGeographicAreaButton.setTextColor(getContext().getColor(R.color.edit_text_color));
        }

        for(int i = 0; i < popupRouteFilteringDifficultyLinearLayout.getChildCount(); i++) {
            int finalI = i;
            popupRouteFilteringDifficultyLinearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int n = finalI;
                    redrawDifficultyDots(n);
                    difficultyFilterValue = n;
                }
            });
        }

        // Set Listeners
        popupRouteFilteringGeographicAreaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(getActivity(), PostFilteringMapsActivity.class);
                PostFilteringMapActivityResultLauncher.launch(switchActivityIntent);
            }
        });

        popupRouteFilterDurationSlider.addOnChangeListener(new OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if(value != 0f) {
                    String stringValue = String.valueOf((int) value);
                    popupRouteFilteringDurationSliderValueTextView.setText(stringValue + '\'');
                } else {
                    popupRouteFilteringDurationSliderValueTextView.setText("Not selected");
                }

            }
        });

        popupRouteFilteringBackButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.hide();

                resetFilterValues();

                filterActive = false;
                filterByButton.setBackgroundResource(R.drawable.natour_button_background);
                designHandler.setTextGradientTextViewButton(filterByButton);
            }
        });

        popupRouteFilteringImageViewDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.hide();

                filterActive = true;
                filterByButton.setBackgroundResource(R.drawable.natour_gradient_button_background);
                filterByButton.getPaint().setShader(null);
                filterByButton.setTextColor(getResources().getColor(R.color.white));

                // Do filtered research based on EditText text

                searchedText = searchEditText.getText().toString();
                durationFilterValue = popupRouteFilterDurationSlider.getValue();
                accessibilityFilterValue = popupRouteFilteringAccessibilityCheckBox.isChecked();

                if(filterActive == true) {
                    showLoadingDialog();
                    currentRecyclerViewPage = 0;

                    // Clear recycler view
                    if(recyclerView.getAdapter() != null) {
                        if(recyclerView.getAdapter() instanceof UserCardAdapter) {
                            recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                        }
                        ((PostCardAdapter) recyclerView.getAdapter()).setLocalDataSet(new Post[0]);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                    }
                    viewModel.setPosts(new Post[0]);

                    viewModel.getPostsWithPaging(
                            currentRecyclerViewPage,
                            recyclerViewPageSize,
                            searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText,
                            areaStartLat == null || areaStartLat == -100d ? -100d : areaStartLat,
                            areaStartLng == null || areaStartLng == -200d ? -200d : areaStartLng,
                            difficultyFilterValue == null || difficultyFilterValue == -1 ? -1 : difficultyFilterValue + 1, // difficultyFilterValue == -1 ? null : difficultyFilterValue,
                            durationFilterValue == null ? 0 : durationFilterValue.intValue(),
                            accessibilityFilterValue == null ? false : accessibilityFilterValue
                    );

                    currentRecyclerViewPage += 1;
                } else {
                    filterActive = false;
                    filterByButton.setBackgroundResource(R.drawable.natour_button_background);
                    designHandler.setTextGradientTextViewButton(filterByButton);
                }
            }
        });

        popupRouteFilteringImageViewClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.hide();

                resetFilterValues();

                filterActive = false;
                filterByButton.setBackgroundResource(R.drawable.natour_button_background);
                designHandler.setTextGradientTextViewButton(filterByButton);

                searchedText = searchEditText.getText().toString();
                durationFilterValue = popupRouteFilterDurationSlider.getValue();
                accessibilityFilterValue = popupRouteFilteringAccessibilityCheckBox.isChecked();

                showLoadingDialog();
                currentRecyclerViewPage = 0;

                // Clear recycler view
                if(recyclerView.getAdapter() != null) {
                    ((PostCardAdapter) recyclerView.getAdapter()).setLocalDataSet(new Post[0]);
                    recyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    recyclerView.setAdapter(new PostCardAdapter(new Post[0]));
                }
                viewModel.setPosts(new Post[0]);

                viewModel.getPostsWithPaging(
                        currentRecyclerViewPage,
                        recyclerViewPageSize,
                        searchedText == null || searchedText.replace(" ", "").isEmpty() ? " " : searchedText,
                        areaStartLat == null || areaStartLat == -100d ? -100d : areaStartLat,
                        areaStartLng == null || areaStartLng == -200d ? -200d : areaStartLng,
                        difficultyFilterValue == null || difficultyFilterValue == -1 ? -1 : difficultyFilterValue + 1, // difficultyFilterValue == -1 ? null : difficultyFilterValue,
                        durationFilterValue == null ? 0 : durationFilterValue.intValue(),
                        accessibilityFilterValue == null ? false : accessibilityFilterValue
                );

                currentRecyclerViewPage += 1;
            }
        });

        // Set Window
        Window window = filterDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(R.drawable.natour_components_edittext_background);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setBackgroundBlurRadius(10);
        }
    }

    private void redrawDifficultyDots(int index) {
        Drawable enabledDifficultyDot = getResources().getDrawable(R.drawable.natour_difficulty_filter_circle);
        Drawable disabledDifficultyDot = getResources().getDrawable(R.drawable.natour_difficulty_filter_circle_disabled);

        for(int i = 0; i < popupRouteFilteringDifficultyLinearLayout.getChildCount(); i++) {
            ImageView difficultyDot = (ImageView) popupRouteFilteringDifficultyLinearLayout.getChildAt(i);
            if(i <= index) {
                difficultyDot.setImageDrawable(enabledDifficultyDot);
            } else {
                difficultyDot.setImageDrawable(disabledDifficultyDot);
            }
        }
    }

    private void resetFilterValues() {
        searchedText = " ";
        areaStartLat = -100d;
        areaStartLng = -200d;
        difficultyFilterValue = -1;
        durationFilterValue = 0f;
        accessibilityFilterValue = false;
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