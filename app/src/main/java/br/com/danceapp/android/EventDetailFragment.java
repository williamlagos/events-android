package br.com.danceapp.android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import br.com.danceapp.android.model.Event;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment {

    public static final String TAG = EventDetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_PARCEL = "item_parcel";

    /**
     * The content this fragment is presenting.
     */
    private Event mItem;

    private SimpleDateFormat mLocalDateFormatter = new SimpleDateFormat("EEEE, dd 'de' MMMM\n'às' HH:mm");
    private Button mInterestButton;
    private Button mGoingButton;
    private boolean mUserIsInterested = false;
    private boolean mUserIsAttending = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_PARCEL)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = getArguments().getParcelable(ARG_ITEM_PARCEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_detail, container, false);

        if (mItem != null) {
            final Activity activity = getActivity();
            final ImageView coverImageView = (ImageView) rootView.findViewById(R.id.eventCoverImage);
            final View separatorView = (View) rootView.findViewById(R.id.separator);

            if (mItem.getCoverImageUrl() == null || mItem.getCoverImageUrl().isEmpty()) {
                coverImageView.setVisibility(View.GONE);
                separatorView.setVisibility(View.GONE);
            } else {
                Picasso.with(activity)
                        .load(mItem.getCoverImageUrl())
                        .into(coverImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                coverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                coverImageView.setAdjustViewBounds(true);
                            }

                            @Override
                            public void onError() {
                                coverImageView.setVisibility(View.GONE);
                                separatorView.setVisibility(View.GONE);
                            }
                        });
            }

            ((TextView) rootView.findViewById(R.id.eventName)).setText(mItem.getName());

            if (mItem.getPlaceName() == null) {
                rootView.findViewById(R.id.place_name_holder).setVisibility(View.GONE);
            } else {
                ((TextView) rootView.findViewById(R.id.placeName)).setText(mItem.getPlaceName());
            }

            String placeAddress = "";
            if (mItem.getPlaceAddress() != null) {
                placeAddress += mItem.getPlaceAddress() + "\n";
            }
            if (mItem.getPlaceCity() != null) {
                placeAddress += mItem.getPlaceCity();
            }
            if (mItem.getPlaceState() != null) {
                placeAddress += " - " + mItem.getPlaceState();
            }
            ((TextView) rootView.findViewById(R.id.placeCity)).setText(placeAddress);

            String eventDate = mLocalDateFormatter.format(mItem.getStartTime());
            eventDate = eventDate.substring(0, 1).toUpperCase() + eventDate.substring(1);
            ((TextView) rootView.findViewById(R.id.eventDate)).setText(eventDate);

            if (mItem.getDescription() == null || mItem.getDescription().isEmpty()) {
                rootView.findViewById(R.id.description).setVisibility(View.GONE);
            } else {
                ((TextView) rootView.findViewById(R.id.description)).setText(mItem.getDescription());
            }

            String presenceText = "Até o momento";

            int interested = mItem.getInterestedCount();
            switch (interested) {
                case 0:
                    presenceText += " não há ninguém interessado";
                    break;

                case 1:
                    presenceText += " há uma pessoa interessada";
                    break;

                default:
                    presenceText += String.format(" há %d pessoas interessadas", interested);
                    break;
            }

            int attending = mItem.getAttendingCount();
            switch (attending) {
                case 0:
                    presenceText += " e ninguém confirmou presença.";
                    break;

                case 1:
                    presenceText += " e uma pessoa confirmou presença.";
                    break;

                default:
                    presenceText += String.format(" e %d pessoas confirmaram presença.", attending);
                    break;
            }

            ((TextView) rootView.findViewById(R.id.presence)).setText(presenceText);

            final Button button = (Button) rootView.findViewById(R.id.button_facebook);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String eventUrl = "https://www.facebook.com/events/" + mItem.getExternalId();
                    String facebookUrl = "fb://facewebmodal/f?href=" + eventUrl;

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(facebookUrl));

                    // Try launching first in Facebook, if it fails try the default chooser
                    try {
                        activity.startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        intent.setData(Uri.parse(eventUrl));
                        activity.startActivity(intent);
                    }

                    Activity activity = getActivity();
                    DanceAppApplication application = (DanceAppApplication) activity.getApplication();
                    application.trackEvent("Events", "OpenExternal", eventUrl);
                }
            });

        }

        // GRAPH
        mInterestButton = (Button)rootView.findViewById(R.id.button_interest);
        mInterestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken == null) {
                    Context context = getContext();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra(LoginActivity.SUCCESS_MESSAGE, getString(R.string.message_success_event_rsvp));
                    context.startActivity(intent);

                    return;
                }

                if (mUserIsInterested) {
                    GraphRequest request = GraphRequest.newPostRequest(
                            accessToken,
                            getGraphRsvpPath("declined"),
                            null,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    Log.d(TAG, "Declined successfully");
                                }
                            });
                    request.executeAsync();
                } else {
                    GraphRequest request = GraphRequest.newPostRequest(
                            accessToken,
                            getGraphRsvpPath("maybe"),
                            null,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    Log.d(TAG, "Interested successfully");
                                }
                            });
                    request.executeAsync();
                }
                mUserIsInterested = !mUserIsInterested;
                toggleInterestButton(mUserIsInterested);
                mUserIsAttending = false;
                toggleGoingButton(false);
            }
        });

        mGoingButton = (Button)rootView.findViewById(R.id.button_going);
        mGoingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken == null) {
                    Context context = getContext();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra(LoginActivity.SUCCESS_MESSAGE, getString(R.string.message_success_event_rsvp));
                    context.startActivity(intent);

                    return;
                }

                if (mUserIsAttending) {
                    GraphRequest request = GraphRequest.newPostRequest(
                            accessToken,
                            getGraphRsvpPath("declined"),
                            null,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    Log.d(TAG, "Declined successfully");
                                }
                            });
                    request.executeAsync();
                } else {
                    GraphRequest request = GraphRequest.newPostRequest(
                            accessToken,
                            getGraphRsvpPath("attending"),
                            null,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    Log.d(TAG, "Attending successfully");
                                }
                            });
                    request.executeAsync();
                }
                mUserIsAttending = !mUserIsAttending;
                toggleGoingButton(mUserIsAttending);
                mUserIsInterested = false;
                toggleInterestButton(false);
            }
        });

        toggleGoingButton(false);
        toggleInterestButton(false);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            GraphRequest interestedRequest = GraphRequest.newGraphPathRequest(
                    accessToken,
                    getGraphRsvpPath("maybe"),
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            try {
                                JSONArray data = response.getJSONObject().getJSONArray("data");
                                mUserIsInterested = (data.length() > 0);
                            } catch (JSONException e) {
                                mUserIsInterested = false;
                            }

                            toggleInterestButton(mUserIsInterested);

                            Log.d(TAG, "User is interested: " + mUserIsInterested);
                        }
                    });
            interestedRequest.executeAsync();

            GraphRequest attendingRequest = GraphRequest.newGraphPathRequest(
                    accessToken,
                    getGraphRsvpPath("attending"),
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            try {
                                JSONArray data = response.getJSONObject().getJSONArray("data");
                                mUserIsAttending = (data.length() > 0);
                            } catch (JSONException e) {
                                mUserIsAttending = false;
                            }

                            toggleGoingButton(mUserIsAttending);

                            Log.d(TAG, "User is attending: " + mUserIsAttending);
                        }
                    });
            attendingRequest.executeAsync();
        }

        String name = getString(R.string.analytics_event_detail_screen);
        Log.i(TAG, "Setting screen name: " + name);
        Activity activity = getActivity();
        DanceAppApplication application = (DanceAppApplication) activity.getApplication();
        application.trackScreenView(name);

        return rootView;
    }

    void toggleInterestButton(boolean on) {
        int iconId = 0;
        int colorId = 0;

        if (on) {
            iconId =  R.drawable.ic_grade_tinted_18dp;
            colorId = R.color.neoncarrot_700;
        } else {
            iconId =  R.drawable.ic_grade_black_18dp;
            colorId = android.R.color.primary_text_light;
        }

        setButtonStyle(mInterestButton, iconId, colorId);
    }

    void toggleGoingButton(boolean on) {
        int iconId = 0;
        int colorId = 0;

        if (on) {
            iconId =  R.drawable.ic_done_tinted_18dp;
            colorId = R.color.neoncarrot_700;
        } else {
            iconId =  R.drawable.ic_done_black_18dp;
            colorId = android.R.color.primary_text_light;
        }

        setButtonStyle(mGoingButton, iconId, colorId);
    }

    private void setButtonStyle(Button button, int iconId, int colorId) {
        Drawable icon = ContextCompat.getDrawable(getContext(), iconId);
        Resources r = getResources();
        float pxs = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, r.getDisplayMetrics());
        icon.setBounds(0, 0, (int)pxs, (int)pxs);
        button.setCompoundDrawables(null, icon, null, null);
        int color = ResourcesCompat.getColor(getResources(), colorId, null);
        button.setTextColor(color);
    }

    private String getGraphRsvpPath(String rsvpStatus) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            return null;
        }

        return mItem.getExternalId() + "/" + rsvpStatus + "/" + accessToken.getUserId();
    }
}
