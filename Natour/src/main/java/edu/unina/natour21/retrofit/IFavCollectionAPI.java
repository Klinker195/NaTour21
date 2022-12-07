package edu.unina.natour21.retrofit;

import java.util.List;

import edu.unina.natour21.dto.FavCollectionDTO;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IFavCollectionAPI {

    @GET("/favCollection/{email}")
    Call<List<FavCollectionDTO>> getFavCollectionByEmail(@Path("email") String email);

    @POST("/addToFavCollection/{collectionId}/{postId}")
    Call<Boolean> addPostToFavCollection(@Path("collectionId") Long collectionId, @Path("postId") Long postId);

    @DELETE("/removePostFromFavCollection/{collectionId}/{postId}")
    Call<Boolean> removePostFromFavCollection(@Path("collectionId") Long collectionId, @Path("postId") Long postId);

}
