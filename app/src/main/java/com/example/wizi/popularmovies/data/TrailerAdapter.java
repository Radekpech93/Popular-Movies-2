package com.example.wizi.popularmovies.data;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wizi.popularmovies.DetailActivity;
import com.example.wizi.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {

    private static final String MOVIE_TRAILER_KEY = "MOVIE_TRAILER";

    private List<Trailer> mTrailers;

    Context mContext;

    public TrailerAdapter(Context context, List<Trailer> trailers) {
        mContext = context;
        mTrailers = trailers;
    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }


    class TrailerHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageButton trailerButton;
        TextView trailerLabel;

        TrailerHolder(View itemView) {
            super(itemView);

            trailerButton = itemView.findViewById(R.id.ib_trailer);
            trailerLabel = itemView.findViewById(R.id.tv_trailer_label);

            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            int trailerNumber = position + 1;
            trailerLabel.setText("Trailer " + trailerNumber);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            String trailerKey = mTrailers.get(position).getKey();

            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + trailerKey));

            try {
                mContext.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                mContext.startActivity(webIntent);
            }
        }
    }

    public void setTrailerData(List<Trailer> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    public void saveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_TRAILER_KEY, (ArrayList<? extends Parcelable>) mTrailers);
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(MOVIE_TRAILER_KEY)) {
            ArrayList<? extends Trailer> savedTrailers = savedInstanceState.getParcelableArrayList(MOVIE_TRAILER_KEY);
            mTrailers.clear();
            mTrailers.addAll(savedTrailers);
            notifyDataSetChanged();
        }
    }
}
