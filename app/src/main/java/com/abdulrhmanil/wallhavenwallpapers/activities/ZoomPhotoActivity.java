package com.abdulrhmanil.wallhavenwallpapers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.abdulrhmanil.wallhavenwallpapers.GlobalConstant;
import com.abdulrhmanil.wallhavenwallpapers.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * ZoomPhotoActivity is Activity that responsible for showing the photo in full screen,
 * and let you zoom the photo.
 */
public class ZoomPhotoActivity extends AppCompatActivity {
    /** PhotoView provide the zoom method */
    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_photo);

        /* Get intent from the caller, and set the path */
        Intent intent = getIntent();
        String photoPath = intent.getStringExtra(GlobalConstant.KEY_EXTRA_FULL_PHOTO_PATH);

        photoView = findViewById(R.id.photoView);

        setZoomLevel();
        loadPhotoIntoView(photoPath);
    }

    /**
     * Set the level of the zoom.
     */
    private void setZoomLevel() {
        final float oldMaxScale = photoView.getMaximumScale();
        photoView.setMaximumScale(oldMaxScale *2);
        photoView.setMediumScale(oldMaxScale);
    }

    /**
     * load the photo into the view, show the photo to the user.
     * @param photoPath is the path of the photo, could be a link, or file path.
     */
    private void loadPhotoIntoView(String photoPath) {
        Glide.with(this)
                .load(photoPath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(photoView);
    }
}
