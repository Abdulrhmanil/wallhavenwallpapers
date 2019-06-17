package com.abdulrhmanil.wallhavenwallpapers.fragments.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulrhmanil.wallhavenwallpapers.DisplayUtil;
import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.activities.ShowPhotoActivity;
import com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource;
import com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.utilities.InlineDownloader;
import com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.utilities.InlineDownloader.OnFinishDownloadingListener;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnReachEndList;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.ThumbPhoto;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.DEFAULT_PATH;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.KEY_EXTRA_PHOTO_ID;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.KEY_EXTRA_THUMB_PHOTO_LINK;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource.getInstance;

/**
 * Recycler View Adapter that adapt our data (image, resolution...)
 * into the the {@link RecyclerView RecyclerView}. So we can show them to the user.
 * This adapter is for the {@link ThumbPhoto Thumb Photos} objects....
 */
public class ThumbPhotosAdapter extends RecyclerView.Adapter<ThumbPhotosAdapter.ThumbPhotoViewHolder>
        implements OnFinishDownloadingListener {


    /**
     * A static nested class that represent an item in the {@link RecyclerView RecyclerView}.
     * In our case: the card that hold the image, resolution, and the download button.
     */
    static class ThumbPhotoViewHolder extends RecyclerView.ViewHolder {
        /* Fields to show and update the view : */
        ImageView imageThumb;
        TextView txtRes;
        ImageButton btnDownloadInline;
        View v;
        Context context;

        public ThumbPhotoViewHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            imageThumb = itemView.findViewById(R.id.imageThumb);
            txtRes = itemView.findViewById(R.id.txtRes);
            btnDownloadInline = itemView.findViewById(R.id.btnDownloadInline);
            btnDownloadInline.setTag(this);
            context = itemView.getContext();
        }
    }


    /** The default amount of remaining items, before we request the next thumb photos from website*/
    private static final int DEF_PHOTOS_AMOUNT = 48;


    /** Is the default content of the image view, and the content that showed in case NO internet */
    private static final RequestOptions REQUEST_OPTIONS = initRequestOptions();


    /** A static variable that represent if we showed NO connection alter */
    private static boolean showedNoConnectionAlter = false;


    /** A singleton that hold the local photo list. */
    private final LocalPhotosDataSource localPhotosUtil = getInstance();


    /** A context */
    private final Context context;


    /** A inflater that inflate XML to view */
    private final LayoutInflater inflater;


    /** List that hold the {@link ThumbPhoto thump photos} objects*/
    private final List<ThumbPhoto> photos;


    /** Is allowed click on a image view and start new {@link ShowPhotoActivity ShowPhotoActivity} */
    private boolean allowedToClick = true;


    /** Util object that calculate the image view width and height, and the num of images in on row*/
    private final DisplayUtil.ThumbRes thumbRes;


    /** Functional interface to interact (call method) when reach the RecyclerView bottom */
    private final OnReachEndList onReachEndList;



    /**
     * Constructor to create and init an instance of {@link ThumbPhotosAdapter ThumbPhotosAdapter};
     * @param context a context.
     * @param photos is a list of the thumb photos objects.
     * @param onReachEndList a functional interface to interact when reach the list bottom.
     */
    public ThumbPhotosAdapter(@NonNull final Context context,
                              @NonNull final List<ThumbPhoto> photos,
                              @NonNull final OnReachEndList onReachEndList) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.photos = photos;
        this.thumbRes = DisplayUtil.getThumbRes(context);
        this.onReachEndList = onReachEndList;
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



    /** Enable clicking on the image views */
    public void enableClick() {
        this.allowedToClick = true;
    }



    /** Disable clicking on the image views */
    public void disableClick() {
        this.allowedToClick = false;
    }



    @Override
    public ThumbPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* Here we create the holder view */

        /* Take an xml and convert it to a view object.*/
        View view = inflater.inflate(R.layout.thumb_photo_item, parent, false);
        /* Create a view holder and return it the caller of the method. */
        ThumbPhotoViewHolder holder = new ThumbPhotoViewHolder(view);
        holder.imageThumb.getLayoutParams().width = thumbRes.widthPx;
        holder.imageThumb.getLayoutParams().height = thumbRes.heightPx;
        return holder;
    }



    @Override
    public void onBindViewHolder(final ThumbPhotoViewHolder holder, int position) {
        /* Here we bind the view holder, and show the info in the UI*/
        if (isOnline()) {
            /*Position: index of the current movie */
            if ((position + DEF_PHOTOS_AMOUNT) >= getItemCount()) {
                onReachEndList.addNextPhotos();
            }
        }
        else {
            alterNoConnectionOnlyOnce();
        }


        final ThumbPhoto thumbPhoto = photos.get(position);

        Glide.with(context)
                .setDefaultRequestOptions(REQUEST_OPTIONS)
                .load(thumbPhoto.getThumbPhotoLink())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageThumb);

        holder.txtRes.setText(thumbPhoto.getWallRes());
        holder.imageThumb.setOnClickListener(v -> startShowPhotoActivity(thumbPhoto));
        updateDownloadButtonIcon(holder, thumbPhoto);
        holder.btnDownloadInline.setOnClickListener(v ->
                downloadPhotoInline(holder, thumbPhoto, position));
    }



    /**
     * Get the count of the items in the RecyclerView and thumb photos list.
     * @return the count of items in the our thumb photos list and RecyclerView.
     */
    @Override
    public int getItemCount() {
        return photos.size();
    }



    /**
     * Start {@link ShowPhotoActivity ShowPhotoActivity} to show the user the photo with full
     * resolution, and with her details,
     * @param thumbPhoto is the thumb photo object (that hold photo's id).
     */
    private void startShowPhotoActivity(final ThumbPhoto thumbPhoto) {
        if (allowedToClick) {
            disableClick();
            Intent intent = new Intent(context, ShowPhotoActivity.class);
            intent.putExtra(KEY_EXTRA_PHOTO_ID, thumbPhoto.getPhotoId());
            intent.putExtra(KEY_EXTRA_THUMB_PHOTO_LINK, thumbPhoto.getThumbPhotoLink());
            context.startActivity(intent);
        }
    }



    /**
     * Update the icon of the download button, while recycle the list,
     * animate the button if download the photo right now.
     * @param holder is the view holder, that hold the button, so you can update the button.
     * @param thumbPhoto is the thumb photo object that contain photo Id.
     */
    private void updateDownloadButtonIcon(final ThumbPhotoViewHolder holder,
                                          final ThumbPhoto thumbPhoto) {

        final String photoId = thumbPhoto.getPhotoId();
        if (!isDownloaded(photoId)) {
            holder.btnDownloadInline.setImageResource(R.drawable.ic_circle_download);
            if (isDownloadingNow(photoId)) {
                holder.btnDownloadInline.setImageResource(R.drawable.ic_circle_download_animated);
                final Drawable drawable = holder.btnDownloadInline.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        }
        else {
            holder.btnDownloadInline.setImageResource(R.drawable.ic_circle_check);
        }
    }



    /**
     * Apply inline downloading for the current photo in the recycler view.
     * @param holder is the view holder, so you can update the download button icon.
     * @param thumbPhoto is the {@link ThumbPhoto ThumbPhoto} object that hold the photo details.
     * @param position is the index of the photo in the list, recycler view. so we can update
     *                 and refresh the button icon, after the process is done or fail.
     */
    private void downloadPhotoInline(final ThumbPhotoViewHolder holder,
                                     final ThumbPhoto thumbPhoto,
                                     final int position) {

        final String photoId = thumbPhoto.getPhotoId();

        if (isDownloaded(photoId)) {
            Toast.makeText(context, "Already exist in:\n" + DEFAULT_PATH,
                    Toast.LENGTH_SHORT).show();
        }
        else if (isDownloadingNow(photoId)) {
            Toast.makeText(context, "Downloading Now", Toast.LENGTH_SHORT).show();
        }
        else {
            InlineDownloader downloader = new InlineDownloader(photoId,
                    position, context, this);
            downloader.startInlineDownloading();
            updateDownloadButtonIcon(holder, thumbPhoto);
        }
    }



    /**
     * Check if the photo with photoId has been downloaded before, and exist in the local photos,
     * return {@code True} if the photo is saved in the default downloads folder, {@code False}
     * otherwise.
     * @param photoId is the name (unique id) of the photo you want to check
     *                if have been downloaded.
     * @return {@code True} if the photo has been downloaded and saved in the the downloads folder,
     * {@code False} otherwise.
     */
    private boolean isDownloaded(final String photoId) {
        return localPhotosUtil.contains(photoId);
    }



    /**
     * Return {@code True} if the photo with {@code photoId} is downloading saving right now,
     * in other words receive the photoId as a parameter and check if this photo is downloading now.
     * @param photoId is the Id of the photo you want to check is in the downloading/saving process.
     * @return {@code True} if the photo that her Id is equal to photoId is downloading now,
     * {@code False} otherwise.
     */
    private boolean isDownloadingNow(final String photoId) {
        return InlineDownloader.isDownloadingNow(photoId);
    }



    /**
     * Return {@code True} if there are internet connection or we are connecting now,
     * {@code False} otherwise. In other words check if there are internet connection.
     * @return true if connected to internet, false otherwise.
     */
    private boolean isOnline() {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



    /**
     * Method that show alter dialog on the UI,
     * that there NO internet connection to notify the user.
     */
    private void showAlertDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Connect to the Internet")
                .setMessage("You're offline. Check your connection")
                .setPositiveButton("dismiss", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert).create();
        alertDialog.show();
    }



    /** Check if there are internet connection, if NOT then alter the user*/
    private void alterNoConnectionOnlyOnce() {
        if (!showedNoConnectionAlter) {
            showedNoConnectionAlter = true;
            showAlertDialog();
        }
    }



    /**
     * We Just refresh the view card that contain the photo we downloading now.
     * @param photoId is the Id if the photo that we downloaded.
     * @param position is the current position of the photo in the list/recycler view.
     */
    @Override
    public void notifyItemDownloaded(String photoId, int position) {
        if (position < photos.size()) {
            if (photos.get(position).getPhotoId().equals(photoId)) {
                notifyItemChanged(position);
            }
            else {
                notifyUnexpected();
            }
        }
        else {
            notifyUnexpected();
        }
    }



    /** Refresh the whole recycler view if unexpected event happened */
    @Override
    public void notifyUnexpected() {
        notifyDataSetChanged();
    }
}