package org.apiguardian.descriptor.model.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apiguardian.descriptor.utils.Visibility;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor //ugly, but required for serialization
@AllArgsConstructor
public class ClassElementNode<ElementDescriptor> {
    @JsonProperty
    private String declaration;
    @JsonProperty
    private Visibility visibility;
    @JsonProperty
    private ElementDescriptor descriptor;
}
