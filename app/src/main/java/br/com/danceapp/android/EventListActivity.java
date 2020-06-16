package br.com.danceapp.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import br.com.danceapp.android.model.Event;
import br.com.danceapp.android.service.ServiceBuilder;
import br.com.danceapp.android.service.ServiceInterface;
import br.com.danceapp.android.util.IgnoreAccentsArrayAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * An activity representing a list of Events. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EventDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EventListActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = EventListActivity.class.getSimpleName();

    private final static int COARSE_LOCATION_CHECK = 100;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private CoordinatorLayout mCoordinatorLayout;

    private int mProgressCount;
    private boolean isSearchOpened = false;
    private AutoCompleteTextView mCitiesAutocomplete;
    private List<String> mCitiesList;

    private SimpleDateFormat mServerDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private boolean mLoadedNearEvents;
    private ImageButton mSearchButton;
    private ImageButton mNearbyEventsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset theme to default AppTheme.NoActionBar, hiding branded launch theme
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_list);

        resetProgressIndicator();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();

        loadCitiesList();

        if (findViewById(R.id.event_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        performSocialLogin();


        // TESTE
        Context context = this;
        Intent intent = new Intent(context, UserEventsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String name = getString(R.string.analytics_event_list_screen);
        Log.i(TAG, "Setting screen name: " + name);
        DanceAppApplication application = (DanceAppApplication)getApplication();
        application.trackScreenView(name);
    }

    /**
     * Requests the coarse location permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCoarseLocationPermission() {
        Log.d(TAG, "Requesting coarse location permission...");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.d(TAG, "Displaying coarse location permission rationale...");
            showMessageOKCancel("O DanceApp precisa acessar sua localização aproximada para mostrar os eventos de dança próximos a você.\n\nDeseja que o aplicativo utilize sua localização aproximada?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(EventListActivity.this, new String[]{ACCESS_COARSE_LOCATION}, COARSE_LOCATION_CHECK);
                        }
                    });
            return;
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, COARSE_LOCATION_CHECK);
        }
    }

    private void retrieveLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestCoarseLocationPermission();
            return;
        }

        Log.d(TAG, "retrieveLastLocation: Getting last known location...");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        loadNearEvents(mLastLocation);
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        retrieveLastLocation();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String errorMessage = "";
        if (connectionResult.getErrorMessage() == null) {
            errorMessage = "Ocorreu um erro inesperado ao conectar à Google Play Services.";
        } else {
            errorMessage = "Ocorreu um erro ao conectar à Google Play Services: " +
                    connectionResult.getErrorMessage();
        }

        Log.d(TAG, "Location services connection failed: " + connectionResult.getErrorCode());
        Snackbar.make(mCoordinatorLayout, errorMessage, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == COARSE_LOCATION_CHECK) {

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Coarse location permission granted.");
                try {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        Log.d(TAG, "mLastLocation: " + mLastLocation.toString());
                        loadNearEvents(mLastLocation);
                    }
                } catch (SecurityException e) {
                    // Ignore this exception
                }
            } else {
                Log.d(TAG, "Coarse location permission denied.");
            }

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Add listeners on the imagebuttons of the menu items, since we are not using
        // regular menu items and we cannot use onOptionsItemSelected to handle them

        MenuItem menuItem = menu.findItem(R.id.action_search_cities);
        View actionView = menuItem.getActionView();
        mSearchButton = (ImageButton) actionView.findViewById(R.id.imagebutton_search_cities);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSearch();
            }
        });

        menuItem = menu.findItem(R.id.action_nearby_events);
        actionView = menuItem.getActionView();
        mNearbyEventsButton = (ImageButton) actionView.findViewById(R.id.imagebutton_nearby_events);
        mNearbyEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
                mLoadedNearEvents = false;
                loadNearEvents(mLastLocation);
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            menuItem = menu.findItem(R.id.action_login);
            menuItem.setTitle(R.string.action_logout);
        }

        performOnboarding();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_login) {
            if (AccessToken.getCurrentAccessToken() == null) {
                Context context = this;
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra(LoginActivity.SUCCESS_MESSAGE, getString(R.string.message_success_event_rsvp));
                context.startActivity(intent);
            } else {
                showConfirmationAlert(getString(R.string.title_logout_confirmation), getString(R.string.message_logout_confirmation), getString(R.string.action_logout), getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        item.setTitle(R.string.action_login);
                        mLoadedNearEvents = false;
                        loadNearEvents(mLastLocation);
                    }
                });
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void toggleSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) {
            closeSearch();
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.cities_search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            mCitiesAutocomplete = (AutoCompleteTextView) action.getCustomView().findViewById(R.id.cities_autocomplete); //the text editor

            if (mCitiesList != null) {
                IgnoreAccentsArrayAdapter<String> adapter = new IgnoreAccentsArrayAdapter<String>(this,
                        R.layout.autocomplete_item, mCitiesList);

                mCitiesAutocomplete.setAdapter(adapter);
                mCitiesAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        performSearch();
                    }
                });
            }

            //this is a listener to do a search when the user clicks on search button
            mCitiesAutocomplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        performSearch();
                        return true;
                    }
                    return false;
                }
            });

            mCitiesAutocomplete.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mCitiesAutocomplete, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchButton.setImageResource(R.drawable.ic_close_white_24dp);

            isSearchOpened = true;
        }
    }

    private void performSearch() {
        loadCityEvents(mCitiesAutocomplete.getText().toString());
        closeSearch();
    }

    private void closeSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
        action.setDisplayShowTitleEnabled(true); //show the title in the action bar

        //hides the keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //add the search icon in the action bar
        mSearchButton.setImageResource(R.drawable.ic_search_white_24dp);

        isSearchOpened = false;
    }

    private void loadCityEvents(final String cityName) {
        showProgressIndicator();

        DanceAppApplication application = (DanceAppApplication) getApplication();
        application.trackEvent("Events", "SearchByCity", cityName);

        ServiceInterface client = ServiceBuilder.getClient();

        String formattedDate = mServerDateFormatter.format(new Date());
        Call<List<Event>> listCall = client.nearEventsList(cityName, formattedDate);
        Log.d(TAG, listCall.request().url().toString());

        listCall.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                List<Event> eventsList = response.body();

                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.event_list);
                assert recyclerView != null;

                EventRecyclerViewAdapter adapter = new EventRecyclerViewAdapter(eventsList, getApplicationContext());
                if (mTwoPane) {
                    adapter.enableTwoPaneMode(getSupportFragmentManager());
                }
                recyclerView.setAdapter(adapter);

                setTitle(cityName);

                hideProgressIndicator();

                if (eventsList.size() == 0) {
                    Snackbar.make(mCoordinatorLayout, "Não foram encontrados eventos em " + cityName + ".", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                hideProgressIndicator();

                t.printStackTrace();
                Snackbar.make(mCoordinatorLayout, "Erro ao carregar eventos em " + cityName + ".", Snackbar.LENGTH_INDEFINITE).setAction("Recarregar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadCityEvents(cityName);
                    }
                }).show();
            }
        });
    }

    private void loadNearEvents(final Location location) {
        if (location == null) {
            Log.d(TAG, "loadNearEvents: location is null");
            Snackbar.make(mCoordinatorLayout, "Não foi possível determinar a sua localização.",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // 04:37 dirty fix...
        if (mLoadedNearEvents) {
            Log.e(TAG, "loadNearEvents: avoided a possibly unwanted reload");
            return;
        }

        Log.d(TAG, "loadNearEvents: location is " + location.toString());

        showProgressIndicator();
        mLoadedNearEvents = true;

        ServiceInterface client = ServiceBuilder.getClient();

        String formattedDate = mServerDateFormatter.format(new Date());
        Call<List<Event>> listCall = client.nearEventsList(location.getLatitude(), location.getLongitude(), 50, formattedDate);
        Log.d(TAG, listCall.request().url().toString());

        listCall.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                List<Event> eventsList = response.body();

                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.event_list);
                assert recyclerView != null;

                EventRecyclerViewAdapter adapter = new EventRecyclerViewAdapter(eventsList, getApplicationContext());
                if (mTwoPane) {
                    adapter.enableTwoPaneMode(getSupportFragmentManager());
                }

                recyclerView.setAdapter(adapter);

                setTitle(R.string.action_nearby_events);

                hideProgressIndicator();

                if (eventsList.size() == 0) {
                    Snackbar.make(mCoordinatorLayout, "Não foram encontrados eventos por perto.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                hideProgressIndicator();

                t.printStackTrace();
                Snackbar.make(mCoordinatorLayout, "Erro ao carregar eventos próximos.", Snackbar.LENGTH_INDEFINITE).setAction("Recarregar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadNearEvents(location);
                    }
                }).show();
            }
        });
    }

    private void loadCitiesList() {
        showProgressIndicator();

        ServiceInterface client = ServiceBuilder.getClient();

        Call<List<String>> listCall = client.citiesList();
        Log.d(TAG, listCall.request().url().toString());

        listCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                hideProgressIndicator();

                mCitiesList = response.body();
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                hideProgressIndicator();

                t.printStackTrace();
                Snackbar.make(mCoordinatorLayout, "Erro ao carregar cidades.", Snackbar.LENGTH_INDEFINITE).setAction("Recarregar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadCitiesList();
                    }
                }).show();
            }
        });
    }

    private void showProgressIndicator() {
        mProgressCount++;

        if (mProgressCount == 1) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressIndicator() {
        mProgressCount--;

        if (mProgressCount == 0) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void resetProgressIndicator() {
        mProgressCount = 0;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Showcase the main features of the application
     *
     * This method checks if the onboarding was performed before. In this case it will be skipped.
     */
    private void performOnboarding() {

        // Check if onboarding was already performed
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean performedOnboarding = sharedPref.getBoolean(getString(R.string.pref_performed_onboarding), false);

        if (performedOnboarding) {
            return;
        }

        // Hide the content view during onboarding, to avoid distractions
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.event_list);
        recyclerView.setVisibility(View.GONE);

        showCityEventsOnboarding();
    }

    /**
     * Show the city search events onboarding
     */
    private void showCityEventsOnboarding() {

        final MaterialTapTargetPrompt.Builder tapTargetPromptBuilder = new MaterialTapTargetPrompt.Builder(this, R.style.MaterialTapTargetPromptTheme_SearchButton)
                .setTarget(mSearchButton)
                .setAnimationInterpolator(new FastOutSlowInInterpolator());

        tapTargetPromptBuilder.setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
        {
            @Override
            public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

            }

            @Override
            public void onHidePromptComplete()
            {
                // Proceed to next onboarding step
                showNearEventsOnboarding();
            }
        });

        tapTargetPromptBuilder.show();
    }

    /**
     * Show the near events onboarding
     */
    private void showNearEventsOnboarding() {
        final AppCompatActivity context = this;
        final MaterialTapTargetPrompt.Builder tapTargetPromptBuilder = new MaterialTapTargetPrompt.Builder(this, R.style.MaterialTapTargetPromptTheme_NearEventsButton)
                .setTarget(mNearbyEventsButton)
                .setAnimationInterpolator(new FastOutSlowInInterpolator());

        tapTargetPromptBuilder.setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
        {
            @Override
            public void onHidePrompt(MotionEvent event, boolean tappedTarget)
            {
                // Remember that the user already performed the onboarding experience
                SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(context.getString(R.string.pref_performed_onboarding), true);
                editor.commit();
            }

            @Override
            public void onHidePromptComplete()
            {
                // Restore the content view when onboarding is done
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.event_list);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        tapTargetPromptBuilder.show();
    }

    private void performSocialLogin() {

        // Check if the user is already logged in
        if (AccessToken.getCurrentAccessToken() != null) {
            return;
        }

        // Check if we should show login at this time
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int executionCounter = sharedPref.getInt(getString(R.string.pref_execution_counter), 0);
        if (executionCounter % 3 == 1) {
            Context context = this;
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra(LoginActivity.SUCCESS_MESSAGE, getString(R.string.message_success_event_rsvp));
            context.startActivity(intent);
        }

        executionCounter++;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.pref_execution_counter), executionCounter);
        editor.commit();
    }

    private void showConfirmationAlert(String title, String message, String confirmLabel, String cancelLabel, DialogInterface.OnClickListener confirmClickListener) {
        new AlertDialog.Builder(EventListActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(confirmLabel, confirmClickListener)
                .setNegativeButton(cancelLabel, null)
                .create()
                .show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        showConfirmationAlert(null, message, "OK", "Cancelar", okListener);
    }
}
