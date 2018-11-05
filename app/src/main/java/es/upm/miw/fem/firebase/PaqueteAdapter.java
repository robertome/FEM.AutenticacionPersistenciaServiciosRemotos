package es.upm.miw.fem.firebase;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.upm.miw.fem.R;

public class PaqueteAdapter extends ArrayAdapter<Paquete> {
    public PaqueteAdapter(Context context, int resource, List<Paquete> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_paquete, parent, false);
        }

        Paquete message = getItem(position);
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        messageTextView.setText(message.getUid() + " " + message.getName());

        return convertView;
    }
}
