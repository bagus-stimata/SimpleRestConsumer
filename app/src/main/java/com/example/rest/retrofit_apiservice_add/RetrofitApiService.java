package com.example.rest.retrofit_apiservice_add;

import com.example.rest.model.UploadFileResponse;
import com.example.rest.model.Employee;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitApiService {
    @GET("getEmployee")
    Call<Employee> getEmployeeRetrofit();

    @GET("getEmployeeParam")
    Call<Employee> getEmployeeRetrofitParam(@Query("id") Integer id);

    @GET("getEmployeePath/{id}")
    Call<Employee> getEmployeeRetrofitPath(@Path("id") Integer id);

    @GET("getListEmployee")
    Call<List<Employee>> getListEmployeeRetrofit();



    @POST("createEmployee")
    Call<Employee> createEmployee(@Body Employee employee);

    @PUT("putEmployee")
    Call<Employee> putEmployee(@Query("id") int id, @Body Employee employee);

    @HTTP(method = "DELETE", path = "deleteEmployee/{id}", hasBody = true)
//    @DELETE("deleteEmployee/{id}")
    Call<Employee> deleteEmployee(@Path("id") int id);


    /**
     * FILE UPLOAD & DOWNLOAD
     */
    @GET("downloadFile/{fileName}")
    Call<ResponseBody> downloadImageOrFile(@Path("fileName") String fileName);

    @Multipart
    @POST("uploadFile")
//    @HTTP(method = "POST", path = "uploadFile", hasBody = true)
    Call<UploadFileResponse> uploadFileMultipart(@Part MultipartBody.Part file);

}
