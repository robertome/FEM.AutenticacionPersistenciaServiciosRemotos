package es.upm.miw.fem.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ActualizarLocalizacionActivity extends Activity {

    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final String KEY_REPARTIDOR_ID = "KEY_REPARTIDOR_ID";

    private EditText localizacionEditText;
    private Button actualizarLocalizacionSendButton;

    private String repartidorId;

    public static Intent newIntent(Context context, String uid) {
        Intent intent = new Intent(context, ActualizarLocalizacionActivity.class);
        intent.putExtra(KEY_REPARTIDOR_ID, uid);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_localizacion);

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        recoverBundleParameters(bundle);

        localizacionEditText = findViewById(R.id.localizacionEditText);
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

    }

    private void submitLocalizacion() {
        final String localizacion = localizacionEditText.getText().toString();
        final MainActivity.PaqueteRepository repository = new MainActivity.PaqueteRepository(repartidorId);
        Query paquetesNoEntregadosQuery = repository.getPaquetesDatabaseReference().orderByChild("fechaEntrega").equalTo(null);
        paquetesNoEntregadosQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot paqueteSnapshot : dataSnapshot.getChildren()) {
                    Paquete paquete = paqueteSnapshot.getValue(Paquete.class);
                    repository.getPaquetesDatabaseReference().child(paquete.getId()).child("localizaciones").push().setValue(localizacion);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_REPARTIDOR_ID, repartidorId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recoverBundleParameters(savedInstanceState);
    }

    private void recoverBundleParameters(Bundle bundle) {
        repartidorId = bundle.getString(KEY_REPARTIDOR_ID);
    }
}
