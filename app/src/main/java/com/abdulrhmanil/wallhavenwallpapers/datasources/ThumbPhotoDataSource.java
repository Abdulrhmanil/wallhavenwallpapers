package com.abdulrhmanil.wallhavenwallpapers.datasources;

import android.support.annotation.NonNull;

import com.abdulrhmanil.wallhavenwallpapers.photostructures.ThumbPhoto;
import com.abdulrhmanil.wallhavenwallpapers.queriesconstants.home;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * API that provide 3 asynchronous methods:
 * (important note : every method work in synchronize way so the call order is saved, you will get
 * the the thumb order as the call method order).
 *
 * 1 - {@link #getToplistPhotos(int, OnThumbPhotoArrivedListener) getToplistPhotos}
 * that receive num page and listener as a parameters and then get the 24 thump photos with there
 * details form toplist category from specified page and then call one of the listener methods
 * (onResult, onError, depends the situation) and run it on the caller thread - main thread.
 * So the main thread can fill the recycle view with toplist thumb photos  in appropriate fragment.
 *
 * 2 - {@link #getLatestPhotos(int, OnThumbPhotoArrivedListener) getLatestPhotos}
 * that receive num page and listener as a parameters and then get the 24 thump photos with there
 * details form Latest category from specified page and then call one of the listener methods
 * (onResult, onError, depends the situation) and run it on the caller thread - main thread.
 * So the main thread can fill the recycle view with Latest thumb photos in appropriate fragment.
 *
 * 3 - {@link #getRandomPhotos(int, OnThumbPhotoArrivedListener) getRandomPhotos}
 * that receive num page and listener as a parameters and then get the 24 thump photos with there
 * details form Latest category from specified page and then call one of the listener methods
 * (onResult, onError, depends the situation) and run it on the caller thread - main thread.
 * So the main thread can fill the recycle view with random thumb photos in appropriate fragment.
 */
public class ThumbPhotoDataSource {


    /**
     * To use
     * {@link #getToplistPhotos(int, OnThumbPhotoArrivedListener) getToplistPhotos},
     * {@link #getLatestPhotos(int, OnThumbPhotoArrivedListener) getLatestPhotos},
     * {@link #getRandomPhotos(int, OnThumbPhotoArrivedListener) getRandomPhotos},
     * you must send a listener as a parameter so we can notify you with the results.
     * In the appropriate method from those:
     * {@link #onResult(List)}, {@link #onError(Exception)}.
     * So one of those method will run on the caller thread - properly the main thread.
     */
    public interface OnThumbPhotoArrivedListener {
        void onResult(@NonNull List<ThumbPhoto> photos);
        void onError(@NonNull Exception e);
    }


    /** Template for the website we connect to him to get the thumb photos info's */
    private static final String ADDRESS_TEMPLATE ="https://wallhaven.cc/%s?page=%s";


    /** Template for the  search in the website we connect to him to get the searched thumb photos*/
    private static final String SEARCH_TEMPLATE ="https://wallhaven.cc/search?q=%s&search_image=&page=%s";


    /** The number of the thumb photos in on page as the website provide, correct for now. */
    private static int defaultSize=24;


    /** Single thread that get the thumb photos from Latest category, work asynchronous with others */
    private static ExecutorService serviceLatest= Executors.newSingleThreadExecutor();


    /** Single thread that get the thumb photos from toplist category, work asynchronous with others */
    private static ExecutorService serviceToplist= Executors.newSingleThreadExecutor();


    /** Single thread that get the thumb photos from random category, work asynchronous with others */
    private static ExecutorService serviceRandom= Executors.newSingleThreadExecutor();


    /** Single thread that get the thumb photos from random category, work asynchronous with others */
    private static ExecutorService serviceSearch= Executors.newSingleThreadExecutor();



    /**
     * Method that connect to the internet {@link #ADDRESS_TEMPLATE link addresss} and get the info
     * from the HTML file and parse it to create a list of {@link ThumbPhoto} objects, and return it.
     * This method work in sync way, we call her in a number of background threads.
     * @param pageNum is page number that you want to extract the thumb photos from her.
     * @param homePage is the category that you want to extract the photos from her,
     *                 we have 3 category (Latest, TopList, Random) in {@link home home enum}.
     * @return a list (ArrayList) of {@link ThumbPhoto thumb photos} that contain the info of
     * the thumb photos.
     * @throws IOException if it's fail to connect to the internet.
     */
    private static List<ThumbPhoto>  getThumbPhotosSync(int pageNum, home homePage)
            throws IOException {
        // parse everything
        final String address=String.format(ADDRESS_TEMPLATE,homePage.toString(),String.valueOf(pageNum));
        final List<ThumbPhoto> photos=new ArrayList<ThumbPhoto>(defaultSize);
        Document doc = Jsoup.connect(address).get();

        Elements elements = doc.select("figure");
        for (Element element : elements) {
            String photoId = element.attr("data-wallpaper-id");
            Elements div = element.select("div");
            //class="overlay-anchor wall-favs"
            String wallRes = div.select("[class*=wall-res]").text();

            ThumbPhoto photo= new ThumbPhoto(photoId,wallRes);
            photos.add(photo);
        }
        return photos;
    }



    /**
     * Helping method to reuse code :
     * Method that receive pageNum, listener, homePage, executorService as a parameters
     * and then get the 24 thump photos. With there details form homePage category
     * (it could be Latest, TopList or Random) from the specified pageNum,
     * and run the task on the executorService (single thread), we must send the appropriate
     * {@link ExecutorService single thread}.
     * After the task done (or fail) we call one of the listener methods.
     * (onResult, onError, depends the situation) and run it on the caller thread - main thread.
     *
     * @param pageNum is page number homePage photos category that you want to extract the thumb
     *                photos from it.
     *
     * @param listener is a listener that implement OnThumbPhotoArrivedListener Interface to notify
     *                 him when the the result is ready in :
     *                 {@link OnThumbPhotoArrivedListener#onResult(List)} method.
     *                 or an error accrued dairying the process in:
     *                 {@link OnThumbPhotoArrivedListener#onError(Exception)} method.
     *                 Notice those methods (onResult,onError) runs on the caller thread.
     *
     * @param homePage is the category that we want to extract the thumb photos from it,
     *                 (it could be Latest, TopList or Random).
     *
     * @param executorService We have 3 final single threads, to optimize the GC, because we use
     *                        them all the time. So executorService is the appropriate thread
     *                        to accomplish the task. if you want to get thump photos from :
     *                        Latest -> you must send serviceLatest thread {@link ExecutorService}
     *                        TopList -> you must send serviceTopList thread {@link ExecutorService}
     *                        Random -> you must send serviceRandom thread {@link ExecutorService}.
     *                        In other words the executorService is the
     *                        thread {@link ExecutorService} that must do the work in async way,
     *                        and when done/fail will call one of the listener methods.
     *
     */
    private static void getPhotos(final int pageNum,
                                  final home homePage,
                                  final OnThumbPhotoArrivedListener listener,
                                  @NonNull final ExecutorService executorService) {
        final android.os.Handler main = new android.os.Handler();
        executorService.submit(()->{
            try {
                List<ThumbPhoto> photos = getThumbPhotosSync(pageNum,homePage);
                /*code that runs on the main thread*/
                main.post(()-> listener.onResult(photos));
            }
            catch (IOException e) {
                /*code that runs on the main thread*/
                main.post(()-> listener.onError(e));
            }
        });
    }



    /**
     * Async method that receive num page and listener as a parameters and then get
     * the 24 thump photos with there details form Latest category from specified page
     * and then call one of the listener methods.
     * (onResult, onError, depends the situation) and run it on the caller thread - main thread.
     * @param pageNum is page number of latest photos category that you want to extract the thumb
     *                photos from it.
     * @param listener is a listener that implement OnThumbPhotoArrivedListener Interface to notify
     *                 him when the the result is ready in :
     *                 {@link OnThumbPhotoArrivedListener#onResult(List)} method.
     *                 or an error accrued dairying the process in:
     *                 {@link OnThumbPhotoArrivedListener#onError(Exception)} method.
     *                 Notice those methods (onResult,onError) runs on the caller thread.
     */
    public static void getLatestPhotos(final int pageNum,
                                       final OnThumbPhotoArrivedListener listener) {
        getPhotos(pageNum, home.Latest, listener, serviceLatest);
    }



    /**
     * Async method that receive num page and listener as a parameters and then get
     * the 24 thump photos with there details form toplist category from specified page
     * and then call one of the listener methods.
     * (onResult, onError, depends the situation) and run it on the caller thread - main thread.
     * @param pageNum is page number of toplist photos category that you want to extract the thumb
     *                photos from it.
     * @param listener is a listener that implement OnThumbPhotoArrivedListener Interface to notify
     *                 him when the the result is ready in :
     *                 {@link OnThumbPhotoArrivedListener#onResult(List)} method.
     *                 or an error accrued dairying the process in:
     *                 {@link OnThumbPhotoArrivedListener#onError(Exception)} method.
     *                 Notice those methods (onResult,onError) runs on the caller thread.
     */
    public static void getToplistPhotos(final int pageNum,
                                        final OnThumbPhotoArrivedListener listener) {
        getPhotos(pageNum, home.TopList, listener, serviceToplist);
    }



    /**
     * Async method that receive num page and listener as a parameters and then get
     * the 24 thump photos with there details form random category from specified page
     * and then call one of the listener methods.
     * (onResult, onError, depends the situation) and run it on the caller thread - main thread.
     * @param pageNum is page number of random photos category that you want to extract the thumb
     *                photos from it.
     * @param listener is a listener that implement OnThumbPhotoArrivedListener Interface to notify
     *                 him when the the result is ready in :
     *                 {@link OnThumbPhotoArrivedListener#onResult(List)} method.
     *                 or an error accrued dairying the process in:
     *                 {@link OnThumbPhotoArrivedListener#onError(Exception)} method.
     *                 Notice those methods (onResult,onError) runs on the caller thread.
     */
    public static void getRandomPhotos(int pageNum,
                                       final OnThumbPhotoArrivedListener listener) {
        getPhotos(pageNum, home.Random, listener, serviceRandom);
    }




    /* Search Util : */

    /**
     * Method that connect to the internet {@link #SEARCH_TEMPLATE link addresss} that contain the
     * searched word in it as a url, and get the info
     * from the HTML file and parse it to create a list of {@link ThumbPhoto} objects, and return it.
     * This method work in sync way, we call her in background thread.
     * @param pageNum is page number that you want to extract the searched thumb photos from her.
     * @param searchedWord is the phrase you want to search or the searched word.
     * @return a list (ArrayList) of {@link ThumbPhoto thumb photos} of the searched photos that
     * contain the info of the thumb photos.
     * @throws IOException if it's fail to connect to the internet.
     */
    private static List<ThumbPhoto>  getSearchedThumbPhotosSync(int pageNum, String searchedWord)
            throws IOException {
        // parse everything
        final String address = String.format(SEARCH_TEMPLATE,searchedWord,String.valueOf(pageNum));
        final List<ThumbPhoto> photos=new ArrayList<ThumbPhoto>(defaultSize);
        Document doc = Jsoup.connect(address).get();

        Elements elements = doc.select("figure");
        for (Element element : elements) {
            String photoId = element.attr("data-wallpaper-id");
            Elements div = element.select("div");
            //class="overlay-anchor wall-favs"
            String wallRes = div.select("[class*=wall-res]").text();

            ThumbPhoto photo= new ThumbPhoto(photoId,wallRes);
            photos.add(photo);
        }
        return photos;
    }



    /**
     * Method that receive pageNum, searched phrase (word) and listener as a parameters
     * and then get the 24 thump photos of the searched photos from the specified pageNum.
     * And run the task on the serviceSearch (single thread).
     * After the task done (or fail) we call one of the listener methods.
     * (onResult, onError, depends the situation) and run it on the caller thread - main thread.
     *
     * @param pageNum is page number of the searched photos that you want to extract the thumb
     *                photos of the searched photos from it.
     *
     * @param searchedWord is the phrase/word that the user enter to search photos, to show him
     *                     related photos of his search.
     *
     * @param listener is a listener that implement OnThumbPhotoArrivedListener Interface to notify
     *                 him when the the result is ready in :
     *                 {@link OnThumbPhotoArrivedListener#onResult(List)} method.
     *                 or an error accrued dairying the process in:
     *                 {@link OnThumbPhotoArrivedListener#onError(Exception)} method.
     *                 Notice those methods (onResult,onError) runs on the caller thread.
     *
     */
    public static void getSearchedPhotos(final int pageNum,
                                          final String searchedWord,
                                          final OnThumbPhotoArrivedListener listener) {
        final android.os.Handler main = new android.os.Handler();
        serviceSearch.submit(()->{
            try {
                List<ThumbPhoto> photos = getSearchedThumbPhotosSync(pageNum,searchedWord);
                /*code that runs on the main thread*/
                main.post(()-> listener.onResult(photos));
            }
            catch (IOException e) {
                /*code that runs on the main thread*/
                main.post(()-> listener.onError(e));
            }
        });
    }
}
