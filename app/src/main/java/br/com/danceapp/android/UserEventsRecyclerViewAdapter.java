package br.com.danceapp.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * Created by Rafael on 09/01/2017.
 */

public class UserEventsRecyclerViewAdapter
        extends RecyclerView.Adapter<UserEventsRecyclerViewAdapter.ViewHolder> {

    private final List<String> mItems;
    private Context mContext;

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    public UserEventsRecyclerViewAdapter(List<String> items, Context context) {
        mItems = items;
        mContext = context;
        mDrawableBuilder = TextDrawable.builder()
                .round();
    }

    @Override
    public UserEventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userevents_item, parent, false);
        return new UserEventsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserEventsRecyclerViewAdapter.ViewHolder holder, int position) {
        String userEvent = mItems.get(position);

        holder.mItem = userEvent;
        holder.mNameView.setText(userEvent);

        TextDrawable drawable = mDrawableBuilder.build(String.valueOf(userEvent.charAt(0)),
                mColorGenerator.getColor(userEvent));
        holder.mIconView.setImageDrawable(drawable);

        //holder.view.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final ImageView mIconView;
        //private final View mSeparatorView;

        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.userevent_name);
            mIconView = (ImageView) view.findViewById(R.id.userevent_icon);
            //mSeparatorView = view.findViewById(R.id.separator);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
