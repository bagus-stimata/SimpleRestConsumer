package com.example.rest.springrest_apiservice;

import android.os.AsyncTask;
import android.util.Log;

import com.example.rest.AppConfig;
import com.example.rest.config.ApiSpringRestClient;
import com.example.rest.model.Employee;
import com.example.rest.model.UploadFileResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SpringRestApiService {
    protected static final String TAG = SpringRestApiService.class.getSimpleName();
    private ApiSpringRestClient apiSpringRestClient  = new ApiSpringRestClient();

    public SpringRestApiService(){
    }
    public Employee getItemById(int id) {
        CrudAsyncTask asyncTask = (CrudAsyncTask) new CrudAsyncTask(apiSpringRestClient, id, true);
        Employee domain = null;
        try {
            domain = asyncTask.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (domain==null) domain =new Employee();
        return domain;
    }

    public void createItem(Employee domain) {
        new SpringRestApiService.CrudAsyncTask(apiSpringRestClient, domain).execute();
    }
    public void updateAccAccount(Integer id, Employee domain) {
        new SpringRestApiService.CrudAsyncTask(apiSpringRestClient, id, domain).execute();
    }
    public void deleteAccAccount(Integer id) {
        new SpringRestApiService.CrudAsyncTask(apiSpringRestClient, id).execute();
    }

    public List<Employee> getAllItems() {
        FecthItemsAsyncTask asyncTask = (FecthItemsAsyncTask) new FecthItemsAsyncTask(apiSpringRestClient);
        List<Employee> listItems = new ArrayList<>();
        try {
            listItems = asyncTask.execute().get();
        } catch (InterruptedException | ExecutionException e) {
        }
        return listItems;
    }

    public class CrudAsyncTask extends AsyncTask<Void, Void, Employee> {

        String operation = "";
        Employee newAccAccount = null;
        Integer id = 0;
        private ApiSpringRestClient apiAuthenticationClient;

        private CrudAsyncTask(ApiSpringRestClient apiAuthenticationClient, Integer id_find, boolean isGetById ) {
            this.apiAuthenticationClient = apiAuthenticationClient;
            if (isGetById) {
                this.id = id_find;
                operation = "GET_BY_ID";
            }
        }
        private CrudAsyncTask(ApiSpringRestClient apiAuthenticationClient, Employee newDomain){
            this.apiAuthenticationClient = apiAuthenticationClient;
            this.newAccAccount = newDomain;
            operation = "ADD_NEW";
        }
        private CrudAsyncTask(ApiSpringRestClient apiAuthenticationClient, Integer id_update, Employee updateDomain){
            this.apiAuthenticationClient = apiAuthenticationClient;
            this.newAccAccount = updateDomain;
            this.id = id_update;
            operation = "UPDATE";
        }
        private CrudAsyncTask(ApiSpringRestClient apiAuthenticationClient, Integer id_delete){
            this.apiAuthenticationClient = apiAuthenticationClient;
            this.id = id_delete;
            operation = "DELETE";
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Employee doInBackground(Void... voids) {
            String url = AppConfig.BASE_URL;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

            try {

//                ResponseEntity<AccAccount> response = restTemplate.exchange(url, HttpMethod.POST, AccAccount.class);
//                HttpEntity<Object> httpEntity = new HttpEntity<Object>(newAccAccount, apiAuthenticationClient.getRequestHeaders());
//                ResponseEntity<AccAccount> response = restTemplate.postForEntity(url, httpEntity,  AccAccount.class);
                ResponseEntity<Employee> response = null;
                try {
                    if (operation.equals("ADD_NEW")) {
                        url += "createEmployee";
                        response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(newAccAccount, apiAuthenticationClient.getRequestHeaders()), Employee.class);
                    } else if (operation.equals("UPDATE")) {
                        url += "putEmployee/" + id;
                        response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<Object>(newAccAccount, apiAuthenticationClient.getRequestHeaders()), Employee.class);
                    } else if (operation.equals("DELETE")) {
                        url += "deleteEmployee/" + id;
                        response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<Object>(apiAuthenticationClient.getRequestHeaders()), Employee.class);
                    } else if (operation.equals("GET_BY_ID")) {
                        url += "getEmployeePath/" + id;
                        response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(apiAuthenticationClient.getRequestHeaders()), Employee.class);
                    }
                    Log.d(TAG, url + " >> " + response.toString());
                }catch (Exception ex){
                }

                return response!=null? response.getBody(): new Employee();

            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Employee();
            } catch (ResourceAccessException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Employee();
            }
        }

        @Override
        protected void onPostExecute(Employee result) {
        }
    }


    public class FecthItemsAsyncTask extends  AsyncTask<Void, Void, List<Employee>>{
        private ApiSpringRestClient apiAuthenticationClient;

        private FecthItemsAsyncTask(ApiSpringRestClient apiAuthenticationClient){
            this.apiAuthenticationClient = apiAuthenticationClient;
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected List<Employee> doInBackground(Void... voids) {
            final String url = AppConfig.BASE_URL + "getListEmployee";

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

            try {
                // Make the network request
                Log.d(TAG, url);
                ResponseEntity<Employee[]> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(apiAuthenticationClient.getRequestHeaders()), Employee[].class);
                List<Employee> list = Arrays.asList(response.getBody());
                return list;

            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new ArrayList<Employee>();
            } catch (ResourceAccessException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new ArrayList<Employee>();
            }
        }
        @Override
        protected void onPostExecute(List<Employee> result) {
        }
    }

    /**
     * UPLOAD PICTURE AND FILE
     */

    public class FileUploadAsyncTask extends AsyncTask<Void, Void, UploadFileResponse> {


        String operation = "";
        UploadFileResponse newDomain = null;
        Integer id = 0;
        private ApiSpringRestClient apiAuthenticationClient;

        private FileUploadAsyncTask(ApiSpringRestClient apiAuthenticationClient, Integer id_find, boolean isGetById ) {
            this.apiAuthenticationClient = apiAuthenticationClient;
            if (isGetById) {
                this.id = id_find;
                operation = "GET_BY_ID";
            }
        }
        private FileUploadAsyncTask(ApiSpringRestClient apiAuthenticationClient, UploadFileResponse newDomain){
            this.apiAuthenticationClient = apiAuthenticationClient;
            this.newDomain = newDomain;
            operation = "ADD_NEW";
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected UploadFileResponse doInBackground(Void... voids) {
            String url = AppConfig.BASE_URL;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

            try {

//                ResponseEntity<AccAccount> response = restTemplate.exchange(url, HttpMethod.POST, AccAccount.class);
//                HttpEntity<Object> httpEntity = new HttpEntity<Object>(newAccAccount, apiAuthenticationClient.getRequestHeaders());
//                ResponseEntity<AccAccount> response = restTemplate.postForEntity(url, httpEntity,  AccAccount.class);
                ResponseEntity<UploadFileResponse> response = null;
                try {
                    if (operation.equals("ADD_NEW")) {
                        url += "createEmployee";
                        response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(newDomain, apiAuthenticationClient.getRequestHeaders()), UploadFileResponse.class);
                    } else if (operation.equals("UPDATE")) {
                        url += "putEmployee/" + id;
                        response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<Object>(newDomain, apiAuthenticationClient.getRequestHeaders()), UploadFileResponse.class);
                    } else if (operation.equals("GET_BY_ID")) {
                        url += "getEmployeePath/" + id;
                        response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(apiAuthenticationClient.getRequestHeaders()), UploadFileResponse.class);
                    }
                    Log.d(TAG, url + " >> " + response.toString());
                }catch (Exception ex){
                }

                return response!=null? response.getBody(): new UploadFileResponse();

            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new UploadFileResponse();
            } catch (ResourceAccessException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new UploadFileResponse();
            }
        }

        @Override
        protected void onPostExecute(UploadFileResponse result) {
        }
    }

}
