package edu.unina.natour21.retrofit;

import java.util.List;

import edu.unina.natour21.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IUserAPI {

    @GET("/user/filtered/{page}/{pageSize}/{genericName}")
    Call<List<UserDTO>> getUsersWithPaging(
            @Path("page") Integer page,
            @Path("pageSize") Integer pageSize,
            @Path("genericName") String genericName
            );

    @GET("/user/{email}")
    Call<UserDTO> getUserByEmail(@Path("email") String email);

    @POST("/user")
    Call<UserDTO> saveNewUser(@Body UserDTO user);

}
