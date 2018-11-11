package es.upm.miw.fem.firebase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class PaqueteAdapter extends ArrayAdapter<Paquete> {

    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<Paquete> mPaquetes;
    private List<String> mPaqueteIds = new ArrayList<>();

    public PaqueteAdapter(Context context, int resource, List<Paquete> objects) {
        super(context, resource, objects);
    }

    public PaqueteAdapter(final Context context, int resource, List<Paquete> objects, DatabaseReference ref) {
        super(context, resource, objects);

        mContext = context;
        mDatabaseReference = ref;
        mPaquetes = objects;

        // Create child event listener
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Paquete comment = dataSnapshot.getValue(Paquete.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                mPaqueteIds.add(dataSnapshot.getKey());
                mPaquetes.add(comment);
                //notifyItemInserted(mPaquetes.size() - 1);

                notifyDataSetChanged();
                notify
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Paquete newPaquete = dataSnapshot.getValue(Paquete.class);
                String commentKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int commentIndex = mPaqueteIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    // Replace with the new data
                    mPaquetes.set(commentIndex, newPaquete);

                    // Update the RecyclerView
                    //notifyItemChanged(commentIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int commentIndex = mPaqueteIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    // Remove data from the list
                    mPaqueteIds.remove(commentIndex);
                    mPaquetes.remove(commentIndex);

                    // Update the RecyclerView
                    //notifyItemRemoved(commentIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Paquete movedPaquete = dataSnapshot.getValue(Paquete.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postPaquetes:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        ref.addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_paquete, parent, false);
        }

        Paquete paquete = getItem(position);
        TextView paqueteTextView = convertView.findViewById(R.id.paqueteTextView);
        paqueteTextView.setText(paquete.getId());
        TextView paqueteDestinoTextView = convertView.findViewById(R.id.paqueteDestinoTextView);
        paqueteDestinoTextView.setText(paquete.getDestino());
        TextView paqueteEntregadoTextView = convertView.findViewById(R.id.paqueteEntregadoTextView);
        if (paquete.isEntregado()) {
            paqueteEntregadoTextView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

}
