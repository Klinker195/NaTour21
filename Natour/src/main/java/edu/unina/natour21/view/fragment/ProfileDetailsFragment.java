package edu.unina.natour21.view.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.unina.natour21.R;
import edu.unina.natour21.adapter.FavCollectionAdapter;
import edu.unina.natour21.model.FavCollection;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.model.User;
import edu.unina.natour21.viewmodel.RouteExplorationViewModel;

public class ProfileDetailsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView nameSurnameTextView;
    private TextView nicknameTextView;
    private ImageView propicImageView;

    private RouteExplorationViewModel viewModel;

    private Dialog loadingDialog;

    public ProfileDetailsFragment() {
    }

    public static ProfileDetailsFragment newInstance(String param1, String param2) {
        ProfileDetailsFragment fragment = new ProfileDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        propicImageView = (ImageView) view.findViewById(R.id.fragmentProfileDetailsUserPropicImageView);
        nameSurnameTextView = (TextView) view.findViewById(R.id.fragmentProfileDetailsNameSurnameTextView);
        nicknameTextView = (TextView) view.findViewById(R.id.fragmentProfileDetailsNicknameTextView);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragmentProfileDetailsFavCollectionsRecyclerView);

        viewModel = new ViewModelProvider(getActivity()).get(RouteExplorationViewModel.class);
        viewModel.setPosts(new Post[0]);
        viewModel.setUsers(new User[0]);

        observeViewModel();
    }

    public void observeViewModel() {

        viewModel.getOnCurrentUserFetchSuccess().observe(this.getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                propicImageView.setImageBitmap(user.getPropic());
                nameSurnameTextView.setText(user.getName() + " " + user.getSurname());
                nicknameTextView.setText("@" + user.getNickname());
                dismissLoadingDialog();
            }
        });

        viewModel.getOnCurrentUserFetchFailure().observe(this.getActivity(), new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                // TODO: Error
                dismissLoadingDialog();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new FavCollectionAdapter(new FavCollection[0], getParentFragmentManager()));

        showLoadingDialog();

        viewModel.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_details, container, false);
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