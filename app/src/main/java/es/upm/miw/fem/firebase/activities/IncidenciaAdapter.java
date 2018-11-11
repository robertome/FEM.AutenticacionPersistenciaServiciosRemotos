package es.upm.miw.fem.firebase.activities;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.upm.miw.fem.firebase.R;
import es.upm.miw.fem.firebase.models.Incidencia;

public class IncidenciaAdapter extends ArrayAdapter<Incidencia> {

    public IncidenciaAdapter(Context context, int resource, List<Incidencia> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_incidencia, parent, false);
        }

        Incidencia incidencia = getItem(position);
        TextView incidenciaTextView = convertView.findViewById(R.id.incidenciaTextView);
        incidenciaTextView.setText(incidencia.getDescripcion());
        TextView incidenciaFechaTextView = convertView.findViewById(R.id.incidenciaFechaTextView);
        incidenciaFechaTextView.setText(new DateFormat().format("yyyy-MM-dd hh:mm", incidencia.fechaAsDate()));

        return convertView;
    }
}
