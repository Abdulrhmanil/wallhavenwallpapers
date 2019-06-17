package com.abdulrhmanil.wallhavenwallpapers.datasources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.abdulrhmanil.wallhavenwallpapers.photostructures.FullPhoto;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.PhotoCache;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.PhotoColor;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.Tag;
import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * API that provide asynchronous methods:
 * 1 - {@link #getPhotoCache(String, String, Context, OnPhotoCacheArrivedListener) getPhotoCache}
 * that receive listener as a parameter and then get the image with her details from the internet
 * and cache it, and then call one of the listener methods (onResult, onError, depends the situation)
 * and run it on the caller thread - main thread.
 * 2 - {@link #saveImageFile(PhotoCache, String, int, OnImageFileSavedListener) saveImageFile}
 * that receive listener as a parameter and write the image file on the storage, with the specified
 * path, name, format and quality, and then call one of the listener method
 * (onSaved, onAlreadySaved, onSavingFailed  depends the situation) and run it on the main thread.
 */
public class PhotoCacheDataSource {

    /**
     * To use
     * {@link #getPhotoCache(String, String, Context, OnPhotoCacheArrivedListener) getPhotoCache}
     * you must send a listener as a parameter so we can notify you with the results, in the
     * appropriate method from those: {@link #onResult(PhotoCache)}, {@link #onError(Exception)}.
     * So one of those method will run on the caller thread - properly the main thread.
     */
    public interface OnPhotoCacheArrivedListener {
        void onResult(@NonNull final PhotoCache photoCache);
        void onError(@NonNull final Exception e);
    }

    /**
     * To use
     * {@link #saveImageFile(PhotoCache, String, int, OnImageFileSavedListener)} saveImageFile}
     * you must send a listener as a parameter so we can notify you with the results, in the
     * one appropriate method from those methods:
     * {@link #onSaved(PhotoCache)} - when successfully saved,
     * {@link #onAlreadySaved(PhotoCache)} - when a file with same name already exist,
     * {@link #onSavingFailed(IOException)} - when an I/O error occurred.
     * So one of those method will run on the caller thread - properly the main thread.
     */
    public interface OnImageFileSavedListener {
        void onSaved(@NonNull final PhotoCache photoCache);
        void onAlreadySaved(@NonNull final PhotoCache photoCache);
        void onSavingFailed(@NonNull final IOException e);
    }


    /** Thread Pool with size 4, that do all the missions in the API */
    private static final ExecutorService service = Executors.newFixedThreadPool(4);


    /** Template for the website we connect to him to get the full photo info's */
    private static final String ADDRESS_TEMPLATE = "https://wallhaven.cc/w/%s";


    /* Sync private helping methods : */

    /**
     * Method that connect to the internet {@link #ADDRESS_TEMPLATE link addresss} and get the info
     * from the HTML file and parse it to create {@link FullPhoto} object, and return it.
     * This method work in sync way, way call her in a background thread.
     * @param photoId is photo id that you want to get his info.
     * @return a new {@link FullPhoto} object that contain all the info on the full res Photo.
     * @throws IOException if it's fail to connect to the internet.
     */
    private static FullPhoto getFullPhotoSync(final String photoId) throws IOException {
        String address = String.format(ADDRESS_TEMPLATE, photoId);
        Document doc = Jsoup.connect(address).get();
        Element element = doc.select("main").first().select("img").first();

        /* FullPhoto member*/
        final String photoUrl = (element.attr("src"));
        /* FullPhoto member*/
        final int width = Integer.parseInt(element.attr("data-wallpaper-width"));
        /* FullPhoto member*/
        final int height = Integer.parseInt(element.attr("data-wallpaper-height"));

        Element eUploader = doc.select("[class=showcase-uploader]").first();
        /* FullPhoto member*/
        final String uploader = eUploader.select("[class^=username]").first().text();

        Element dl = eUploader.parent();

        /* FullPhoto member*/
        final String category = dl.select("dt:contains(Category) + dd").text();
        /* FullPhoto member*/
        final String size = dl.select("dt:contains(Size) + dd").text();
        /* FullPhoto member*/
        final String views = dl.select("dt:contains(Views) + dd").text();
        /* FullPhoto member*/
        final int numOfFav = Integer.parseInt(dl.select("dt:contains(Favorites) + dd").text());
        /* FullPhoto member*/
        final String wallRes = doc.select("[class=showcase-resolution]").first().text();

        Elements eTags = doc.select("[id=tags]").select("li[class^=tag ]");
        /* FullPhoto member*/
        List<Tag> tags = new ArrayList<>(eTags.size());
        for (Element eTag : eTags) {
            final String tagId = eTag.attr("data-tag-id");
            final String tagText = eTag.select("a[class=tagname]").first().text();
            tags.add(new Tag(tagId, tagText));
        }

        Elements eColors = doc.select("[class=color]");
        /* FullPhoto member*/
        List<PhotoColor> colors = new ArrayList<>(eColors.size());
        for (Element eColor : eColors) {
            final String color = (eColor.attr("style").substring(17));
            /* Adding Color*/
            colors.add(new PhotoColor(color));
        }
        return new FullPhoto(photoId, wallRes, numOfFav, photoUrl, height, width
                , uploader, category, size, views, tags, colors);
    }


    /**
     * Method that write a {@link Bitmap Bitmap} object to an image file in the storage,
     * (external memory)
     * @param foldersPath is a String that represent the path of the image file.
     *                   Example: /storage/emulated/0/Pictures/MyWallHaven
     * @param imageNameWithExtension the name
     * @param format is the format of the image file you want to write - can be: JPEG, PNG, WEBP.
     * @param bitmap is the object that hold the image, can compress the image depends on the quality
     * @param quality int: Hint to the compressor, 0-100. 0 meaning compress for small size,quality
     *                100 meaning compress for max quality. Some formats, like PNG which is lossless,
     *                will ignore the quality setting
     * @return True if there NO file with the same name and succeed to create and write the file,
     * false otherwise.
     * @throws IOException If an I/O error occurred
     */
    private static boolean saveImageFileSync(final String foldersPath,
                                             final String imageNameWithExtension,
                                             final Bitmap.CompressFormat format,
                                             final Bitmap bitmap,
                                             final int quality) throws IOException {
        File mkFolders=new File(foldersPath);
        if (!mkFolders.exists()) {
            boolean createFlag = mkFolders.mkdirs();
        }
        final File imageFile = new File(mkFolders,imageNameWithExtension);
        boolean createStatus = imageFile.createNewFile();
        if (createStatus) {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(format, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        }
        return createStatus;
    }


    /* Async methods that getting image from internet and saving image to storage : */

    /**
     * Async method that receive listener as a parameter and then get the image with her details
     * from the internet and cache it, and then call one of the listener methods
     * (onResult(PhotoCache), onError(Exception), depends the situation)
     * and run it on the caller thread - main thread.
     * @param photoId a String that represent the photo id that you want to get.
     * @param defaultPath is the default path of the saving images, it's necessary to check at runtime
     *                    if the image already downloaded and saved in a matter to
     *                    show the appropriate icon and string.
     *                    Example: /storage/emulated/0/Pictures/MyWallHaven
     * @param context  Any context, will not be retained.
     * @param listener is a listener that implement OnPhotoCacheArrivedListener Interface to notify
     *                 him when the the result is ready in :
     *                 {@link OnPhotoCacheArrivedListener#onResult(PhotoCache)} method.
     *                 or an error accrued dairying the process in:
     *                 {@link OnPhotoCacheArrivedListener#onError(Exception)} method.
     *                 Notice those methods (onResult,onError) runs on the caller thread.
     */
    public static void getPhotoCache(final String photoId,
                                     final String defaultPath,
                                     final Context context,
                                     final OnPhotoCacheArrivedListener listener) {
        final android.os.Handler main = new android.os.Handler();
        service.submit(() -> {
            try {
                final FullPhoto fullPhoto = getFullPhotoSync(photoId);
                final Drawable drawableCache = Glide
                        .with(context)
                        .load(fullPhoto.getPhotoUrl())
                        .submit()
                        .get();
                final PhotoCache photoCache = new PhotoCache(fullPhoto,drawableCache,defaultPath);

                main.post(() -> {
                    listener.onResult(photoCache);
                });
            }
            catch (IOException | InterruptedException | ExecutionException e) {
                main.post(() -> {
                    listener.onError(e);
                });
            }
        });
    }


    /**
     * Async method that receive listener as a parameter and write the image file on the storage,
     * with the specified path, name, format and quality, and then call one of the listener method
     * (onSaved(PhotoCache), onAlreadySaved(PhotoCache), onSavingFailed(IOException)
     * depends on the situation) and run it on the caller thread - main thread.
     * @param photoCache is an instance that contain the image cache (as a Bitmap and Drawable),
     *                   name(id) and format so we use it to write the image file.
     * @param foldersPath is the path of the folders you want to save the image file in it.
     *                    Example: /storage/emulated/0/Pictures/MyWallHaven
     * @param quality int: Hint to the compressor, 0-100. 0 meaning compress for small size,quality
     *                100 meaning compress for max quality. Some formats, like PNG which is lossless,
     *                will ignore the quality setting
     * @param listener is a listener that implement OnImageFileSavedListener Interface to notify
     *                 him when the file successfully saved  in :
     *                 {@link OnImageFileSavedListener#onSaved(PhotoCache)} method.
     *                 or when the file already exist in foldersPath in :
     *                 {@link OnImageFileSavedListener#onAlreadySaved(PhotoCache)} method.
     *                 or an error accrued dairying the process in:
     *                 {@link OnImageFileSavedListener#onSavingFailed(IOException)} method.
     *                 Notice those methods (onSaved, onAlreadySaved, onSavingFailed)
     *                 runs on the caller thread.
     */
    public static void saveImageFile(final PhotoCache photoCache,
                                     final String foldersPath,
                                     final int quality,
                                     final OnImageFileSavedListener listener) {
        final android.os.Handler main = new android.os.Handler();
        service.submit(() -> {
            try {
                String imageName = photoCache.getPhotoId() + photoCache.getFormatExtension();
                final boolean creationSucceed = saveImageFileSync(foldersPath, imageName,
                        photoCache.getFormat(), photoCache.getBitmapCache(), quality);

                    main.post(() -> {
                        if (creationSucceed) {
                            listener.onSaved(photoCache);
                        }
                        else {
                            listener.onAlreadySaved(photoCache);
                        }
                    });
            }
            catch (IOException e) {
                main.post(() -> {
                    listener.onSavingFailed(e);
                });
            }
        });
    }
}
