package com.abdulrhmanil.wallhavenwallpapers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.ZoomedPhotosAdapter;

import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.KEY_EXTRA_CURRENT_POSITION;

/**
 * {@link com.abdulrhmanil.wallhavenwallpapers.fragments.ScrollZoomedPhotosFragment},
 * it's much optimized, more safe for using.
 * just keep it for now, we will remove it later.
 *
 * WE DON'T USE THIS CLASS, KEEP IT FOR NOW.
 * Maybe we will remove it later, include : activity_scroll_zoomed_photos.xml
 */

//WE DON'T USE THIS CLASS, KEEP IT FOR NOW.
// Maybe we will remove it later, include : activity_scroll_zoomed_photos.xml

public class ScrollZoomedPhotosActivity extends AppCompatActivity {


    /*

    Caller must must implement thus methods, so to create activity,
    and receive a response.

    public void startScrollZoomedPhotosActivity(int position) {
        Intent intent = new Intent(context, ScrollZoomedPhotosActivity.class);
        intent.putExtra(GlobalConstant.KEY_EXTRA_CURRENT_POSITION, position);
        DownloadedFragment.this.startActivityForResult(intent,2);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2) {
            if (data!=null) {
                int res = data.getIntExtra("res", -1);
                if (res!=-1) {
                    recyclerView.scrollToPosition(res);
                }
            }
        }
    }
    */


    /** Recycler view to show thumb photos*/
    protected RecyclerView recyclerView;

    /** Thumb photos adapter*/
    protected ZoomedPhotosAdapter adapter;

    /** Linear layout manager*/
    protected LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_zoomed_photos);

        /* Get intent from the caller, and set the path */
        Intent intent = getIntent();
        int position = intent.getIntExtra(KEY_EXTRA_CURRENT_POSITION,0);



        recyclerView = findViewById(R.id.rvZoomedPhotos);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new ZoomedPhotosAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.scrollToPosition(position);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int itemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                savePosition(itemPosition);
            }
        });

    }

    /** To notify the last position we reached in scrolling*/
    public void savePosition(int position) {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("res", position);
        setResult(2, returnIntent);
    }
}
