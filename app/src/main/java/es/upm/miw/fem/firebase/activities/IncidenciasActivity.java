package es.upm.miw.fem.firebase.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import es.upm.miw.fem.firebase.R;
import es.upm.miw.fem.firebase.models.Incidencia;
import es.upm.miw.fem.firebase.models.Paquete;

public class IncidenciasActivity extends BaseActivity {

    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final String KEY_PAQUETE = "IncidenciasActivity.KEY_PAQUETE";

    private DatabaseReference paqueteDatabaseReference;
    private DatabaseReference incidenciasDatabaseReference;
    private ChildEventListener childEventListener;


    private TextView infoPaqueteIdTextView;
    private TextView infoPaqueteOrigenTextView;
    private TextView infoPaqueteDestinoTextView;
    private TextView infoPaqueteFechaRecogidaTextView;
    private TextView infoPaqueteFechaEntregaTextView;
    private Button paqueteEntregarButton;
    private ListView incidenciaListView;
    private EditText incidenciaEditText;
    private Button incidenciaSendButton;

    private Paquete paquete;

    public static Intent newIntent(Context context, Paquete paquete) {
        Intent intent = new Intent(context, IncidenciasActivity.class);
        intent.putExtra(KEY_PAQUETE, paquete);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencias);

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        recoverBundleParameters(bundle);

        paqueteDatabaseReference = FirebaseDatabase.getInstance().getReference().child("repartidores").child(getUid()).child("paquetes").child(paquete.getId());
        incidenciasDatabaseReference = paqueteDatabaseReference.child("incidencias");

        infoPaqueteIdTextView = findViewById(R.id.infoPaqueteIdTextView);
        infoPaqueteOrigenTextView = findViewById(R.id.infoPaqueteOrigenTextView);
        infoPaqueteDestinoTextView = findViewById(R.id.infoPaqueteDestinoTextView);
        infoPaqueteFechaRecogidaTextView = findViewById(R.id.infoPaqueteFechaRecogidaTextView);
        infoPaqueteFechaEntregaTextView = findViewById(R.id.infoPaqueteFechaEntregaTextView);
        paqueteEntregarButton = findViewById(R.id.paqueteEntregarButton);
        incidenciaEditText = findViewById(R.id.incidenciaEditText);
        incidenciaListView = findViewById(R.id.incidenciaListView);
        incidenciaEditText = findViewById(R.id.incidenciaEditText);
        incidenciaSendButton = findViewById(R.id.incidenciaSendButton);

        paqueteEntregarButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                paquete.setFechaEntrega(System.currentTimeMillis());

                updateUI();

                paqueteDatabaseReference.child("fechaEntrega").setValue(paquete.getFechaEntrega());
            }
        });

        incidenciaEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        incidenciaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    incidenciaSendButton.setEnabled(true);
                } else {
                    incidenciaSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        incidenciaSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIncidencia(incidenciaEditText.getText().toString());

                incidenciaEditText.setText("");
            }
        });

        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IncidenciaAdapter incidenciaAdapter = new IncidenciaAdapter(this, R.layout.item_incidencia, new ArrayList<Incidencia>());
        incidenciaListView.setAdapter(incidenciaAdapter);
        childEventListener = new IncidenciaChildEventListener(incidenciaAdapter);
        incidenciasDatabaseReference.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (childEventListener != null) {
            incidenciasDatabaseReference.removeEventListener(childEventListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_PAQUETE, paquete);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recoverBundleParameters(savedInstanceState);
        updateUI();
    }

    private void recoverBundleParameters(Bundle bundle) {
        paquete = (Paquete) bundle.getSerializable(KEY_PAQUETE);
    }

    private void updateUI() {
        infoPaqueteIdTextView.setText(paquete.getId());
        infoPaqueteOrigenTextView.setText(paquete.getOrigen());
        infoPaqueteDestinoTextView.setText(paquete.getDestino());
        infoPaqueteFechaRecogidaTextView.setText(paquete.fechaInicioAsString());
        infoPaqueteFechaEntregaTextView.setText(paquete.fechaEntregaAsString());
        paqueteEntregarButton.setEnabled(!paquete.isEntregado());
        incidenciaEditText.setEnabled(!paquete.isEntregado());
    }

    private void saveIncidencia(String descripcion) {
        Incidencia incidencia = new Incidencia(incidenciasDatabaseReference.push().getKey(), paquete.getId(), descripcion);
        incidenciasDatabaseReference.child(incidencia.getId()).setValue(incidencia);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_paquete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.localizaciones_menu == item.getItemId()) {
            startActivity(LocalizacionesActivity.newIntent(getApplicationContext(), paquete));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class IncidenciaChildEventListener implements ChildEventListener {

        private IncidenciaAdapter incidenciaAdapter;

        IncidenciaChildEventListener(IncidenciaAdapter incidenciaAdapter) {
            this.incidenciaAdapter = incidenciaAdapter;
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Incidencia incidencia = dataSnapshot.getValue(Incidencia.class);
            this.incidenciaAdapter.add(incidencia);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }

}
