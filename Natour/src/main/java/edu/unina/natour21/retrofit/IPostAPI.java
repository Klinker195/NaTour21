package edu.unina.natour21.retrofit;

import java.util.List;

import edu.unina.natour21.dto.PostDTO;
import edu.unina.natour21.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IPostAPI {

    @GET("/post/range/{rangeStart}/{rangeEnd}")
    Call<List<PostDTO>> getPostByRange(@Path("rangeStart") Long rangeStart, @Path("rangeEnd") Long rangeEnd);

    @GET("/post/filtered/{page}/{pageSize}/{title}/{areaStartLat}/{areaStartLng}/{difficulty}/{duration}/{accessibility}")
    Call<List<PostDTO>> getPostsWithPaging(
            @Path("page") Integer page,
            @Path("pageSize") Integer pageSize,
            @Path("title") String title,
            @Path("areaStartLat") Double areaStartLat,
            @Path("areaStartLng") Double areaStartLng,
            @Path("difficulty") Integer difficulty,
            @Path("duration") Integer duration,
            @Path("accessibility") Boolean accessibility
            );

    @GET("/post/{id}")
    Call<PostDTO> getPostByID(@Path("id") Long id);

    @POST("/post")
    Call<PostDTO> saveNewPost(@Body PostDTO post);

    @DELETE("/post/delete/{id}")
    Call<Void> deletePostById(@Path("id") Long id);

}
