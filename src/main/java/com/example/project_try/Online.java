package com.example.project_try;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class Online extends AppCompatActivity {

    ListView listView;
    ArrayList<String> arrayListSongsName = new ArrayList<>();
    ArrayList<String> arrayListSongsUrl = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        getSupportActionBar().setTitle("Online Songs");

        listView = findViewById(R.id.SongListOnline);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        retrieveSongs();

    }

    private void retrieveSongs() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("0");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){
                    Song songObj = ds.getValue(Song.class);
                    arrayListSongsName.add(songObj.getSong());
                    arrayListSongsUrl.add(songObj.getUrl());
                }

                arrayAdapter = new ArrayAdapter<String>(Online.this, android.R.layout.simple_list_item_1,arrayListSongsName){
                    //to prevent song names from taking more than one line
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position,convertView,parent);
                        TextView textView = (TextView)view.findViewById(android.R.id.text1);
                        textView.setSingleLine(true);
                        textView.setMaxLines(1);
                        return view;
                    }
                };
                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String songName = (String) listView.getItemAtPosition(position);
                        startActivity(new Intent(getApplicationContext(),player.class).putExtra("Songs",arrayListSongsUrl)
                                .putExtra("SongNames",arrayListSongsName)
                                .putExtra("pos",position));

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}