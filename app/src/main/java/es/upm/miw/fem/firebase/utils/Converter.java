package es.upm.miw.fem.firebase.utils;

public interface Converter<S, T> {

    T convert(S obj);

}
