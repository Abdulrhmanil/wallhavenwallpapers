package com.abdulrhmanil.wallhavenwallpapers.activities;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource;
import com.abdulrhmanil.wallhavenwallpapers.datasources.PhotoCacheDataSource.OnImageFileSavedListener;
import com.abdulrhmanil.wallhavenwallpapers.datastructures.OptimizedMap;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.PhotoCache;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.PhotoColor;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.Tag;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.AUTHORITY;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.DEFAULT_PATH;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.KEY_EXTRA_FULL_PHOTO_PATH;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.KEY_EXTRA_PHOTO_ID;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.KEY_EXTRA_THUMB_PHOTO_LINK;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.MAX_QUALITY;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.PhotoCacheDataSource.OnPhotoCacheArrivedListener;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.PhotoCacheDataSource.getPhotoCache;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.PhotoCacheDataSource.saveImageFile;


/**
 * ShowPhotoActivity is the activity that show the full photo with details.
 */
public class ShowPhotoActivity extends AppCompatActivity
        implements OnPhotoCacheArrivedListener {

    /**
     * Singleton instance that hold a list of the names (ids) of the local (downloaded) photos so,
     * when we download a photo we add here name (id) to this list, so all the app can be
     * synchronized.
     */
    private final LocalPhotosDataSource localPhotosUtil = LocalPhotosDataSource.getInstance();


    /**
     * The max size num of the photos that we keep cached in the memory.
     */
    private static final int maxSize = 10;


    /**
     * Map that hold the ids of the photos as a keys, and the cached photos as a values.
     * we define the max size of this map to be 32. to get good experience and NOT to fill the ram.
     * Notice: that this map is synchronized map and her type is {@link OptimizedMap OptimizedMap},
     * our customized map.
     */
    private static final Map<String,PhotoCache> fullPhotosCaching =
            Collections.synchronizedMap(new OptimizedMap<String,PhotoCache>(maxSize));


    /**
     * Static method to get {@link #fullPhotosCaching fullPhotosCaching} in other classes.
     * @return the cached photos, a map that hold the ids of the photos as a keys, and the
     * cached photos as a values.
     */
    public static Map<String, PhotoCache> getFullPhotosCaching() {
        return fullPhotosCaching;
    }

    private ImageView imageFull;
    private TextView txt_wallRes,txt_numOfFav,txt_Uploader,
            txt_category,txt_size, txt_views, txt_name, txt_tags;
    private Button btnSave,btnSetAs,btnShare,
            btnColor_1,btnColor_2,btnColor_3,btnColor_4, btnColor_0;
    private ProgressBar progressBar,savingBar;
    private LinearLayout linearLayout_tags;

    private String photoId,thumbPhotoLink;
    private boolean isInFront = false;


    /**
     * Find all views by id, define the views into the fields.
     */
    private void findViewsById(){
        imageFull=findViewById(R.id.imageFull);

        txt_wallRes=findViewById(R.id.txt_wallRes);
        txt_numOfFav=findViewById(R.id.txt_numOfFav);
        txt_Uploader=findViewById(R.id.txt_Uploader);
        txt_category=findViewById(R.id.txt_category);
        txt_size=findViewById(R.id.txt_size);
        txt_views=findViewById(R.id.txt_views);
        txt_name=findViewById(R.id.txt_name);
        txt_tags=findViewById(R.id.txt_tags);

        progressBar=findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE,
                android.graphics.PorterDuff.Mode.MULTIPLY);
        savingBar=findViewById(R.id.savingBar);
        savingBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorPrimaryDarkHeader),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        btnColor_0 =findViewById(R.id.btnColor_0);
        btnColor_1=findViewById(R.id.btnColor_1);
        btnColor_2=findViewById(R.id.btnColor_2);
        btnColor_3=findViewById(R.id.btnColor_3);
        btnColor_4=findViewById(R.id.btnColor_4);

        btnSave =findViewById(R.id.btnSave);
        btnSetAs =findViewById(R.id.btnSetAs);
        btnShare=findViewById(R.id.btnShare);

        linearLayout_tags=findViewById(R.id.linearLayout_tags);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        findViewsById();

        Intent intent = getIntent();
        this.photoId = intent.getStringExtra(KEY_EXTRA_PHOTO_ID);
        this.thumbPhotoLink = intent.getStringExtra(KEY_EXTRA_THUMB_PHOTO_LINK);

        loadPhotoCache(photoId,thumbPhotoLink);
    }


    /**
     * Load the cached photo into the {@link ImageView ImageView}, and if NO cached photo,
     * then set the thump photo into {@link ImageView ImageView} , and go to the internet and
     * cache the photo.
     * @param photoId is the id (name) of the photo that we want to show the user.
     * @param thumbPhotoLink is the link of the thump photo, so we can show the thump photo,
     *                       while loading and cache the full photo.
     */
    private void loadPhotoCache(final String photoId, final String thumbPhotoLink) {
        if (fullPhotosCaching.containsKey(photoId)) {
            final PhotoCache photoCache = fullPhotosCaching.get(photoId);
            setUI(photoCache);
        }
        else {
            Glide.with(this)
                    .load(thumbPhotoLink)
                    .into(imageFull);

            /* this will set the UI and apply fullPhotosCaching in onResult method */
            getPhotoCache(photoId, DEFAULT_PATH,this,this);
        }
    }


    /**
     * This method is responsible to set all the UI to the user, after we sure that we have the
     * {@link PhotoCache cached photo}.
     * @param photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void setUI(final PhotoCache photoCache) {
        if (!this.isDestroyed()) {

            setUI_ImageView(photoCache);
            setUI_SaveButtonStatus(photoCache);
            setUI_InfoTexts(photoCache);
            setUI_TagsTexts(photoCache);
            setUI_ColorButtons(photoCache);
            setOnClickListeners(photoCache);
            setUI_EnabledViews(true);
        }
    }


    /**
     * Display the photo to the user by load the image into {@link ImageView ImageView},
     * we use {@link Glide Glide library} to load photos to the user.
     * @param photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void setUI_ImageView(final PhotoCache photoCache) {
        progressBar.setVisibility(View.GONE);
        Glide.with(this)
                .load(photoCache.getDrawableCache())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageFull);
    }


    /**
     * Set the status (icon and text) of the save button, set the text "save" if the photo is NOT
     * saved, and "saved" if the the photo is have been downloaded nad saved.
     * @param photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void setUI_SaveButtonStatus(final PhotoCache photoCache) {
        if (!this.isDestroyed()) {
            savingBar.setVisibility(View.INVISIBLE);
            btnSave.setEnabled(true);
            if (photoCache.fileExists()) {
                btnSave.setText("saved");
                btnSave.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.stat_saved_blue, 0, 0);
            } else {
                btnSave.setText("save");
                btnSave.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.stat_download_blue, 0, 0);
            }
        }
    }


    /** Reset the original save icon and the original text "save" in case of IO Exception */
    private void setUI_ResetSaveButton() {
        if (!this.isDestroyed()) {
            savingBar.setVisibility(View.INVISIBLE);
            btnSave.setText("save");
            btnSave.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.stat_download_blue, 0, 0);
            btnSave.setEnabled(true);
        }
    }


    /** Show loading bar only if the image is NOT saved after pressing the button */
    private void setUI_ShowSavingBar() {
        if (!this.isDestroyed()) {
            btnSave.setEnabled(false);
            btnSave.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.stat_blank, 0, 0);
            btnSave.setText("loading");
            savingBar.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Set the info (details) of the photo: resolution,favours,uploader, category, size, views,
     * and photo name (id).
     * @param photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void setUI_InfoTexts(final PhotoCache photoCache) {
        txt_wallRes.setText(photoCache.getWallRes());
        txt_numOfFav.setText(String.valueOf(photoCache.getNumOfFav()));
        txt_Uploader.setText(photoCache.getUploader());
        txt_category.setText(photoCache.getCategory());
        txt_size.setText(photoCache.getSize());
        txt_views.setText(photoCache.getViews());
        txt_name.setText((photoCache.getPhotoId()+photoCache.getFormatExtension()));
    }


    /**
     * Add the tags to the views.
     * @param photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void setUI_TagsTexts(final PhotoCache photoCache) {
        if (photoCache.getTags().size() > 0) {
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin_px = 12;
            int margin_dp = (int) (margin_px * (getResources().getDisplayMetrics().density));
            lParams.setMargins(0, 0, 0, margin_dp);
            txt_tags.setVisibility(View.GONE);
            List<Tag> tags = photoCache.getTags();
            for (Tag tag : tags) {
                TextView txtTag = new TextView(this);
                txtTag.setLayoutParams(lParams);
                txtTag.setText(tag.getTagText());
                txtTag.setTextColor(getResources().getColor(R.color.colorPrimaryLightHeader));
                txtTag.setBackgroundColor(getResources().getColor(R.color.WhiteSmoke));
                this.linearLayout_tags.addView(txtTag);
            }
        }
        else {
            txt_tags.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Add the color buttons (5 buttons), thus colors is the dominants color in the photo.
     * @param photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void setUI_ColorButtons(final PhotoCache photoCache) {
        List<PhotoColor> colors = photoCache.getColors();
        btnColor_0.setBackgroundColor(Color.parseColor(colors.get(0).getColor()));
        btnColor_1.setBackgroundColor(Color.parseColor(colors.get(1).getColor()));
        btnColor_2.setBackgroundColor(Color.parseColor(colors.get(2).getColor()));
        btnColor_3.setBackgroundColor(Color.parseColor(colors.get(3).getColor()));
        btnColor_4.setBackgroundColor(Color.parseColor(colors.get(4).getColor()));
    }


    /**
     * Set all the Listeners in the in the actively, so we can define the behavior of the app.
     * The Listeners when press on the buttons or on the {@link ImageView ImageView}.
     * @param photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void setOnClickListeners(final PhotoCache photoCache) {
        btnSave.setOnClickListener(v -> savePhoto(photoCache));
        btnSetAs.setOnClickListener(v -> setAsPhoto(photoCache));
        btnShare.setOnClickListener(v -> sharePhoto(photoCache));
        imageFull.setOnClickListener(v -> startZoomPhotoActivity(photoCache));
    }


    /**
     * Enable or disable the buttons, in the start all the button are disables,
     * After loading the photo we enable the buttons.
     * @param enabled determine to disable or enable the buttons, true if you want to enable the
     *                buttons, false if you want to disable the buttons.
     */
    private void setUI_EnabledViews(boolean enabled){
        btnSave.setEnabled(enabled);
        btnSetAs.setEnabled(enabled);
        btnShare.setEnabled(enabled);
    }



    /* Methods for on click listeners  */


    /**
     * Method that define the behavior of the actively after pressing the save button,
     * We call this method in the listener of the save button.
     * @param photoCache photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void savePhoto(final PhotoCache photoCache) {
        if (!photoCache.fileExists()) {
            setUI_ShowSavingBar();
        }
        saveImageFile(photoCache,DEFAULT_PATH, MAX_QUALITY,savePhotoListener);
    }


    /**
     * Method that define the behavior of the actively after pressing the set as button,
     * We call this method in the listener of the set as button.
     * @param photoCache photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void setAsPhoto(final PhotoCache photoCache) {
        if (!photoCache.fileExists()) {
            setUI_ShowSavingBar();
        }
        saveImageFile(photoCache, DEFAULT_PATH,MAX_QUALITY,setAsPhotoListener);
    }


    /**
     * Method that define the behavior of the actively after pressing the share button,
     * We call this method in the listener of the share button.
     * @param photoCache photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void sharePhoto(final PhotoCache photoCache) {
        if (!photoCache.fileExists()) {
            setUI_ShowSavingBar();
        }
        saveImageFile(photoCache, DEFAULT_PATH, MAX_QUALITY,sharePhotoListener);
    }


    /**
     * Method that start {@link ZoomPhotoActivity ZoomPhotoActivity} activity, this activity
     * let you show the photo in full screen and zoom in/out the photo.
     * @param photoCache photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void startZoomPhotoActivity(final PhotoCache photoCache) {
        imageFull.setEnabled(false);
        Intent intent = new Intent(this, ZoomPhotoActivity.class);
        intent.putExtra(KEY_EXTRA_FULL_PHOTO_PATH, photoCache.getPhotoUrl());
        startActivity(intent);
    }



    /* Private helping methods for showing intents: */


    /**
     * Method that create set as wallpaper intent, and start set as wallpaper intent.
     * @param photoCache photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void setWallpaperIntent(final PhotoCache photoCache) {
        try {
            Uri uri = getUriForFile(this, AUTHORITY, photoCache.getFile());
            WallpaperManager wallpaperMgr = WallpaperManager.getInstance(this);
            startActivity(wallpaperMgr.getCropAndSetWallpaperIntent(uri));
        }
        catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Method that create set share intent, and start share intent.
     * @param photoCache photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    private void shareIntent(final PhotoCache photoCache) {
        Uri uri = getUriForFile(this, AUTHORITY, photoCache.getFile());
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }


    @Override
    protected void onResume() {
        super.onResume();
        isInFront=true;
        imageFull.setEnabled(true);
    }


    @Override
    protected void onPause() {
        super.onPause();
        isInFront=false;
    }


    /* Callback methods from PhotoCacheDataSource API: */


    /**
     * Called only if the photo is NOT cached, first we called
     * {@link com.abdulrhmanil.wallhavenwallpapers.datasources.PhotoCacheDataSource#getPhotoCache(String, String, Context, OnPhotoCacheArrivedListener) getPhotoCache}
     * after the photo is arrived this method will called to set the UI.
     * this method is defined in OnPhotoCacheArrivedListener when this (ShowPhotoActivity)
     * is implement {@link OnPhotoCacheArrivedListener OnPhotoCacheArrivedListener}.
     * @param photoCache photoCache is an instance that hold all the info of the cached photo so we can
     *                   display it to the user.
     */
    @Override
    public void onResult(@NonNull PhotoCache photoCache) {
        setUI(photoCache);
        fullPhotosCaching.put(photoCache.getPhotoId(),photoCache);
    }


    /**
     * Called if something wrong happen (Exception trowed) while caching the photo.
     * So here we show the user, the reason of the Exception.
     * @param e is exception that trowed while caching the photo.
     */
    @Override
    public void onError(@NonNull Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }



    /* listener that contain call back methods,from PhotoCacheDataSource API: */


    /**
     * Listener that determine the behavior of the activity after pressing save button,
     * the listener change the button state if the saved succeed, and showed a appropriate message.
     * and if the saved fail or have been saved before, then showed appropriate message.
     */
    private final OnImageFileSavedListener savePhotoListener = new OnImageFileSavedListener() {
        @Override
        public void onSaved(@NonNull PhotoCache photoCache) {
            setUI_SaveButtonStatus(photoCache);
            boolean addStatus = localPhotosUtil.add(photoCache.getPhotoId(),
                    photoCache.getFormatExtension());
            if (!isInFront) {
                Toast.makeText(ShowPhotoActivity.this,
                        "Saved", Toast.LENGTH_SHORT).show();
            }
            if (!addStatus) {
                Toast.makeText(ShowPhotoActivity.this,
                        "Something wrong while adding to Downloads", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onAlreadySaved(@NonNull PhotoCache photoCache) {
            Toast.makeText(ShowPhotoActivity.this,
                    "Already saved", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onSavingFailed(@NonNull IOException e) {
            setUI_ResetSaveButton();
            Toast.makeText(ShowPhotoActivity.this, "Saving Image Failed:"+
                    e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * Listener that determine the behavior of the activity after pressing set as button,
     * the listener change the button state if the saved succeed, and showed a appropriate message.
     * and if the saved fail or have been saved before, then showed appropriate message.
     */
    private final OnImageFileSavedListener setAsPhotoListener = new OnImageFileSavedListener() {
        @Override
        public void onSaved(@NonNull PhotoCache photoCache) {
            ShowPhotoActivity.this.savePhotoListener.onSaved(photoCache);
            setWallpaperIntent(photoCache);
        }
        @Override
        public void onAlreadySaved(@NonNull PhotoCache photoCache) {
            setWallpaperIntent(photoCache);
        }
        @Override
        public void onSavingFailed(@NonNull IOException e) {
            ShowPhotoActivity.this.savePhotoListener.onSavingFailed(e);
        }
    };


    /**
     * Listener that determine the behavior of the activity after pressing share button,
     * the listener change the button state if the saved succeed, and showed a appropriate message.
     * and if the saved fail or have been saved before, then showed appropriate message.
     */
    private final OnImageFileSavedListener sharePhotoListener = new OnImageFileSavedListener() {
        @Override
        public void onSaved(@NonNull PhotoCache photoCache) {
            ShowPhotoActivity.this.savePhotoListener.onSaved(photoCache);
            shareIntent(photoCache);
        }
        @Override
        public void onAlreadySaved(@NonNull PhotoCache photoCache) {
            shareIntent(photoCache);
        }
        @Override
        public void onSavingFailed(@NonNull IOException e) {
            ShowPhotoActivity.this.savePhotoListener.onSavingFailed(e);
        }
    };
}
