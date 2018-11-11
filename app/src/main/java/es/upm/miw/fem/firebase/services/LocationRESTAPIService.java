package es.upm.miw.fem.firebase.services;


import es.upm.miw.fem.firebase.models.InfoLocation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


interface LocationRESTAPIService {

    @GET("/json/{ip}")
    Call<InfoLocation> getInfoLocationByIP(@Path("ip") String ip);

}
