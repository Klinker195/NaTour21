package edu.unina.natour21.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.NatourUIDesignHandler;
import edu.unina.natour21.viewmodel.RouteExplorationViewModel;

public class RouteExplorationFragment extends Fragment {

    public final String TAG = this.getClass().getSimpleName();

    private EditText searchEditText;
    private Button takeMeButton;

    private RouteExplorationViewModel viewModel;

    public RouteExplorationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RouteExplorationFragment newInstance() {
        RouteExplorationFragment fragment = new RouteExplorationFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Find Views
        searchEditText = (EditText) getView().findViewById(R.id.fragmentRouteExplorationSearchEditText);
        takeMeButton = (Button) getView().findViewById(R.id.fragmentRouteExplorationTakeMeAnywhereButton);

        // Set View Model
        viewModel = new ViewModelProvider(this).get(RouteExplorationViewModel.class);

        // Set Standard UI
        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();
        designHandler.setTextGradient(takeMeButton);

        searchEditText.setTransitionName("searchEditText");

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    // Transition to search
                    FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(searchEditText, "searchEditText").build();
                    Navigation.findNavController(view).navigate(R.id.action_routeExplorationFragment2_to_routeSearchFragment, null, null, extras);
                    // viewModel.setFragment(RouteSearchFragment.newInstance(), getParentFragmentManager().beginTransaction().addSharedElement(searchEditText, "searchEditText"));
                }
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route_exploration, container, false);
    }
}