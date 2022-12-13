package edu.unina.natour21.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import com.google.firebase.analytics.FirebaseAnalytics;

import edu.unina.natour21.R;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.utility.KeyboardHandler;
import edu.unina.natour21.utility.NatourFileHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.view.activity.PostDetailsActivity;
import edu.unina.natour21.view.activity.PostFilteringMapsActivity;
import edu.unina.natour21.viewmodel.RouteExplorationViewModel;

public class RouteExplorationFragment extends Fragment {

    private static final String TAG = RouteExplorationFragment.class.getSimpleName();

    private FirebaseAnalytics firebaseAnalytics;

    private RouteExplorationViewModel viewModel;

    private EditText searchEditText;
    private Button takeMeButton;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        KeyboardHandler.hideKeyboard(getActivity());
    }

    public RouteExplorationFragment() {
        // Required empty public constructor
    }

    public static RouteExplorationFragment newInstance() {
        RouteExplorationFragment fragment = new RouteExplorationFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        // Find Views
        searchEditText = (EditText) getView().findViewById(R.id.fragmentRouteExplorationSearchEditText);
        takeMeButton = (Button) getView().findViewById(R.id.fragmentRouteExplorationTakeMeAnywhereButton);

        // Set Listeners
        takeMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Take me anywhere button");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Post details");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                viewModel.getRandomPost();
            }
        });

        // Set View Model
        viewModel = new ViewModelProvider(this).get(RouteExplorationViewModel.class);

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setTextGradient(takeMeButton);

        searchEditText.setTransitionName("searchEditText");

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // Transition to search
                    FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(searchEditText, "searchEditText").build();
                    Navigation.findNavController(view).navigate(R.id.action_routeExplorationFragment2_to_routeSearchFragment, null, null, extras);
                    // viewModel.setFragment(RouteSearchFragment.newInstance(), getParentFragmentManager().beginTransaction().addSharedElement(searchEditText, "searchEditText"));
                }
            }
        });

        observeViewModel();
    }

    public void observeViewModel() {

        viewModel.getOnRandomPostFetchSuccess().observe(this.getActivity(), new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                Intent switchActivityIntent = new Intent(RouteExplorationFragment.this.getContext(), PostDetailsActivity.class);
                switchActivityIntent.putExtra("postDetails", post);

                String[] picsArray = new String[post.getPics().size()];
                NatourFileHandler fileHandler = new NatourFileHandler();
                for (int i = 0; i < post.getPics().size(); i++) {
                    picsArray[i] = fileHandler.createImageFromBitmap(RouteExplorationFragment.this.getContext(), post.getPics().get(0));
                }
                switchActivityIntent.putExtra("postPics", picsArray);

                String userPropic;
                userPropic = fileHandler.createImageFromBitmap(RouteExplorationFragment.this.getContext(), post.getAuthor().getPropic());
                switchActivityIntent.putExtra("userPropic", userPropic);

                RouteExplorationFragment.this.getContext().startActivity(switchActivityIntent);
            }
        });

        viewModel.getOnRandomPostFetchFailure().observe(this.getActivity(), new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                Toast.makeText(getActivity(), "Couldn't fetch random post",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route_exploration, container, false);
    }
}