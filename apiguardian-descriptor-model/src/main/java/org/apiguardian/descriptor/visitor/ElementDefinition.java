package org.apiguardian.descriptor.visitor;

import lombok.Builder;
import lombok.Value;
import org.apiguardian.descriptor.utils.Visibility;

@Value
@Builder(toBuilder = true)
public class ElementDefinition {
    private String packageName;
    private String className;
    private String declaration;
    private Visibility classVisibility;
    private Visibility elementVisibility;
}
