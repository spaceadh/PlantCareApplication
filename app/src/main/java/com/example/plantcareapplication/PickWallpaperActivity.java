package com.example.plantcareapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.plantcareapplication.Models.Wallpaper;
import com.example.plantcareapplication.adapters.WallpaperAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PickWallpaperActivity extends AppCompatActivity {

    private ListView listView;
    private WallpaperAdapter adapter;
    private List<Wallpaper> wallpaperList;
    private DatabaseReference wallpaperRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_wallpaper);

        wallpaperList = new ArrayList<>();
        wallpaperRef = FirebaseDatabase.getInstance().getReference().child("Plants");

        listView = findViewById(R.id.listView);
        adapter = new WallpaperAdapter(this, wallpaperList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Wallpaper selectedWallpaper = wallpaperList.get(position);
                String wallpaperId = selectedWallpaper.getImageKey();
                passIntentToInstructionsPage(wallpaperId);
            }
        });

        loadWallpapers();
    }

    private void loadWallpapers() {
        Query query = wallpaperRef.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wallpaperList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Wallpaper wallpaper = snapshot.getValue(Wallpaper.class);
                    wallpaperList.add(wallpaper);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PickWallpaperActivity.this, "Failed to load wallpapers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void passIntentToInstructionsPage(String wallpaperId) {
        Intent intent = new Intent(PickWallpaperActivity.this, com.example.plantcareapplication.InstructionsPage.class);
        intent.putExtra("WallpaperId", wallpaperId);
        startActivity(intent);
    }
}