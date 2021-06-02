package com.example.project_try;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    private boolean Permission = false; //to check if read permission is given

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.Menu);

        String[] menuItems =new String[]{"Online","Downloaded"};
        ArrayAdapter<String> firstAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,menuItems);
        listView.setAdapter(firstAdapter);

        if(checkPermission())
        {
            final ArrayList<File> allSongs = getSongs(Environment.getExternalStorageDirectory());
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) listView.getItemAtPosition(position);
                    if (selection.equals("Online")) {
                        startActivity(new Intent(getApplicationContext(), Online.class));
                    } else if (selection.equals("Downloaded")) {
                        startActivity(new Intent(getApplicationContext(), Downloaded.class)
                                .putExtra("songs",allSongs));
                    }
                }
            });
        }

    }

    public boolean checkPermission(){
        Dexter.withContext(MainActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Permission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Permission = false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        return Permission;
    }

    public ArrayList<File> getSongs(File file)       //get songs from files
    {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if(files!=null)
        {
            for(File currentFile:files)        //checking for only mp3 files
            {
                if(currentFile.isDirectory() && !currentFile.isHidden() )
                {
                    arrayList.addAll(getSongs(currentFile));
                }
                else
                {
                    if(currentFile.getName().endsWith(".mp3") || currentFile.getName().endsWith(".wav"))
                    {
                        arrayList.add(currentFile);
                    }
                }
            }
            return arrayList;
        }
        else
        {
            return null;
        }
    }
}