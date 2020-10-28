package com.example.rest.volley_apiservice;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rest.AppConfig;
import com.example.rest.model.Employee;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyApiService {
    String TAG = VolleyApiService.class.toString();

    RequestQueue requestQueue;
    Context context;
    public VolleyApiService(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    Employee result = new Employee();
    String str = "";
    public String getItemById(int id) {

        String url  = AppConfig.BASE_URL + "getEmployeePath/" + id;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                textView1.setText(response.toString());
//                result.setName(response.toString());
                try {

//                    Gson gson=new Gson();
                    Gson gson=new GsonBuilder().create();

                    result = gson.fromJson(response.toString() , Employee.class);
                    str = response.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, result.getName() + " >> " + result.getDesignation(), Toast.LENGTH_LONG).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsObjRequest.setTag(TAG);
        requestQueue.add(jsObjRequest);
        return str;
    }

    public void getAllItems() {
        String url  = AppConfig.BASE_URL + "getListEmployee";
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                    Gson gson=new Gson();
                Gson gson=new GsonBuilder().create();
                Employee[] results = gson.fromJson(response.toString() , Employee[].class);
                //Or
                Type userListType = new TypeToken<ArrayList<Employee>>(){}.getType();
                ArrayList<Employee> employeList = gson.fromJson(response.toString(), userListType);

//                Toast.makeText(context, results[1].getName(), Toast.LENGTH_LONG).show();
                Toast.makeText(context, employeList.get(3).getName() + " >> " + employeList.get(3).getDesignation(), Toast.LENGTH_LONG).show();

                requestQueue.stop();
                requestQueue.cancelAll(TAG);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyerror) {
            }
        }) ;
        stringRequest.setTag(TAG);
        requestQueue.add(stringRequest);

    }

    public void getAllItems_JsonArray() {
        requestQueue = Volley.newRequestQueue(context);
        String url = AppConfig.BASE_URL + "getListEmployee";

            JsonArrayRequest arrayRequest = new JsonArrayRequest(url, new com.android.volley.Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // Parsing json
                    Gson gson = new GsonBuilder().create();
                    List<Employee> list = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Employee domain = gson.fromJson(obj.toString(), Employee.class);
                            list.add(domain);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(context, list.get(1).getName() + " >> " + list.get(1).getDesignation(), Toast.LENGTH_LONG).show();
//                Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();

                    //STOP YANG LAIN >> SIAP AYANG DAHULU
                    requestQueue.stop();
                    requestQueue.cancelAll(TAG);
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyerror) {
                }
            });

            JsonArrayRequest arrayRequest2 = new JsonArrayRequest(url, new com.android.volley.Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // Parsing json
                    Gson gson = new GsonBuilder().create();
                    List<Employee> list = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Employee domain = gson.fromJson(obj.toString(), Employee.class);
                            list.add(domain);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(context, list.get(0).getName() + " >> " + list.get(0).getDesignation(), Toast.LENGTH_LONG).show();
//                Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();

                    //STOP YANG LAIN >> SIAP AYANG DAHULU
                    requestQueue.stop();
                    requestQueue.cancelAll(TAG);
                    /**
                     * Pakai cara ini juga oke
                     */
                    requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                        @Override
                        public boolean apply(Request<?> request) {
                            return false;
                        }
                    });

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyerror) {
                }
            });

        arrayRequest.setTag(TAG);
        arrayRequest2.setTag(TAG);
        requestQueue.add(arrayRequest);
        requestQueue.add(arrayRequest2);

    }


}
