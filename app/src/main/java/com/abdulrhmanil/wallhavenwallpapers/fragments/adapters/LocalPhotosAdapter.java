package com.abdulrhmanil.wallhavenwallpapers.fragments.adapters;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.abdulrhmanil.wallhavenwallpapers.DisplayUtil;
import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnPressLocalPhoto;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.LocalPhoto;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.AUTHORITY;


/**
 * Recycler View Adapter that adapt our local photos in their full resolution
 * into the the {@link RecyclerView RecyclerView}. So we can show them to the user.
 * This adapter is for the {@link LocalPhoto local Photos} objects ...
 * The {@link LocalPhoto localPhoto} is an instance that hold a basis info for the local photos.
 */
public class LocalPhotosAdapter extends RecyclerView.Adapter<LocalPhotosAdapter.LocalPhotoViewHolder> {


    /** Is the default content of the image view, and the content that showed in case NO internet */
    private static final RequestOptions REQUEST_OPTIONS = initRequestOptions();


    /** A singleton that hold the local photo list. */
    private final LocalPhotosDataSource localPhotosUtil = LocalPhotosDataSource.getInstance();


    /** A context */
    private final Context context;


    /** A inflater that inflate XML to view */
    private final LayoutInflater inflater;


    /** List that hold the {@link LocalPhoto local photos} objects,
     * that hold a basis info for the local downloaded photos*/
    private final List<LocalPhoto> localPhotosList;


    /** Util object that calculate the image view width and height, and the num of images in on row*/
    private final DisplayUtil.ThumbRes thumbRes;


    /**
     * Listener that responsible for the action we apply when press on local photo,
     * so when we press on local photo this functional interaction interface call :
     * startScrollZoomedPhotosFragment method, that show ScrollZoomedPhotosFragment fragment.
     * this fragment show us the local photos in fullscreen mode with the option to zoom in/out.
     * You must provide the implementation and send it in the constructor.
     */
    private final OnPressLocalPhoto onPressLocalPhoto;



