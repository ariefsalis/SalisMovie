package com.salis.salismovie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.salis.salismovie.adapter.MovieAdapter;
import com.salis.salismovie.api.Client;
import com.salis.salismovie.api.Service;
import com.salis.salismovie.model.Movie;
import com.salis.salismovie.model.MovieResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> moviesList;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    public static final String LOG_TAG = MovieAdapter.class.getName();


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState){
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      initViews();

      swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
      swipeContainer.setColorSchemeColors(android.R.color.holo_orange_dark);
      swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
              initViews();
              Toast.makeText(MainActivity.this,"Movie Refreshed",Toast.LENGTH_SHORT).show();
          }
      });
    }

    public Activity getActivity(){
        Context context = this;
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    private void initViews(){
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Movies..");
        pd.setCancelable(false);
        pd.show();

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);

        moviesList = new ArrayList<>();
        adapter = new MovieAdapter(this, moviesList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadJSON();

    }

    private void loadJSON(){
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(),"Please Obtain API key fristly from themoviedb.org",Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }
            Client client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<MovieResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie>movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(getApplicationContext(),movies));
//                    Log.d("Data ", "" + response.body().getResults());
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(MainActivity.this,"Error Fething Data",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
