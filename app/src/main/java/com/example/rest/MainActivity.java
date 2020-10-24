package com.example.rest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rest.clientapi_service.RetrofitApiService;
import com.example.rest.config.ApiClientRetrofit;
import com.example.rest.model.Employee;
import com.example.rest.model.RecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class MainActivity extends AppCompatActivity {
    Employee employee = new Employee();
    List<Employee> listEmployee = new ArrayList<>();

    @BindView(R.id.textView1)
    TextView textView1;

    @BindView(R.id.recyclerView1)
    RecyclerView recyclerView;

    @BindView(R.id.imageView1)
    ImageView imageView1;

    @BindView(R.id.floatingActionButton1)
    FloatingActionButton floatingActionButton;

    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), new ArrayList<>()); //Pasti kosong dulu
        recyclerView.setAdapter(recyclerAdapter);
        //RecyclerView harus diberi layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        simpleRestClient_WithRetrofit2();
        floatingActionButton.setOnClickListener(e -> {
            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.setType("*/*");
//            fileIntent.setType("image/*");
            startActivityForResult(fileIntent, 10);


        });
    }

    public void simpleRestClient_WithRetrofit2() {
        RetrofitApiService apiService = ApiClientRetrofit.getClient().create(RetrofitApiService.class);

        Call<Employee> call = apiService.getEmployeeRetrofitPath(3);
        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                employee = response.body();
                if (employee != null)
                    textView1.setText(employee.getName() + " | " + employee.getDesignation());
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Log.e("ERROR >> ######", t.getMessage());
            }

        });

        Call<List<Employee>> callList = apiService.getListEmployeeRetrofit();
        callList.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                listEmployee = response.body();
                recyclerAdapter.setList(listEmployee);

                Log.d("", "======================================");
                listEmployee.forEach(employee1 -> {
                    Log.d(">>>>>>>>>#####", employee1.getName() + " | " + employee1.getDesignation());
                });
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {

            }
        });

        employee.setId(6);
        employee.setName("Tambahan Bos");
        employee.setDesignation("operator");
        Call<Employee> callCreateEmployee = apiService.createEmployee(employee);
        callCreateEmployee.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                Log.d("Masuk", "Masuk Create");
                listEmployee.add(response.body());
                recyclerAdapter.setList(listEmployee);
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Log.e("Error", "Error Create");
            }
        });

        Call<Employee> callPutEmployee = apiService.putEmployee(2, employee);
        callPutEmployee.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                Log.d("Put", "Put Berhasil");
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Log.e("Error", "Error Put");
            }
        });

//        Call<Employee> callDeleteEmployee = apiService.deleteEmployee(1);
//        callDeleteEmployee.enqueue(new Callback<Employee>() {
//            @Override
//            public void onResponse(Call<Employee> call, Response<Employee> response) {
//                Log.d("Delete", "Delete Berhasil");
//            }
//            @Override
//            public void onFailure(Call<Employee> call, Throwable t) {
//                Log.e("Error", "Error Delete");
//            }
//        });

        Call<ResponseBody> callRetrievePicture = apiService.downloadImageOrFile("aa.png");

        callRetrievePicture.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                InputStream is = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageView1.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

//            Response<ResponseBody> response = retrievePicture.execute();
//            InputStream is = response.body().byteStream();
//            Bitmap bitmap = BitmapFactory.decodeStream(is);

    }

    String mediaPath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 10:
                    RetrofitApiService apiService = ApiClientRetrofit.getClient().create(RetrofitApiService.class);

                    Uri uriPath = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriPath);
                        imageView1.setImageBitmap(bitmap);
                    } catch (Exception ex) {
                    }

//                    File file = convertBitmapToFile_UsingViaByteArrayOs(bitmap);

                    MediaType mediaType = MediaType.parse(getContentResolver().getType(uriPath));


                    Toast.makeText(this, mediaType + " >> " + uriPath.getPath() +  " >> " + uriPath.getEncodedPath(),
                            Toast.LENGTH_LONG).show();

//                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
////                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
//                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
//
//
//                    Call<ResponseBody> call = apiService.uploadPhotoMultipart(body);
//                    call.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            if (response.isSuccessful()) {
//                                Log.d("UploadImage", "Yeepee!!! = " + response.message());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            if (t instanceof SocketTimeoutException) {
//                                // "Connection Timeout";
//                                t.printStackTrace();
//                                Log.e("UploadImage", "Connection Timeout");
//                            } else if (t instanceof IOException) {
//                                // "Timeout";
//                                t.printStackTrace();
//                                Log.e("UploadImage", "Timeout");
//                            } else {
//                                //Call was cancelled by user
//                                if (call.isCanceled()) {
//                                    Log.e("UploadImage", "Call was cancelled forcefully");
//                                } else {
//                                    //Generic error handling
//                                    Log.e("UploadImage", "Network Error :: " + t.getLocalizedMessage());
//                                }
//                            }
//                        }
//                    });

                    break;

                default:
                    break;
            }
        }
    }


    static Resource createTempFileResource(byte[] content) throws IOException {
        Path tempFile = Files.createTempFile("upload-file", ".txt");
        Files.write(tempFile, content);
        return new FileSystemResource(tempFile.toFile());
    }

    private File convertBitmapToFile_UsingViaByteArrayOs(Bitmap reducedBitmap) {
//        File mediaStorageDir = Environment.getExternalStorageDirectory() ;
//        File file = new File(mediaStorageDir + File.separator + "reduced_file");
        File mediaStorageDir = getApplicationContext().getFilesDir();
        File file = new File(mediaStorageDir, "nama_baru.jpg");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reducedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] imgbytes = byteArrayOutputStream.toByteArray(); //Proses Isi nomor. 1
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file); //Proses Write to File
            fos.write(imgbytes); //Proses Isi Nomor. 2
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private File convertBitmapToFile_UsingOsLangsung(Bitmap reducedBitmap) {
        File mediaStorageDir = getApplicationContext().getFilesDir();


        File file = new File(mediaStorageDir, "nama_baru.png");

        OutputStream os;
        try {
            os = new FileOutputStream(file); //Proses Write to File
            reducedBitmap.compress(Bitmap.CompressFormat.PNG, 40, os); //Proses Isi Nomor. 1
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return file;
    }
}