    /**
     * Constructor to create and init an instance of {@link LocalPhotosAdapter LocalPhotosAdapter};
     * @param context a context.
     * @param localPhotosList is a list that hold all the {@link LocalPhoto LocalPhoto} objects...
     */
    public LocalPhotosAdapter(@NonNull Context context, @NonNull List<LocalPhoto> localPhotosList,
                              @NonNull OnPressLocalPhoto onPressLocalPhoto) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.localPhotosList = localPhotosList;
        this.thumbRes = DisplayUtil.getThumbRes(context);
        this.onPressLocalPhoto = onPressLocalPhoto;
    }



    /**
     * Create RequestOptions for Glide library, to show the default icons.
     * @return the default RequestOptions.
     */
    private static RequestOptions initRequestOptions() {
        return new RequestOptions()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error);
    }



    @Override
    public LocalPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* Here we create the holder view */
        /* Take an xml and convert it to a view object.*/
        View v = inflater.inflate(R.layout.local_photo_item, parent, false);
        /* Create a view holder and return it the caller of the method. */
        LocalPhotoViewHolder holder = new LocalPhotoViewHolder(v);
        holder.imageDownloaded.getLayoutParams().width = thumbRes.widthPx;
        holder.imageDownloaded.getLayoutParams().height = thumbRes.heightPx;
        return holder;
    }



    @Override
    public void onBindViewHolder(LocalPhotoViewHolder holder, int position) {
        /* Here we bind the view holder, and show the info in the UI*/
        final LocalPhoto localPhoto = localPhotosList.get(position);
        final int index = position;

        Glide.with(context)
                .setDefaultRequestOptions(REQUEST_OPTIONS)
                .load(localPhoto.getImageFile())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageDownloaded);

        holder.localPhoto = localPhoto;
        holder.btnDeleteLocalPhoto.setOnClickListener(v -> showDeleteDialog(localPhoto,index));
        holder.btnSetLocalPhoto.setOnClickListener(v -> setWallpaperIntent(localPhoto));
        holder.btnShareLocalPhoto.setOnClickListener(v -> shareIntent(localPhoto));
        holder.imageDownloaded.setOnClickListener(v -> startScrollZoomedPhotosFragment(position));
        holder.imageDownloaded.setOnLongClickListener(v -> showResDialog(localPhoto));
    }


    /**
     * Get the count of the items in the RecyclerView and local photos list.
     * @return the count of items in the our local photos list and RecyclerView.
     */
    @Override
    public int getItemCount() {
        return localPhotosList.size();
    }



    /**
     * Create intent to set current photo as wallpaper, and then start the set as wallpaper intent.
     * @param localPhoto is the local photo object, hold basis info of the local photo,
     *                   include the path of the local photo.
     */
    private void setWallpaperIntent(final LocalPhoto localPhoto) {
        try {
            Uri uri = getUriForFile(context, AUTHORITY, localPhoto.getImageFile());
            WallpaperManager wallpaperMgr = WallpaperManager.getInstance(context);
            context.startActivity(wallpaperMgr.getCropAndSetWallpaperIntent(uri));
        }
        catch (IllegalArgumentException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Create intent to share current wallpaper, and then start the sharing intent.
     * @param localPhoto is the local photo object, hold basis info of the local photo,
     *                   include the path of the local photo.
     */
    private void shareIntent(final LocalPhoto localPhoto) {
        Uri uri = getUriForFile(context, AUTHORITY, localPhoto.getImageFile());
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }


    /**
     * Method that call startScrollZoomedPhotosFragment in
     * {@link #onPressLocalPhoto onPressLocalPhoto} interaction listener that his method is
     * implemented in:
     * {@link com.abdulrhmanil.wallhavenwallpapers.fragments.DownloadedFragment Downloaded Fragment}.
     * This method start and show:
     * {@link com.abdulrhmanil.wallhavenwallpapers.fragments.ScrollZoomedPhotosFragment} fragment
     * that show the photos in full screen mode and zoom mode. so you can scroll and zoom them in
     * full screen mode.
     * @param startPosition is the position of the photo you want the recycler view to show as a
     *                      start point (the first photo we show in zoom fullscreen mode.
     */
    private void startScrollZoomedPhotosFragment(int startPosition) {
        onPressLocalPhoto.startScrollZoomedPhotosFragment(startPosition);
    }



    /**
     * Show the resolution (width X height) of the {@link LocalPhoto LocalPhoto} object.
     * @param localPhoto is the local photo object, hold basis info of the local photo,
     *                   include the path of the local photo.
     * @return just ignore the returned value, used for the long click listener.
     */
    private boolean showResDialog(final LocalPhoto localPhoto) {
        final AlertDialog resDialog = new AlertDialog.Builder(context)
                .setMessage(localPhoto.getWallRes())
                .create();
        resDialog.show();
        return true;
    }



    /**
     * Show the confirm delete dialog, and if choose to delete the the photo, and pressed delete
     * button, then call {@link #deleteLocalPhoto(LocalPhoto, int) delete photo} method. to delete
     * the photo from the storage, and remove it from the local photos list, so all the application
     * can be synchronized.
     * @param localPhoto is the local photo you want to delete and remove from the application.
     * @param index the index of the local photo in the list ...
     */
    private void showDeleteDialog(final LocalPhoto localPhoto, final int index) {
        final AlertDialog deleteDialog = new AlertDialog.Builder(context)
                .setTitle("Delete this wallpaper ?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(R.string.delete, (dialog, which) ->
                        deleteLocalPhoto(localPhoto,index))
                .setNegativeButton(R.string.cancel_deleting, (dialog, which) ->
                        dialog.dismiss())
                .create();
        // don't change order - change color must be after show method
        deleteDialog.show();
        deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }



    /**
     * Apply deleting photo:
     * method that firstly remove the photo from the {@link #localPhotosList local photos list},
     * so the application can be synchronized
     * And then delete the local photo from the local storage.
     * @param localPhoto is the local photo object you want to remove and delete.
     * @param index is the index of the local photo you want to remove and delete.
     */
    private void deleteLocalPhoto(final LocalPhoto localPhoto, final int index) {
        File imageFile = localPhoto.getImageFile();
        boolean removeStatus = localPhotosUtil.remove(localPhoto, index);
        removeStatus &= imageFile.delete();
        if (!removeStatus) {
            Toast.makeText(context, "Something wrong while remove the file",
                    Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }



    /**
     * A static nested class that represent an item in the {@link RecyclerView RecyclerView}.
     * In our case: the card that hold the image view that show the local photo, saved in the
     * default downloads folder.
     */
    public static class LocalPhotoViewHolder extends RecyclerView.ViewHolder {
        LocalPhoto localPhoto;
        ImageView imageDownloaded;
        Button btnDeleteLocalPhoto,btnSetLocalPhoto,btnShareLocalPhoto;
        View v;

        public LocalPhotoViewHolder(View itemView) {
            super(itemView);
            this.v =itemView;
            imageDownloaded=itemView.findViewById(R.id.imageLocal);
            btnDeleteLocalPhoto=itemView.findViewById(R.id.btnDeleteLocalPhoto);
            btnSetLocalPhoto=itemView.findViewById(R.id.btnSetLocalPhoto);
            btnShareLocalPhoto=itemView.findViewById(R.id.btnShareLocalPhoto);
        }
    }
}
