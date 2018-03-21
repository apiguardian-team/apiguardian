package org.apiguardian.descriptor.model.evolution;

import lombok.Value;
import org.apiguardian.contract.ApiElementState;
import org.apiguardian.descriptor.model.ArtifactDescriptor;

@Value(staticConstructor = "between")
public class ArtifactEvolution {
    private ArtifactDescriptor<String, ApiElementState> previousDescriptor;
    private ArtifactDescriptor<String, ApiElementState> nextDescriptor;
}
