package com.pby.user.moviepby;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final static String TAGBEFORE = "before";
    private final static String TAGAFTER = "after";

    private Context mContext;
    private Map<Integer, Movie> mCachedMovies;
    private ArrayList<String> mLinkArrayList;
    private ArrayList<String> mTitleArrayList;
    private ArrayList<Bitmap> mImageArrayList;
    private ArrayList<String> mUserRatingArrayList;
    private ArrayList<String> mPubDateArrayList;
    private ArrayList<String> mDirectorArrayList;
    private ArrayList<String> mActorArrayList;

    public RecyclerAdapter(Context context, Map<Integer, Movie> cachedMovies) {
        Log.d("test-new Adapter", TAGBEFORE);
        mContext = context;

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
        Log.d("test-loadMovie", mCachedMovies.size() + TAGBEFORE);
        Iterator<Movie> movieListCopy = mCachedMovies.values().iterator();
        while (movieListCopy.hasNext()) {
            Movie mealCopy = movieListCopy.next();
            mLinkArrayList.add(mealCopy.getmLink());
            mTitleArrayList.add(mealCopy.getmTitle());
            mImageArrayList.add(mealCopy.getmImage());
            mUserRatingArrayList.add(mealCopy.getmUserRating());
            mPubDateArrayList.add(mealCopy.getmPubDate());
            mDirectorArrayList.add(mealCopy.getmDirector());
            mActorArrayList.add(mealCopy.getmActor());
            Log.d("test-loadMovie", TAGAFTER);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("test-onCreateViewHolder", TAGBEFORE);
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_cardmovie, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        Log.d("test-onCreateViewHolder", TAGAFTER);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {
        Log.d("test-onBindViewHolder", "before");
        holder.mLinearLayoutMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLinkArrayList.get(position).equals("")) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(mContext, Uri.parse(mLinkArrayList.get(position)));
                }
            }
        });
        holder.mImageViewImage.setImageBitmap(mImageArrayList.get(position));
        if (holder.mImageViewImage.getDrawable() == null) {
            holder.mImageViewImage.setImageResource(R.drawable.ic_launcher_background);
        }
        holder.mTextViewTitle.setText(Html.fromHtml(mTitleArrayList.get(position)));
        holder.mTextViewUserRating.setText(Html.fromHtml(mUserRatingArrayList.get(position)));
        holder.mTextViewPubDate.setText(Html.fromHtml(mPubDateArrayList.get(position)));
        holder.mTextViewDirector.setText(Html.fromHtml(mDirectorArrayList.get(position)));
        holder.mTextViewActor.setText(Html.fromHtml(mActorArrayList.get(position)));
        Log.d("test-onBindViewHolder", mDirectorArrayList.get(position) + "after1");
        Log.d("test-onBindViewHolder", holder.mTextViewDirector.getText() + "after2");
    }

    @Override
    public int getItemCount() {
        return mCachedMovies.size();
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
