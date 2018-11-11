package es.upm.miw.fem.firebase.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.upm.miw.fem.firebase.R;
import es.upm.miw.fem.firebase.models.InfoLocation;
import es.upm.miw.fem.firebase.models.Paquete;
import es.upm.miw.fem.firebase.services.LocationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarLocalizacionActivity extends BaseActivity {

    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private DatabaseReference paquetesDatabaseReference;
    private LocationService locationService;

    private EditText localizacionEditText;
    private Button actualizarLocalizacionSendButton;
    private Button actualizarLocalizacionButton;

    public static Intent newIntent(Context context) {
        return new Intent(context, ActualizarLocalizacionActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_localizacion);

        paquetesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("repartidores").child(getUid()).child("paquetes");
        locationService = new LocationService(getApplicationContext());

        localizacionEditText = findViewById(R.id.localizacionEditText);
        actualizarLocalizacionButton = findViewById(R.id.actualizarLocalizacionButton);
        actualizarLocalizacionSendButton = findViewById(R.id.actualizarLocalizacionSendButton);

        localizacionEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        localizacionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    actualizarLocalizacionSendButton.setEnabled(true);
                } else {
                    actualizarLocalizacionSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        actualizarLocalizacionSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitLocalizacion();
            }
        });

        actualizarLocalizacionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateLocalizacion();
            }
        });
    }

    private void calculateLocalizacion() {
        showProgressDialog();
        locationService.getInfoLocation(new Callback<InfoLocation>() {

            @Override
            public void onResponse(Call<InfoLocation> call, Response<InfoLocation> response) {
                InfoLocation infoLocation = response.body();

                Log.i(LOG_TAG, "Valor devuelto por el servicio de localizacion: " + infoLocation.toString());

                localizacionEditText.setText(String.format("%s, %s, %s, %s", infoLocation.getCity(), infoLocation.getZip(), infoLocation.getRegionName(), infoLocation.getCountry()));

                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<InfoLocation> call, Throwable t) {
                hideProgressDialog();

                Toast.makeText(
                        ActualizarLocalizacionActivity.this,
                        "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

                Log.e(LOG_TAG, t.getMessage());
            }
        });
    }

    private void submitLocalizacion() {
        final String localizacion = localizacionEditText.getText().toString();

        Query paquetesNoEntregadosQuery = paquetesDatabaseReference.orderByChild("fechaEntrega").equalTo(null);
        paquetesNoEntregadosQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot paqueteSnapshot : dataSnapshot.getChildren()) {
                    Paquete paquete = paqueteSnapshot.getValue(Paquete.class);

                    paquetesDatabaseReference.child(paquete.getId()).child("localizaciones").push().setValue(localizacion);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        finish();
    }

}
