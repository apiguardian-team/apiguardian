package org.apiguardian.descriptor.model;

import lombok.*;
import org.apiguardian.contract.APIElementState;

@ToString
@EqualsAndHashCode
public class ClassElementNode {
    String declaration;
    Visibility visibility;
    APIElementState state;

    static enum Visibility {
        PKG,
        PRIVATE,
        PROTECTED,
        PUBLIC
    }

    ClassElementNode(String declaration, Visibility visibility, APIElementState state) {
        this.declaration = declaration;
        this.visibility = visibility;
        this.state = state;
    }
}
