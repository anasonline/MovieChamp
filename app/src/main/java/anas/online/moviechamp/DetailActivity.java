package anas.online.moviechamp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import anas.online.moviechamp.rest.ApiInterface;
import anas.online.moviechamp.rest.RetrofitClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity {

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";
    private static final String BACKDROP_SIZE = "w780";
    ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
    int mMovieId;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.iv_backdrop)
    ImageView backdrop;
    @BindView(R.id.iv_poster)
    ImageView poster;
    @BindView(R.id.iv_star)
    ImageView star;
    @BindView(R.id.tv_plot)
    TextView plot;
    @BindView(R.id.tv_releaseDate)
    TextView releaseDate;
    @BindView(R.id.tv_rating)
    TextView rating;
    @BindView(R.id.fab_favorite)
    FloatingActionButton fab_favorite;
    private List<Review> mReviews;
    private List<Video> mVideos;
    private ReviewAdapter mReviewAdapter;
    private VideoAdapter mVideoAdapter;
    private RecyclerView mVideosRecyclerView;
    private RecyclerView mReviewRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        mVideosRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);

        Movie mMovieData = getIntent().getExtras().getParcelable("EXTRA_MOVIE");

        title.setText(mMovieData.getTitle());
        plot.setText(mMovieData.getOverview());
        releaseDate.setText(mMovieData.getReleaseDate());
        rating.setText(mMovieData.getVoteAverage().toString());

        String fullBackdropPath = BASE_IMAGE_URL + BACKDROP_SIZE + mMovieData.getBackdropPath();
        Picasso.with(this).load(fullBackdropPath).into(backdrop);

        // Get poster path and load the image with Picasso
        String fullPosterPath = BASE_IMAGE_URL + POSTER_SIZE + mMovieData.getPosterPath();
        Picasso.with(this).load(fullPosterPath).into(poster);

        mMovieId = mMovieData.getId();


        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);

        loadReviews();
        loadTrailers();

    }

    public void loadTrailers() {
        Call<Video> call = apiService.getMovieVideos(mMovieId, BuildConfig.TMDB_API_KEY);

        call.enqueue(new Callback<Video>() {

            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                mVideos = response.body().getVideosList();

                mVideoAdapter = new VideoAdapter(mVideos, R.layout.item_trailer, getApplicationContext());
                mVideosRecyclerView.setAdapter(mVideoAdapter);
                mVideosRecyclerView.setHasFixedSize(false);
                mVideosRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

            }

            @Override
            public void onFailure(Call<Video> call, Throwable error) {
                // Log error here since request failed
                Log.e("ERROR", error.toString());
            }
        });
    }

    public void loadReviews() {
        Call<Review> call = apiService.getMovieReviews(mMovieId, BuildConfig.TMDB_API_KEY);

        call.enqueue(new Callback<Review>() {

            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                mReviews = response.body().getReviewsList();

                mReviewAdapter = new ReviewAdapter(mReviews, R.layout.item_review, getApplicationContext());
                mReviewRecyclerView.setAdapter(mReviewAdapter);
                mReviewRecyclerView.setHasFixedSize(false);
                mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

            }

            @Override
            public void onFailure(Call<Review> call, Throwable error) {
                // Log error here since request failed
                Log.e("ERROR", error.toString());
            }
        });
    }

}