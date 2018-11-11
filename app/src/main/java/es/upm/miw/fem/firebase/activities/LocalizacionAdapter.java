package es.upm.miw.fem.firebase.activities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.upm.miw.fem.firebase.R;

public class LocalizacionAdapter extends ArrayAdapter<String> {

    public LocalizacionAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_localizacion, parent, false);
        }

        String localizacion = getItem(position);
        TextView localizacionTextView = convertView.findViewById(R.id.localizacionTextView);
        localizacionTextView.setText(localizacion);

        return convertView;
    }
}
