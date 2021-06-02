package com.example.project_try;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Downloaded extends AppCompatActivity {

    ListView listView;
    ArrayList<File> allSongs;
    ArrayList<String> Songs = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded);

        getSupportActionBar().setTitle("Downloaded Songs");

        listView = findViewById(R.id.SongListDownloaded);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        allSongs = (ArrayList) bundle.getParcelableArrayList("songs");
        displaySongs();
    }

    private void displaySongs() {
        if(allSongs!=null)
        {
            //Songs = new String[allSongs.size()];

            for(int i=0;i<allSongs.size();i++)
            {
                //Songs.set(i, allSongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", ""));
                Songs.add(allSongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", ""));
            }
            arrayAdapter = new ArrayAdapter<String>(Downloaded.this, android.R.layout.simple_list_item_1,Songs);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(getApplicationContext(),player.class).putExtra("Songs",allSongs)
                            .putExtra("SongNames",Songs)
                            .putExtra("pos",position));

                }
            });
        }
    }
}