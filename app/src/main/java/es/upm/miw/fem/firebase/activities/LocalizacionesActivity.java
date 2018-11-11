package es.upm.miw.fem.firebase.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import es.upm.miw.fem.firebase.R;
import es.upm.miw.fem.firebase.models.Paquete;

public class LocalizacionesActivity extends BaseActivity {

    private static final String KEY_PAQUETE = "LocalizacionesActivity.KEY_PAQUETE";
    private DatabaseReference localizacionesDatabaseReference;
    private ChildEventListener childEventListener;

    private ListView localizacionesListView;

    private Paquete paquete;

    public static Intent newIntent(Context context, Paquete paquete) {
        Intent intent = new Intent(context, LocalizacionesActivity.class);
        intent.putExtra(KEY_PAQUETE, paquete);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizaciones);

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        recoverBundleParameters(bundle);

        localizacionesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("repartidores").child(getUid())
                .child("paquetes").child(paquete.getId()).child("localizaciones");

        localizacionesListView = findViewById(R.id.localizacionesListView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalizacionAdapter localizacionAdapter = new LocalizacionAdapter(this, R.layout.item_localizacion, new ArrayList<String>());
        localizacionesListView.setAdapter(localizacionAdapter);
        childEventListener = new LocalizacionesChildEventListener(localizacionAdapter);
        localizacionesDatabaseReference.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (childEventListener != null) {
            localizacionesDatabaseReference.removeEventListener(childEventListener);
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
    }

    private void recoverBundleParameters(Bundle bundle) {
        paquete = (Paquete) bundle.getSerializable(KEY_PAQUETE);
    }

    static class LocalizacionesChildEventListener implements ChildEventListener {

        private LocalizacionAdapter localizacionAdapter;

        LocalizacionesChildEventListener(LocalizacionAdapter localizacionAdapter) {
            this.localizacionAdapter = localizacionAdapter;
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String localizacion = dataSnapshot.getValue(String.class);
            this.localizacionAdapter.add(localizacion);
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
