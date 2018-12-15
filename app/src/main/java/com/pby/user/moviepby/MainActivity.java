package com.pby.user.moviepby;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button mButtonMovieSearch;
    private EditText mEditTextMovieSearch;
    private RecyclerView mRcyclerViewMovieList;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private RecyclerView.Adapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mButtonMovieSearch = (Button) findViewById(R.id.buttonMovieSearch);
        mEditTextMovieSearch = (EditText) findViewById(R.id.editTextMovieSearch);
        mRcyclerViewMovieList = (RecyclerView) findViewById(R.id.recyclerViewMovieList);

        mButtonMovieSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callMovieSearchAPI();

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
    }

    public final void setRecyclerAdapter(RecyclerView.Adapter recyclerAdapter) {
        Log.d("test-setRecyclerAdapter", "before");
        mRecyclerViewAdapter = recyclerAdapter;
        Log.d("test-setRecyclerAdapter", mRecyclerViewAdapter + "after");
    }

    public final void setRecyclerView() {
        Log.d("test-setRecyclerView", "before");
        mRecyclerViewLayoutManager = new LinearLayoutManager(this);
        mRcyclerViewMovieList.setLayoutManager(mRecyclerViewLayoutManager);
        mRcyclerViewMovieList.setAdapter(mRecyclerViewAdapter);
        Log.d("test-setRecyclerView", mRecyclerViewAdapter + "after");
    }

    public final void refreshRecyclerView() {
        Log.d("test-refresh", "before");
        mRecyclerViewAdapter.notifyDataSetChanged();
        Log.d("test-refresh", mRecyclerViewAdapter + "after");
    }

    private void callMovieSearchAPI() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // All your networking logic
                // should be here
                String clientId = "IT9hEd58EVBQaYX18Ot4";
                String clientSecret = "MZTgAOHFee";

                URL apiEndpoint = null;
                try {
                    String movieSearchText = mEditTextMovieSearch.getText().toString();
                    Log.d("test-movieSearchText", movieSearchText);
                    String text = URLEncoder.encode(movieSearchText, "UTF-8");
                    String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text; // json 결과
                    // Create URL
                    // 클라우드 url에는 port를 안붙인다.
                    apiEndpoint = new URL(apiURL);

                    // Create connection HttpURLConnection or HttpsURLConnection
                    HttpURLConnection myConnection =
                            (HttpURLConnection) apiEndpoint.openConnection();

                    Log.d("test-myConnection", myConnection.toString());

                    myConnection.setRequestMethod("GET");
                    myConnection.setRequestProperty("X-Naver-Client-Id", clientId);
                    myConnection.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                    int responseCode = myConnection.getResponseCode();
                    Log.d("test-responseCode", Integer.toString(responseCode));

                    BufferedReader br = null;
                    if (responseCode == 200) { // 정상 호출
                        br = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                    } else {  // 에러 발생
                        br = new BufferedReader(new InputStreamReader(myConnection.getErrorStream()));
                    }

                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }

                    Log.d("test-response", response.toString());

                    JSONObject jsonObj = new JSONObject(response.toString());
                    final JSONArray movieList = jsonObj.getJSONArray("items");

                    br.close();
                    myConnection.disconnect();

                    final Map<Integer, MovieObject> mCachedMovies = new LinkedHashMap<>();
                    MovieObject movieObject = null;
                    JSONObject movie = null;
                    String image = null;
                    URL imageURL = null;
                    HttpURLConnection connection = null;
                    Bitmap bmp = null;

                    for (int movieListIdx = 0; movieListIdx < movieList.length(); ++movieListIdx) {
                        movie = movieList.getJSONObject(movieListIdx);
                        image = movie.getString("image");
                        if(image.length() > 1){
                            imageURL = new URL(movie.getString("image"));
                            connection = (HttpURLConnection) imageURL.openConnection();
                            bmp = BitmapFactory.decodeStream(connection.getInputStream());
                        }else{
                            bmp = null;
                        }

                        movieObject = new MovieObject(
                                movie.getString("link"),
                                bmp,
                                movie.getString("title"),
                                movie.getString("userRating"),
                                movie.getString("pubDate"),
                                movie.getString("director"),
                                movie.getString("actor"));
                        mCachedMovies.put(movieListIdx, movieObject);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("test-runOnUiThread", "before");
                            mRecyclerViewAdapter = new RecyclerAdapter(mCachedMovies);
                            setRecyclerAdapter(mRecyclerViewAdapter);
                            setRecyclerView();
                            refreshRecyclerView();
                            Log.d("test-runOnUiThread", "after");
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public final class MovieObject {
        @Nullable
        private String mLink;

        @Nullable
        private String mTitle;

        @Nullable
        private Bitmap mImage;

        @Nullable
        private String mUserRating;

        @Nullable
        private String mPubDate;

        @Nullable
        private String mDirector;

        @Nullable
        private String mActor;

        public MovieObject() {
        }

        public MovieObject(@Nullable String link, @Nullable Bitmap image,
                           @Nullable String title, @Nullable String userRating,
                           @Nullable String pubDate, @Nullable String director, @Nullable String actor) {
            mLink = link;
            mTitle = title;
            mImage = image;
            mUserRating = userRating;
            mPubDate = pubDate;
            mDirector = director;
            mActor = actor;
        }

        @Nullable
        public String getmLink() {
            return mLink;
        }

        @Nullable
        public String getmTitle() {
            return mTitle;
        }

        @Nullable
        public Bitmap getmImage() {
            return mImage;
        }

        @Nullable
        public String getmUserRating() {
            return mUserRating;
        }

        @Nullable
        public String getmPubDate() {
            return mPubDate;
        }

        @Nullable
        public String getmDirector() {
            return mDirector;
        }

        @Nullable
        public String getmActor() {
            return mActor;
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private Map<Integer, MovieObject> mCachedMovies;
        private ArrayList<String> mLinkArrayList;
        private ArrayList<String> mTitleArrayList;
        private ArrayList<Bitmap> mImageArrayList;
        private ArrayList<String> mUserRatingArrayList;
        private ArrayList<String> mPubDateArrayList;
        private ArrayList<String> mDirectorArrayList;
        private ArrayList<String> mActorArrayList;

        public RecyclerAdapter(Map<Integer, MovieObject> cachedMovies) {
            Log.d("test-new Adapter", "before");
            mCachedMovies = cachedMovies;
            if (mLinkArrayList == null) mLinkArrayList = new ArrayList<>();
            if (mTitleArrayList == null) mTitleArrayList = new ArrayList<>();
            if (mImageArrayList == null) mImageArrayList = new ArrayList<>();
            if (mUserRatingArrayList == null) mUserRatingArrayList = new ArrayList<>();
            if (mPubDateArrayList == null) mPubDateArrayList = new ArrayList<>();
            if (mDirectorArrayList == null) mDirectorArrayList = new ArrayList<>();
            if (mActorArrayList == null) mActorArrayList = new ArrayList<>();

            loadMovieToArrayList();
        }

        public void loadMovieToArrayList() {
            Log.d("test-loadMovie", mCachedMovies.size() + "before");
            Iterator<MovieObject> movieListCopy = mCachedMovies.values().iterator();
            while (movieListCopy.hasNext()) {
                MovieObject mealCopy = movieListCopy.next();
                mLinkArrayList.add(mealCopy.getmLink());
                mTitleArrayList.add(mealCopy.getmTitle());
                mImageArrayList.add(mealCopy.getmImage());
                mUserRatingArrayList.add(mealCopy.getmUserRating());
                mPubDateArrayList.add(mealCopy.getmPubDate());
                mDirectorArrayList.add(mealCopy.getmDirector());
                mActorArrayList.add(mealCopy.getmActor());
                Log.d("test-loadMovie", "after");
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            Log.d("test-onCreateViewHolder", "before");
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_cardmovie, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            Log.d("test-onCreateViewHolder", "after");
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {
            Log.d("test-onBindViewHolder", "before");
            holder.mLinearLayoutMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mLinkArrayList.get(position).equals("")){
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(getApplicationContext(), Uri.parse(mLinkArrayList.get(position)));
                    }
                }
            });
            holder.mImageViewImage.setImageBitmap(mImageArrayList.get(position));
            if (holder.mImageViewImage.getDrawable() == null) {
                holder.mImageViewImage.setImageResource(R.drawable.ic_launcher_background);
            }
            holder.mTextViewTitle.setText(Html.fromHtml(mTitleArrayList.get(position)));
            holder.mTextViewUserRating.setText(mUserRatingArrayList.get(position));
            holder.mTextViewPubDate.setText(mPubDateArrayList.get(position));
            holder.mTextViewDirector.setText(mDirectorArrayList.get(position));
            holder.mTextViewActor.setText(mActorArrayList.get(position));
            Log.d("test-onBindViewHolder", mDirectorArrayList.get(position) + "after1");
            Log.d("test-onBindViewHolder", holder.mTextViewDirector.getText() + "after2");
        }

        @Override
        public int getItemCount() {
            return mLinkArrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public LinearLayout mLinearLayoutMovie;
            public ImageView mImageViewImage;
            public TextView mTextViewTitle;
            public TextView mTextViewUserRating;
            public TextView mTextViewPubDate;
            public TextView mTextViewDirector;
            public TextView mTextViewActor;

            public ViewHolder(View itemView) {
                super(itemView);
                mLinearLayoutMovie = (LinearLayout) itemView.findViewById(R.id.linearLayoutMovie);
                mImageViewImage = (ImageView) itemView.findViewById(R.id.imageViewImage);
                mTextViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                mTextViewUserRating = (TextView) itemView.findViewById(R.id.textViewUserRating);
                mTextViewPubDate = (TextView) itemView.findViewById(R.id.textViewPubDate);
                mTextViewDirector = (TextView) itemView.findViewById(R.id.textViewDirector);
                mTextViewActor = (TextView) itemView.findViewById(R.id.textViewActor);
            }
        }
    }
}
