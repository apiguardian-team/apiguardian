package org.apiguardian.descriptor.validation;

import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public interface Validator<T> {
    Set<Violation<T>> validate(T target);

    default void throwIfInvalid(T target){
        Set<Violation<T>> violations = validate(target);
        if (!violations.isEmpty()) {
            RuntimeException up = new ValidationFailedException(violations, target.getClass());
            throw up;
        }
    }

    class ValidationFailedException extends RuntimeException {
        @Getter private final Set<Violation> violations;
        @Getter private final Class targetClass;

        public <T> ValidationFailedException(Set<Violation<T>> violations, Class targetClass) {
            super(
                "Validation failed with following violations: "+
                    violations.stream().
                        map(Violation::getMessage).
                        collect(toList())
            );
            this.violations = violations.stream().map(v -> (Violation) v).collect(Collectors.toSet());
            this.targetClass = targetClass;
        }
    }
}
