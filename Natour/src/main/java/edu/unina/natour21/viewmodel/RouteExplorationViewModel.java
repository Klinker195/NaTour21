package edu.unina.natour21.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.unina.natour21.R;
import edu.unina.natour21.dto.FavCollectionDTO;
import edu.unina.natour21.dto.PostDTO;
import edu.unina.natour21.dto.UserDTO;
import edu.unina.natour21.model.FavCollection;
import edu.unina.natour21.model.Post;
import edu.unina.natour21.model.User;
import edu.unina.natour21.retrofit.AmazonAPI;
import edu.unina.natour21.retrofit.IFavCollectionAPI;
import edu.unina.natour21.retrofit.IPostAPI;
import edu.unina.natour21.retrofit.IUserAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteExplorationViewModel extends ViewModel {

    public final String TAG = this.getClass().getSimpleName();

    private final MutableLiveData<Post[]> onPostFetchSuccess = new MutableLiveData<Post[]>();
    private final MutableLiveData<Post[]> onPostFetchFailure = new MutableLiveData<Post[]>();
    private Post[] posts = new Post[0];

    private final MutableLiveData<User[]> onUserFetchSuccess = new MutableLiveData<User[]>();
    private final MutableLiveData<User[]> onUserFetchFailure = new MutableLiveData<User[]>();
    private User[] users = new User[0];

    private final MutableLiveData<User> onCurrentUserFetchSuccess = new MutableLiveData<User>();
    private final MutableLiveData<Void> onCurrentUserFetchFailure = new MutableLiveData<Void>();

    private final MutableLiveData<ArrayList<FavCollection>> onCurrentUserFavCollectionSuccess = new MutableLiveData<ArrayList<FavCollection>>();
    private final MutableLiveData<Void> onCurrentUserFavCollectionFailure = new MutableLiveData<Void>();
    private ArrayList<FavCollection> userFavCollections = new ArrayList<FavCollection>();

    public void setFragment(Fragment fragment, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.replace(R.id.routeExplorationFragment, fragment, "RouteExploration");
        fragmentTransaction.commit();
    }

    public void getPostByRange(Long startRange, Long endRange) {
        IPostAPI postAPI = AmazonAPI.getClient().create(IPostAPI.class);
        Call<List<PostDTO>> postCall = postAPI.getPostByRange(startRange, endRange);

        LinkedList<Post> postLinkedList = new LinkedList<Post>();

        postCall.enqueue(new Callback<List<PostDTO>>() {
            @Override
            public void onResponse(Call<List<PostDTO>> call, Response<List<PostDTO>> response) {
                if(response.body() != null) {
                    Log.i(TAG, "Post call success");
                    LinkedList<PostDTO> responsePostDTOLinkedList = new LinkedList<PostDTO>(response.body());
                    for(PostDTO postDTO : responsePostDTOLinkedList) {
                        postLinkedList.add(new Post(postDTO));
                    }
                    onPostFetchSuccess(postLinkedList.toArray(new Post[postLinkedList.size()]));
                }
            }

            @Override
            public void onFailure(Call<List<PostDTO>> call, Throwable t) {
                onPostFetchSuccess(new Post[0]);
                Log.e(TAG, "Post call failure");
                t.printStackTrace();
            }
        });
    }

    public void getCurrentUser() {
        Amplify.Auth.fetchUserAttributes(new Consumer<List<AuthUserAttribute>>() {
            @Override
            public void accept(@NonNull List<AuthUserAttribute> attributes) {
                for(AuthUserAttribute value : attributes) {
                    if (value.getKey().getKeyString().equals("email")) {
                        String email = value.getValue().replace(" ", "");
                        IUserAPI userAPI = AmazonAPI.getClient().create(IUserAPI.class);
                        Call<UserDTO> userCall = userAPI.getUserByEmail(email);

                        userCall.enqueue(new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                if(response.body() != null) {
                                    onCurrentUserFetchSuccess(new User(response.body()));
                                } else {
                                    onCurrentUserFetchFailure();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDTO> call, Throwable t) {
                                onCurrentUserFetchFailure();
                            }
                        });
                    }
                }
            }
        }, new Consumer<AuthException>() {
            @Override
            public void accept(@NonNull AuthException value) {
                onCurrentUserFetchFailure();
            }
        });
    }

    public void getCurrentUserFavCollectionsByEmail(String userEmail) {
        IFavCollectionAPI favCollectionAPI = AmazonAPI.getClient().create(IFavCollectionAPI.class);
        Call<List<FavCollectionDTO>> favCollectionCall = favCollectionAPI.getFavCollectionByEmail(userEmail);

        favCollectionCall.enqueue(new Callback<List<FavCollectionDTO>>() {
            @Override
            public void onResponse(Call<List<FavCollectionDTO>> call, Response<List<FavCollectionDTO>> response) {
                if(response.body() != null) {
                    ArrayList<FavCollectionDTO> favCollectionDTOArrayList = new ArrayList<FavCollectionDTO>(response.body());
                    ArrayList<FavCollection> favCollectionArrayList = new ArrayList<FavCollection>();
                    for(FavCollectionDTO favCollectionDTO : favCollectionDTOArrayList) {
                        favCollectionArrayList.add(new FavCollection(favCollectionDTO));
                    }
                    userFavCollections = favCollectionArrayList;
                    onCurrentUserFavCollectionSuccess(favCollectionArrayList);
                } else {
                    onCurrentUserFavCollectionFailure();
                }
            }

            @Override
            public void onFailure(Call<List<FavCollectionDTO>> call, Throwable t) {
                onCurrentUserFavCollectionFailure();
            }
        });
    }

    public void getUsersWithPaging(Integer page, Integer pageSize, String genericName) {
        IUserAPI userAPI = AmazonAPI.getClient().create(IUserAPI.class);
        Call<List<UserDTO>> userCall = userAPI.getUsersWithPaging(page, pageSize, genericName);

        Log.i(TAG, genericName);

        Log.i(TAG, userCall.request().toString());

        LinkedList<User> userLinkedList = new LinkedList<User>();

        userCall.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if(response.body() != null) {
                    Log.i(TAG, "User call success");
                    LinkedList<UserDTO> responseUserDTOLinkedList = new LinkedList<UserDTO>(response.body());
                    for(UserDTO userDTO : responseUserDTOLinkedList) {
                        userLinkedList.add(new User(userDTO));
                    }
                    onUserFetchSuccess(userLinkedList.toArray(new User[userLinkedList.size()]));
                } else {
                    onUserFetchFailure(new User[0]);
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                onUserFetchFailure(new User[0]);
                Log.e(TAG, "User call failure");
                t.printStackTrace();
            }
        });
    }

    public void getPostsWithPaging(Integer page, Integer pageSize, String title, Double areaStartLat, Double areaStartLng, Integer difficulty, Integer duration, Boolean accessibility) {
        IPostAPI postAPI = AmazonAPI.getClient().create(IPostAPI.class);
        Call<List<PostDTO>> postCall = postAPI.getPostsWithPaging(page, pageSize, title, areaStartLat, areaStartLng, difficulty, duration, accessibility);

        Log.i(TAG, title);

        Log.i(TAG, postCall.request().toString());

        LinkedList<Post> postLinkedList = new LinkedList<Post>();

        postCall.enqueue(new Callback<List<PostDTO>>() {
            @Override
            public void onResponse(Call<List<PostDTO>> call, Response<List<PostDTO>> response) {
                if(response.body() != null) {
                    Log.i(TAG, "Post call success");
                    LinkedList<PostDTO> responsePostDTOLinkedList = new LinkedList<PostDTO>(response.body());
                    for(PostDTO postDTO : responsePostDTOLinkedList) {
                        postLinkedList.add(new Post(postDTO));
                    }
                    onPostFetchSuccess(postLinkedList.toArray(new Post[postLinkedList.size()]));
                } else {
                    onPostFetchFailure(new Post[0]);
                }
            }

            @Override
            public void onFailure(Call<List<PostDTO>> call, Throwable t) {
                onPostFetchFailure(new Post[0]);
                Log.e(TAG, "Post call failure");
                t.printStackTrace();
            }
        });
    }

    private void onCurrentUserFavCollectionSuccess(ArrayList<FavCollection> favCollectionArrayList) {
        onCurrentUserFavCollectionSuccess.postValue(favCollectionArrayList);
    }

    private void onCurrentUserFavCollectionFailure() {
        onCurrentUserFavCollectionFailure.postValue(null);
    }

    private void onCurrentUserFetchSuccess(User user) {
        onCurrentUserFetchSuccess.postValue(user);
    }

    private void onCurrentUserFetchFailure() {
        onCurrentUserFetchFailure.postValue(null);
    }

    private void onPostFetchSuccess(Post[] postArray) {
        Post[] resultPostArray = posts;
        if(postArray.length != 0) {
            List<Post> list = new ArrayList<Post>(Arrays.asList(posts));
            list.addAll(Arrays.asList(postArray));
            resultPostArray = list.toArray(new Post[list.size()]);
            posts = resultPostArray;
        }
        onPostFetchSuccess.postValue(resultPostArray);
    }

    private void onUserFetchSuccess(User[] userArray) {
        User[] resultUserArray = users;
        if(userArray.length != 0) {
            List<User> list = new ArrayList<User>(Arrays.asList(users));
            list.addAll(Arrays.asList(userArray));
            resultUserArray = list.toArray(new User[list.size()]);
            users = resultUserArray;
        }
        onUserFetchSuccess.postValue(resultUserArray);
    }

    private void onPostFetchFailure(Post[] postArray) {
        onPostFetchFailure.postValue(posts);
    }

    private void onUserFetchFailure(User[] userArray) {
        onUserFetchFailure.postValue(users);
    }

    public LiveData<ArrayList<FavCollection>> getOnCurrentUserFavCollectionSuccess() {
        return onCurrentUserFavCollectionSuccess;
    }

    public LiveData<Void> getOnCurrentUserFavCollectionFailure() {
        return onCurrentUserFavCollectionFailure;
    }

    public LiveData<User> getOnCurrentUserFetchSuccess() {
        return onCurrentUserFetchSuccess;
    }

    public LiveData<Void> getOnCurrentUserFetchFailure() {
        return onCurrentUserFetchFailure;
    }

    public LiveData<Post[]> getOnPostFetchSuccess() {
        return onPostFetchSuccess;
    }

    public LiveData<Post[]> getOnPostFetchFailure() {
        return onPostFetchFailure;
    }

    public LiveData<User[]> getOnUserFetchSuccess() {
        return onUserFetchSuccess;
    }

    public LiveData<User[]> getOnUserFetchFailure() {
        return onUserFetchFailure;
    }

    public void setPosts(Post[] posts) {
        this.posts = posts;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Post[] getPosts() {
        return posts;
    }

    public User[] getUsers() {
        return users;
    }

    public ArrayList<FavCollection> getUserFavCollections() {
        return userFavCollections;
    }

    public void setUserFavCollections(ArrayList<FavCollection> userFavCollections) {
        this.userFavCollections = userFavCollections;
    }
}
