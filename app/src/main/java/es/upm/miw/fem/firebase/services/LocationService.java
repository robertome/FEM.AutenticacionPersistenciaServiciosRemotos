package es.upm.miw.fem.firebase.services;

import android.content.Context;
import android.util.Log;

import es.upm.miw.fem.firebase.models.InfoLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationService {

    private static final String LOG_TAG = "LocationService";
    private static final String API_BASE_URL = "http://ip-api.com";

    private Context context;
    private LocationRESTAPIService locationRESTAPIService;
    private IpResolver ipResolver;

    public LocationService(Context context) {
        this.context = context;
        this.ipResolver = new TestIpResolver(context);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        locationRESTAPIService = retrofit.create(LocationRESTAPIService.class);
    }

    public void getInfoLocation(Callback<InfoLocation> callback) {
        String ip = ipResolver.getIp();

        Log.i(LOG_TAG, "getInfoLocation() with IPAdress: " + ip);

        Call<InfoLocation> call = locationRESTAPIService.getInfoLocationByIP(ip);

        // Asíncrona
        call.enqueue(callback);
    }


}
