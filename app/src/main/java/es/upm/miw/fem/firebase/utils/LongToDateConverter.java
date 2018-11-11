package es.upm.miw.fem.firebase.utils;

import java.util.Date;

public class LongToDateConverter implements Converter<Long, Date> {
    @Override
    public Date convert(Long timestamp) {
        return timestamp == null || timestamp == 0 ? null : new Date(timestamp);
    }
}
