package es.upm.miw.fem.firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 2018;

    private DatabaseReference paquetesDatabaseReference;

    private Button showActualizarLocalizacionButton;
    private PaqueteAdapter paqueteAdapter;
    private ListView paqueteListView;
    private ChildEventListener mChildEventListener;


    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paquetesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("repartidores").child(getUid()).child("paquetes");

        paqueteListView = findViewById(R.id.paqueteListView);
        showActualizarLocalizacionButton = findViewById(R.id.showActualizarLocalizacionButton);

        showActualizarLocalizacionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ActualizarLocalizacionActivity.newIntent(getApplicationContext()));
            }
        });

        paqueteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Paquete paquete = (Paquete) parent.getItemAtPosition(position);
                startActivity(IncidenciasActivity.newIntent(getApplicationContext(), paquete));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        paqueteAdapter = new PaqueteAdapter(this, R.layout.item_paquete, new ArrayList<Paquete>());
        paqueteListView.setAdapter(paqueteAdapter);
        if (mChildEventListener != null) {
            paquetesDatabaseReference.removeEventListener(mChildEventListener);
        }

        mChildEventListener = new PaqueteChildEventListener(paqueteAdapter);
        paquetesDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.logout_menu == item.getItemId()) {
            FirebaseAuth.getInstance().signOut();
            startActivity(LoginActivity.newIntent(getApplicationContext()));
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    class PaqueteChildEventListener implements ChildEventListener {

        private PaqueteAdapter paqueteAdapter;

        PaqueteChildEventListener(PaqueteAdapter paqueteAdapter) {
            this.paqueteAdapter = paqueteAdapter;
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Paquete paquete = dataSnapshot.getValue(Paquete.class);
            this.paqueteAdapter.add(paquete);
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
