package es.upm.miw.fem.firebase.services;

import android.content.Context;

import es.upm.miw.fem.firebase.R;

class TestIpResolver implements IpResolver {

    private Context context;

    TestIpResolver(Context context) {
        this.context = context;
    }

    @Override
    public String getIp() {
        return context.getString(R.string.ip_test);
    }
}