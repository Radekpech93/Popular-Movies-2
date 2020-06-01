package com.example.wizi.popularmovies.data;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wizi.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private static final String MOVIE_REVIEW_KEY = "MOVIE_REVIEW";

    private List<Review> mReviews = new ArrayList<>();

    private final Context mContext;

    public ReviewAdapter(Context context, List<Review> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        TextView listItemReviewLabel;
        TextView listItemReviewContent;

        ReviewHolder(View itemView) {
            super(itemView);

            listItemReviewLabel = itemView.findViewById(R.id.tv_review_author);
            listItemReviewContent = itemView.findViewById(R.id.tv_review_content);
        }

        void bind(int position) {
            listItemReviewLabel.setText(mReviews.get(position).getAuthor());
            listItemReviewContent.setText(mReviews.get(position).getContent());
        }
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentMmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentMmediately);

        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder reviewHolder, int position) {
        reviewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void setReviewData(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public void saveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_REVIEW_KEY, (ArrayList<? extends Parcelable>) mReviews);
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(MOVIE_REVIEW_KEY)) {
            List<Review> savedReviews = savedInstanceState.getParcelableArrayList(MOVIE_REVIEW_KEY);
            mReviews.clear();
            mReviews.addAll(savedReviews);
            notifyDataSetChanged();
        }
    }
}
