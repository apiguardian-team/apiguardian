package org.apiguardian.descriptor.validation;

import lombok.Value;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

//todo this needs some consideration, probably we'll want some arguments/payload, not only a message
@Value(staticConstructor = "of")
public final class Violation<T> {
    private final T target;
    private String message;

    @Override
    public String toString() {
        return "Violation<"+formatNameWithShortPackage(target.getClass())+">{" +
            "message="+message+
            '}';
    }

    private static String formatNameWithShortPackage(Class clazz){
        String simpleName = clazz.getSimpleName();
        String shortenedPackage = Stream.of(clazz.getPackage().getName().split("[.]")).
            map(p -> p.substring(0, 1)).
            collect(joining("."));
        return shortenedPackage+"."+simpleName;
    }
}
