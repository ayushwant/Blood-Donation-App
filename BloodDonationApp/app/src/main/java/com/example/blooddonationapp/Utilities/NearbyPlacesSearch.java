package com.example.blooddonationapp.Utilities;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.blooddonationapp.Activities.MapActivity;
import com.example.blooddonationapp.BuildConfig;
import com.example.blooddonationapp.ModelClasses.NearbySearch.NearbyPlaceResponse;
import com.example.blooddonationapp.ModelClasses.NearbySearch.Results;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A Nearby Search lets you search for places within a specified area.
 * You can refine your search request by supplying keywords or specifying the type of place you are searching for.
 * https://developers.google.com/maps/documentation/places/web-service/search-nearby
 */
public class NearbyPlacesSearch {
    final static String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    final static String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;
    private static final String TAG = "NearbyPlacesSearch";

    public List<HashMap<String, String>> search(LatLng loc, String type, String radius) throws IOException
    {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("location", loc.latitude + "," + loc.longitude)
                .appendQueryParameter("rankby", "distance");

        // add radius (we can use only radius or rankby at once)
//        if (radius != null)
//            builder.appendQueryParameter("radius", radius);
//        else
//            builder.appendQueryParameter("radius", "1000"); //default 10 km

        // add type
        if (type != null)
            builder.appendQueryParameter("type", type);
        else
            builder.appendQueryParameter("type", "establishment");

        // add key
        builder.appendQueryParameter("key", MAPS_API_KEY);

        // build your url
        URL url = null;
        try {
            url = new URL(builder.build().toString());
            Log.d(TAG, String.valueOf(url));

            return returnResult(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<HashMap<String, String>> returnResult(URL url) throws IOException {
        Gson gson = new GsonBuilder().create();
        List<HashMap<String, String>> results = new ArrayList<>();

//        NearbyPlaceResponse nearbyPlaceResponse = null;
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                try {
                    URLConnection urlcon = url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

                    // get the response in the class
                    NearbyPlaceResponse nearbyPlaceResponse = gson.fromJson(br, NearbyPlaceResponse.class);

                    // now add required data to hashmap
                    if(nearbyPlaceResponse.getStatus().equals("OK"))
                    {
                        for (int i = 0; i < nearbyPlaceResponse.getResults().size(); i++)
                        {
                            Results result = nearbyPlaceResponse.getResults().get(i);
                            results.add(new HashMap<>());
                            results.get(i).put("placeName", result.getName());
                            results.get(i).put("placeRating", String.valueOf(result.getRating()));
                            results.get(i).put("totalRatings", String.valueOf(result.getUserRatingsTotal()));
                            results.get(i).put("placeType", result.getTypes().get(0)); // get the first type identifier
                            results.get(i).put("openStatus", result.getBusinessStatus() );
                            results.get(i).put("fullAddress", result.getVicinity());
                            results.get(i).put("placeID", result.getPlaceId());

                        }
                    }
                    else
                        return null;

                } catch (IOException | JsonSyntaxException | JsonIOException e) {
                    e.printStackTrace();
                }
//            }
//        }).start();



        return results;
    }

}
