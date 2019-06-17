package com.abdulrhmanil.wallhavenwallpapers.datasources;

import com.abdulrhmanil.wallhavenwallpapers.photostructures.LocalPhoto;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.DEFAULT_PATH;


/**
 * A Singleton that hold 1 - a map with local (downloaded) photos names (unique ids) as a keys,
 * and {@link LocalPhoto localPhotos} objects as values.
 * So we can examine in runtime a gaven photo name (id), if have been downloaded and saved in
 * the default downloads folder, in very efficient time O(1), we concerned to update this map,
 * in whole application lifetime.
 *
 * 2 - a list that hold the same {@link LocalPhoto local Photos objects}, we use this list to
 * sort and show them to the user in the download tab.
 * {@link LocalPhoto localPhoto} object hold a basis info for the local downloaded photo.
 */
final public class LocalPhotosDataSource {


    /**
     * We provide {@link #reLoad(OnReloadLocalPhotosListener) reload} method,
     * that read all the info files from the storage, that run run on another thread,
     * if you want to use it, you must send as a parameter
     * {@link OnReloadLocalPhotosListener listener} reload method call
     * {@link #afterLoading() afterLoading} method, on the caller thread, after reload all
     * the files from the storage. We use it to refresh the UI.
     */
    public interface OnReloadLocalPhotosListener {

        /** Call this method after reloading the names of all local (downloaded) photos*/
        void afterLoading();
    }


    /** We use this thread to load the photos names from the storage */
    private static final ExecutorService service = Executors.newSingleThreadExecutor();


    /** Early initialization for the Singleton */
    private static final LocalPhotosDataSource ourInstance = new LocalPhotosDataSource();


    /** Public static method to get the Singleton instance*/
    public static LocalPhotosDataSource getInstance() {
        return ourInstance;
    }


    /**
     * Map that hold the names (unique) of the local photos as keys,
     * and the LocalPhoto objects as values
     */
    private final Map<String,LocalPhoto> localPhotosMap;


    /**
     * List that hold {@link LocalPhoto LocalPhoto} objects that hold a basis info of
     * the local photos, we use this list to sort the local (downloaded) photos.
     */
    private final List<LocalPhoto> localPhotosList;


    /** Comparator to sort the list according to added date, in desc order. */
    private final Comparator<LocalPhoto> addedComparator = (o1, o2) ->
            Long.compare(o2.getImageFile().lastModified(), o1.getImageFile().lastModified());


    /** Comparator to sort the list according to width value, in desc order. */
    private final Comparator<LocalPhoto> widthComparator = (o1, o2) ->
            Integer.compare(o2.getWidth(), o1.getWidth());


    /** Comparator to sort the list according to height value, in desc order. */
    private final Comparator<LocalPhoto> heightComparator = (o1, o2) ->
            Integer.compare(o2.getHeight(), o1.getHeight());



    /** private constructor to init the singleton instance*/
    private LocalPhotosDataSource() {
        localPhotosMap = Collections.synchronizedMap(loadLocalPhotosMap());
        localPhotosList = Collections.synchronizedList(new ArrayList<>(localPhotosMap.values()));
        sortLocalPhotosList();
    }



    /**
     * Method that read all the photos names (unique Id's) in the default folder of the downloaded
     * photos, and add them as keys in the map, and for each key create a {@link LocalPhoto LocalPhoto}
     * object, that hold basis info for the local photo.
     * @return map that hold Id's photos as a keys and  {@link LocalPhoto LocalPhotos} as a values.
     */
    private synchronized Map<String, LocalPhoto> loadLocalPhotosMap() {
        Map<String,LocalPhoto> downloadedMap = new HashMap<>(0);
        File defaultFolder= new File(DEFAULT_PATH);
        String[] imageNames = defaultFolder.list();
        if (imageNames != null) {
            downloadedMap = new HashMap<>(imageNames.length * 2);
            for (String name : imageNames) {
                int dotIndex=name.indexOf(".");
                String photoId = name.substring(0,dotIndex);
                File imageFile= new File(DEFAULT_PATH,name);
                downloadedMap.put(photoId,new LocalPhoto(photoId,imageFile));
            }
        }
        return downloadedMap;
    }



    /** Sort the list of the local photos according to the added time */
    private synchronized void sortLocalPhotosList() {
        Collections.sort(localPhotosList, addedComparator);
    }



    /** Reload all the local photos from the storage into the map as into the list*/
    private synchronized void reLoad() {
       localPhotosMap.clear();
       localPhotosMap.putAll(loadLocalPhotosMap());
       localPhotosList.clear();
       localPhotosList.addAll(localPhotosMap.values());
       sortLocalPhotosList();
    }



