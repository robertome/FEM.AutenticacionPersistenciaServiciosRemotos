package es.upm.miw.fem.firebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Firebase

public class MainActivity extends Activity {

    final static String LOG_TAG = "MiW";
    private static final int RC_SIGN_IN = 2018;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private PaqueteRepository repository;
    private ChildEventListener paquetesChildEventListener;
    private PaqueteAdapter paqueteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button actualizarLocalizacionButton = findViewById(R.id.actualizarLocalizacionButton);
        actualizarLocalizacionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ActualizarLocalizacionActivity.newIntent(getApplicationContext(), firebaseAuth.getCurrentUser().getUid()));
            }
        });

        List<Paquete> paquetes = new ArrayList<>();
        paqueteAdapter = new PaqueteAdapter(this, R.layout.item_paquete, paquetes);
        paquetesChildEventListener = new PaqueteChildEventListener(paqueteAdapter);
        ListView paqueteListView = findViewById(R.id.paqueteListView);
        paqueteListView.setAdapter(paqueteAdapter);
        paqueteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Paquete paquete = (Paquete) parent.getItemAtPosition(position);
                startActivity(IncidenciasActivity.newIntent(getApplicationContext(), firebaseAuth.getCurrentUser().getUid(), paquete));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed id
                    onSignedInInitialize(user);
                } else {
                    // user ins signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onPause() {
        firebaseAuth.removeAuthStateListener(authStateListener);
        if (repository != null) {
            repository.removeChildEventListener(paquetesChildEventListener);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void onSignedOutCleanup() {
        if (repository != null) {
            repository.removeChildEventListener(paquetesChildEventListener);
            repository = null;
        }
        paqueteAdapter.clear();
    }

    private void onSignedInInitialize(FirebaseUser currentUser) {
        Toast.makeText(MainActivity.this, getString(R.string.firebase_user_fmt, currentUser.getEmail()), Toast.LENGTH_LONG).show();
        if (repository == null) {
            repository = new PaqueteRepository(currentUser.getUid()).addChildEventListener(paquetesChildEventListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu:
                firebaseAuth.signOut();
                //startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.signed_in, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "onActivityResult " + getString(R.string.signed_in));
                //onSignedInInitialize(firebaseAuth.getCurrentUser());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.signed_cancelled, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "onActivityResult " + getString(R.string.signed_cancelled));
                finish();
            }
        }
    }

    static class PaqueteRepository {

        FirebaseDatabase firebaseDatabase;
        private DatabaseReference paquetesDatabaseReference;

        PaqueteRepository(String userId) {
            this.firebaseDatabase = FirebaseDatabase.getInstance();
            this.paquetesDatabaseReference = firebaseDatabase.getReference().child("repartidores").child(userId).child("paquetes");
        }

        DatabaseReference getPaquetesDatabaseReference() {
            return paquetesDatabaseReference;
        }

        PaqueteRepository addChildEventListener(ChildEventListener childEventListener) {
            if (paquetesDatabaseReference != null && childEventListener != null) {
                paquetesDatabaseReference.addChildEventListener(childEventListener);
            }

            return this;
        }

        PaqueteRepository removeChildEventListener(ChildEventListener childEventListener) {
            if (paquetesDatabaseReference != null && childEventListener != null) {
                paquetesDatabaseReference.removeEventListener(childEventListener);
            }

            return this;
        }

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
            Paquete paquete = dataSnapshot.getValue(Paquete.class);
            this.paqueteAdapter.add(paquete);
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
