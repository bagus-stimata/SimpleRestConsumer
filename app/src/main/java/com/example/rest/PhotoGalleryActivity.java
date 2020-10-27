package com.example.rest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.rest.ui.GlideImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoGalleryActivity extends AppCompatActivity {

    public String authHeader = ""; //Untuk REtrofit2 springboot security

    @BindView(R.id.imageView1)
    ImageView imageView1;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.imageView4)
    ImageView imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        ButterKnife.bind(this);

        String bundle = getIntent().getStringExtra("parameter1");
        if (bundle !=null) Toast.makeText(this, "Hello: " + bundle, Toast.LENGTH_LONG).show();

        /**
         * RETROFIT 2
         * SPRING BASIC AUTH
         */
        String stringBaseAuth = AppConfig.BASIC_AUTH_USERNAME + ":" + AppConfig.BASIC_AUTH_PASSWORD;
        authHeader = "Basic " + Base64.encodeToString(stringBaseAuth.getBytes(), Base64.NO_WRAP);

        //USING GLIDE
        String url = AppConfig.BASE_URL + "downloadFile/abc.jpg";
        GlideUrl glideUrl = new GlideUrl(url,
                new LazyHeaders.Builder()
                        .addHeader("Authorization", authHeader)
                        //                                .addHeader("Cookie", AUTHORIZATION)
                        //                                .addHeader("Accept", ABC)
                        .build());

        /**
         * Glide Caching Srategy
         * https://android.jlelse.eu/best-strategy-to-load-images-using-glide-image-loading-library-for-android-e2b6ba9f75b2
         */
        RequestOptions requestOptions = RequestOptions
                .diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(this)
                .load(glideUrl)
                .circleCrop()
                .apply(requestOptions)
                .apply(new RequestOptions().override(300, 300))
                .into(imageView1);
        Glide.with(this)
                .load(glideUrl)
                .circleCrop()
                .apply(requestOptions)
                .apply(new RequestOptions().override(300, 300))
                .into(imageView2);
        Glide.with(this)
                .load(glideUrl)
                .circleCrop()
                .apply(requestOptions)
                .apply(new RequestOptions().override(300, 300))
                .into(imageView3);


        /**
         * TRY LOAD INDICATOR
         */
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .circleCrop()
                .override(400, 400)
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_broken_image_24)
                .priority(Priority.HIGH);

//        Glide.with(this)
//                .load(glideUrl)
//                .circleCrop()
//                .apply(requestOptions)
//                .apply(new RequestOptions().override(300, 300))
//                .into(imageView4);

        new GlideImageLoader(imageView4, null).load(url, options);

    }
}