package br.com.danceapp.android;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.Arrays;
import java.util.List;

import br.com.danceapp.android.model.Event;

/**
 * Created by Rafael on 09/01/2017.
 */

public class UserEventsActivity extends AppCompatActivity {
    public static final String TAG = UserEventsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.userevents_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        loadUserEvents();
    }

    void loadUserEvents() {
        List<String> userEventsList = Arrays.asList(
            "Kirinus", "Clube da Dança", "Japesca", "Dancemonium", "Salsa y Merengue",
            "Arriba Arriba", "OmegaRED", "Cidade Alta", "Gothiclub", "Oxente Danceteria",
            "Casa do Embalo", "Dancinha", "Mexe-mexe", "Oligateer", "Ammark"
        );

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.userevents_list);
        assert recyclerView != null;

        UserEventsRecyclerViewAdapter adapter = new UserEventsRecyclerViewAdapter(userEventsList, getApplicationContext());

        recyclerView.setAdapter(adapter);
    }
}
