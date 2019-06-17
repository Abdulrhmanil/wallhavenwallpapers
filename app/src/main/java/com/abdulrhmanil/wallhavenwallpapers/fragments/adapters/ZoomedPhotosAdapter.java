package com.abdulrhmanil.wallhavenwallpapers.fragments.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.LocalPhoto;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

import static com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource.getInstance;

public class ZoomedPhotosAdapter extends RecyclerView.Adapter<ZoomedPhotosAdapter.ZoomedPhotoViewHolder> {


    /**
     * A static nested class that represent an item in the {@link RecyclerView RecyclerView}.
     * In our case: the card that hold the image view (PhotoView) that show the local photo,
     * in fullscreen mode with zoom option,that saved in the default downloads folder.
     */
    static class ZoomedPhotoViewHolder extends RecyclerView.ViewHolder {
        /* Fields to show and update the view : */
        PhotoView photoView;
        View v;

        ZoomedPhotoViewHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            photoView = itemView.findViewById(R.id.photoView);
        }
    }


    /** A context */
    private final Context context;


    /** A inflater that inflate XML to view */
    private final LayoutInflater inflater;


    /** List that hold the {@link LocalPhoto local photos} objects,
     * that hold a basis info for the local downloaded photos,
     * we need only the the file path of the images*/
    private final List<LocalPhoto> photos = getInstance().getLocalPhotosList();


    /**
     * Constructor to create and init an instance of {@link LocalPhotosAdapter ZoomedPhotosAdapter};
     * @param context a context.
     */
    public ZoomedPhotosAdapter(@NonNull final Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
    }




    @Override
    public ZoomedPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* Here we create the holder view */
        /* Take an xml and convert it to a view object.*/
        View view = inflater.inflate(R.layout.zoomed_photo_item, parent, false);
        /* Create a view holder and return it the caller of the method. */
        return new ZoomedPhotoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ZoomedPhotoViewHolder holder, int position) {
        /* Here we bind the view holder, and show the photo in screen mode to the UI*/
        Glide.with(holder.photoView)
                .load(photos.get(position).getImageFile().toString())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.photoView);
    }


    /**
     * Get the count of the items in the RecyclerView and local photos list.
     * @return the count of items in the our local photos list and RecyclerView.
     */
    @Override
    public int getItemCount() {
        return photos.size();
    }

}
