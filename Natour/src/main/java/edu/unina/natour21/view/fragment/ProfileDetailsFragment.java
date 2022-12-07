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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

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

        nameSurnameTextView.setTextColor(getResources().getColor(R.color.white));
        nicknameTextView.setTextColor(getResources().getColor(R.color.white));

        viewModel = new ViewModelProvider(getActivity()).get(RouteExplorationViewModel.class);
        viewModel.setPosts(new Post[0]);
        viewModel.setUsers(new User[0]);

        observeViewModel();
    }

    public void observeViewModel() {

        viewModel.getOnCurrentUserFetchSuccess().observe(this.getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                nameSurnameTextView.setTextColor(getResources().getColor(R.color.edit_text_color));
                nicknameTextView.setTextColor(getResources().getColor(R.color.edit_text_color));
                propicImageView.setImageBitmap(user.getPropic());
                nameSurnameTextView.setText(user.getName() + " " + user.getSurname());
                nicknameTextView.setText("@" + user.getNickname());
                // dismissLoadingDialog();
                viewModel.getCurrentUserFavCollectionsByEmail(user.getEmail());
            }
        });

        viewModel.getOnCurrentUserFetchFailure().observe(this.getActivity(), new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                // TODO: Error
                dismissLoadingDialog();
            }
        });

        viewModel.getOnCurrentUserFavCollectionSuccess().observe(this.getActivity(), new Observer<ArrayList<FavCollection>>() {
            @Override
            public void onChanged(ArrayList<FavCollection> favCollectionArrayList) {
                ArrayList<FavCollection> newFavCollectionArrayList = new ArrayList<FavCollection>();

                // TODO: Check if correct

                for(FavCollection favCollection : favCollectionArrayList) {
                    if(favCollection != null && favCollection.getPosts() != null && !favCollection.getPosts().isEmpty()) {
                        newFavCollectionArrayList.add(favCollection);
                    }
                }

                if(recyclerView.getAdapter() != null) {
                    ((FavCollectionAdapter) recyclerView.getAdapter()).setLocalDataSet(newFavCollectionArrayList.toArray(new FavCollection[newFavCollectionArrayList.size()]));
                } else {
                    recyclerView.setAdapter(new FavCollectionAdapter(newFavCollectionArrayList.toArray(new FavCollection[newFavCollectionArrayList.size()]), getParentFragmentManager()));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                dismissLoadingDialog();
            }
        });

        viewModel.getOnCurrentUserFavCollectionFailure().observe(this.getActivity(), new Observer<Void>() {
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