    /**
     * Reload all the local photos from the storage into the map as will into the list, in separate
     * thread, and when the mission done, notify the caller (listener), that the mission done in
     * {@link OnReloadLocalPhotosListener#afterLoading() afterLoading} method, so can update UI.
     * @param listener is a listener to notify him that we reload all the local photos from storage.
     */
    final public synchronized void reLoad(OnReloadLocalPhotosListener listener) {
        final android.os.Handler main = new android.os.Handler();
        service.submit(()->{
           reLoad();
           main.post(listener::afterLoading);
        });
    }



    /**
     * Check if a photo with the gaven photoId is already downloaded (considered to be local photo).
     * @param photoId is the id of the local photo that you want to check if have been downloaded.
     * @return true if have been download the photo with the gaven id,
     * (the map contain the photoId), false otherwise.
     */
    final public synchronized boolean contains(String photoId) {
        return localPhotosMap.containsKey(photoId);
    }



    /**
     * Get the list of the local (downloaded) photos, so you cn show them to the user.
     * @return a list of {@link LocalPhoto local Photos} objects,
     * that contain basis info of the local photo.
     */
    final public synchronized List<LocalPhoto> getLocalPhotosList() {
        return localPhotosList;
    }



    /**
     * Create new Local photo to the map as will to the list, we use this method, after downloading
     * a new photo, and we want to add it to the singleton instance, so the application will be
     * synchronized.
     * @param photoId is the name (unique Id) of the photo you want to add to the map and list.
     * @param formatExtension is the format of the photo, could be PNG, JPG.
     * @return true if we succeed to add the photo to the list and map, false otherwise.
     */
    final public synchronized boolean add(String photoId, String formatExtension) {
        if (!localPhotosMap.containsKey(photoId)) {
            File imageFile = new File(DEFAULT_PATH, (photoId + formatExtension));
            LocalPhoto addImage = new LocalPhoto(photoId, imageFile);
            localPhotosMap.put(photoId, addImage);
            localPhotosList.add(0 , addImage);
            return true;
        }
        return false;
    }



    /**
     * Remove the local photo with the id photoId from the map and from the list , so the app can
     * be synchronized. use it when we remove a local photo from the app.
     * @param photoId is the name (unique Id) of the photo you want to remove.
     * @return true if we succeed to remove the local photo.
     */
    final public synchronized boolean remove(String photoId) {
        if (localPhotosMap.containsKey(photoId)) {
            LocalPhoto removeImage = localPhotosMap.remove(photoId);
            return localPhotosList.remove(removeImage);
        }
        return false;
    }



    /**
     * Remove the local photo with from the map and from the list, so the app can
     * be synchronized. use it when we remove a local photo from the app.
     * @param localPhoto is the {@link LocalPhoto local photo}  you want to remove.
     * @return true if we succeed to remove the local photo.
     */
    final public synchronized boolean remove(LocalPhoto localPhoto) {
        return remove(localPhoto.getPhotoId());
    }



    /**
     * Remove the local photo with id equal to photoId from the map as will from the list, so the
     * application can be synchronized. we use it when you want to delete a photo from the app.
     * Notice: this app is more efficient from other method if you know the index of the photo in
     * the list, try to use it cost O(1).
     * @param photoId is the name (unique Id) of the photo you want to remove to the map and list.
     * @param index is the index of the photo you want to remove in the {@link #localPhotosList list}.
     * @return true if we succeed to remove the photo, false otherwise.
     */
    final public synchronized boolean remove(String photoId, int index) {
        if (localPhotosMap.containsKey(photoId) && index < localPhotosList.size() &&
                localPhotosMap.get(photoId) == localPhotosList.get(index)) {
            localPhotosMap.remove(photoId);
            localPhotosList.remove(index);
            return true;
        }
        return false;
    }



    /**
     * Remove the {@link LocalPhoto local photo} from the map as will from the list, so the
     * application can be synchronized. we use it when you want to delete a photo from the app.
     * Notice: this app is more efficient from other method if you know the index of the photo in
     * the list, try to use it cost O(1).
     * @param localPhoto is the photo you want to remove to the map and list.
     * @param index is the index of the photo you want to remove in the {@link #localPhotosList list}.
     * @return true if we succeed to remove the photo, false otherwise.
     */
    final public synchronized boolean remove(LocalPhoto localPhoto, int index) {
        return remove(localPhoto.getPhotoId(),index);
    }
}
