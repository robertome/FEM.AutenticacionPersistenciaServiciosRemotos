package es.upm.miw.fem.firebase;

public interface Converter<S, T> {

    T convert(S obj);

}
