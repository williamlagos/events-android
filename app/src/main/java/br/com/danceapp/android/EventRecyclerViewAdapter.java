package br.com.danceapp.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import br.com.danceapp.android.model.Event;

public class EventRecyclerViewAdapter
        extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private SimpleDateFormat mLocalDateFormatter = new SimpleDateFormat("EEEE\ndd/MM/yyyy");

    private final List<Event> mItems;
    private Context mContext;
    private boolean mTwoPane = false;
    private FragmentManager mFragmentManager = null;

    public EventRecyclerViewAdapter(List<Event> items, Context context) {
        mItems = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Event event = mItems.get(position);
        String eventDate = mLocalDateFormatter.format(event.getStartTime());
        eventDate = eventDate.substring(0, 1).toUpperCase() + eventDate.substring(1);

        holder.mItem = event;
        holder.mEventNameView.setText(event.getName());
        holder.mCityNameView.setText(event.getPlaceCity());
        holder.mEventDateView.setText(eventDate);

        if (event.getCoverImageUrl() == null || event.getCoverImageUrl().isEmpty()) {
            holder.mCoverImageView.setVisibility(View.GONE);
            holder.mSeparatorView.setVisibility(View.GONE);
        } else {
            Picasso.with(mContext)
                    .load(event.getCoverImageUrl())
                    .noFade()
                    .into(holder.mCoverImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            holder.mCoverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            holder.mCoverImageView.setAdjustViewBounds(true);
                        }

                        @Override
                        public void onError() {
                            holder.mCoverImageView.setVisibility(View.GONE);
                            holder.mSeparatorView.setVisibility(View.GONE);
                        }
                    });
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(EventDetailFragment.ARG_ITEM_PARCEL, holder.mItem);
                    EventDetailFragment fragment = new EventDetailFragment();
                    fragment.setArguments(arguments);
                    mFragmentManager.beginTransaction()
                            .replace(R.id.event_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra(EventDetailFragment.ARG_ITEM_PARCEL, holder.mItem);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    void enableTwoPaneMode(FragmentManager fragmentManager) {
        mTwoPane = true;
        mFragmentManager = fragmentManager;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mEventNameView;
        public final TextView mCityNameView;
        public final TextView mEventDateView;
        public final ImageView mCoverImageView;
        private final View mSeparatorView;

        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mEventNameView = (TextView) view.findViewById(R.id.eventName);
            mCityNameView = (TextView) view.findViewById(R.id.placeCity);
            mEventDateView = (TextView) view.findViewById(R.id.eventDate);
            mCoverImageView = (ImageView) view.findViewById(R.id.coverImage);
            mSeparatorView = view.findViewById(R.id.separator);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mEventNameView.getText() + "'";
        }
    }
}