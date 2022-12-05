package edu.unina.natour21.retrofit;

import edu.unina.natour21.dto.ReportDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IReportAPI {

    @POST("/post/report")
    Call<Boolean> saveNewReport(@Body ReportDTO report);

}
