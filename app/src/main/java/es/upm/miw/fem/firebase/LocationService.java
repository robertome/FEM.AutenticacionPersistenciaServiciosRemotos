package es.upm.miw.fem.firebase;

import android.content.Context;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

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

    LocationService(Context context) {
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

    interface IpResolver {
        String getIp();
    }

    static class TestIpResolver implements IpResolver {

        Context context;

        TestIpResolver(Context context) {
            this.context = context;
        }

        @Override
        public String getIp() {
            return context.getString(R.string.ip_test);
        }
    }

    static class MobileIpResolver implements IpResolver {

        @Override
        public String getIp() {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            return addr.getHostAddress();
                        }
                    }
                }
            } catch (Exception ex) {
            } // for now eat exceptions

            return null;
        }
    }
}
