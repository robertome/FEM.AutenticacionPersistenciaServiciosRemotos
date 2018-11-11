package es.upm.miw.fem.firebase.activities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.upm.miw.fem.firebase.R;
import es.upm.miw.fem.firebase.models.Paquete;

public class PaqueteAdapter extends ArrayAdapter<Paquete> {

    public PaqueteAdapter(Context context, int resource, List<Paquete> objects) {
        super(context, resource, objects);
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

}
