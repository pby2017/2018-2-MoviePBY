package com.pby.user.moviepby;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button mButtonMovieSearch;
    private EditText mEditTextMovieSearch;
    private RecyclerView mRcyclerViewMovieList;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private RecyclerView.Adapter mRecyclerViewAdapter;

    private final static String IMAGE = "image";
    private final static String LINK = "link";
    private final static String TITLE = "title";
    private final static String USERRATING = "userRating";
    private final static String PUBDATE = "pubDate";
    private final static String DIRECTOR = "director";
    private final static String ACTOR = "actor";

    private final static String TAGBEFORE = "before";
    private final static String TAGAFTER = "after";

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
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
    }

    public final void setRecyclerAdapter(RecyclerView.Adapter recyclerAdapter) {
        Log.d("test-setRecyclerAdapter", TAGBEFORE);
        mRecyclerViewAdapter = recyclerAdapter;
        Log.d("test-setRecyclerAdapter", mRecyclerViewAdapter + TAGAFTER);
    }

    public final void setRecyclerView() {
        Log.d("test-setRecyclerView", TAGBEFORE);
        mRecyclerViewLayoutManager = new LinearLayoutManager(this);
        mRcyclerViewMovieList.setLayoutManager(mRecyclerViewLayoutManager);
        mRcyclerViewMovieList.setAdapter(mRecyclerViewAdapter);
        Log.d("test-setRecyclerView", mRecyclerViewAdapter + TAGAFTER);
    }

    public final void refreshRecyclerView() {
        Log.d("test-refresh", TAGBEFORE);
        mRecyclerViewAdapter.notifyDataSetChanged();
        Log.d("test-refresh", mRecyclerViewAdapter + TAGAFTER);
    }

    private String getMovieSearchURL() throws UnsupportedEncodingException {
        String movieSearchText = mEditTextMovieSearch.getText().toString();
        Log.d("test-movieSearchText", movieSearchText);
        String text = URLEncoder.encode(movieSearchText, "UTF-8");
        String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text; // json 결과

        return apiURL;
    }

    private HttpURLConnection makeMovieSearchConnection(URL apiEndPoint) throws IOException {
        String clientId = "";
        String clientSecret = "";
        HttpURLConnection myConnection =
                (HttpURLConnection) apiEndPoint.openConnection();

        Log.d("test-myConnection", myConnection.toString());

        myConnection.setRequestMethod("GET");
        myConnection.setRequestProperty("X-Naver-Client-Id", clientId);
        myConnection.setRequestProperty("X-Naver-Client-Secret", clientSecret);
        return myConnection;
    }

    private Map<Integer, Movie> makeCachedMoviesForRecycle(JSONArray movieList) throws JSONException, IOException {
        Map<Integer, Movie> mCachedMovies = new LinkedHashMap<>();

        for (int movieListIdx = 0; movieListIdx < movieList.length(); ++movieListIdx) {
            JSONObject movie = movieList.getJSONObject(movieListIdx);

            Bitmap bmp = find(movie);

            Movie movieModel = makeMovie(movie, bmp);

            mCachedMovies.put(movieListIdx, movieModel);
        }

        return mCachedMovies;
    }

    private Bitmap find(JSONObject movie) throws JSONException, IOException {
        String image = movie.getString(IMAGE);
        if (image.length() > 1) {
            URL imageURL = new URL(image);
            HttpURLConnection myConnection = (HttpURLConnection) imageURL.openConnection();
            return BitmapFactory.decodeStream(myConnection.getInputStream());
        }
        return null;
    }

    private Movie makeMovie(JSONObject movie, Bitmap bmp) throws JSONException {

        return new Movie(
                movie.getString(LINK),
                bmp,
                movie.getString(TITLE),
                movie.getString(USERRATING),
                movie.getString(PUBDATE),
                movie.getString(DIRECTOR),
                movie.getString(ACTOR));
    }

    private void callMovieSearchAPI() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // All your networking logic
                // should be here
                BufferedReader br = null;

                try {
                    // Create URL
                    // 클라우드 url에는 port를 안붙인다.
                    URL apiEndpoint = new URL(getMovieSearchURL());

                    HttpURLConnection myConnection = makeMovieSearchConnection(apiEndpoint);

                    int responseCode = myConnection.getResponseCode();

                    Log.d("test-responseCode", Integer.toString(responseCode));

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

                    final Map<Integer, Movie> mCachedMovies = makeCachedMoviesForRecycle(movieList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("test-runOnUiThread", TAGBEFORE);

                            mRecyclerViewAdapter = new RecyclerAdapter(getApplicationContext(), mCachedMovies);

                            setRecyclerAdapter(mRecyclerViewAdapter);
                            setRecyclerView();
                            refreshRecyclerView();
                            Log.d("test-runOnUiThread", TAGAFTER);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
