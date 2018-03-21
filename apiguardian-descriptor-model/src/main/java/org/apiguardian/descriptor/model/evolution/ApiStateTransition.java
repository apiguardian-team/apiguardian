package org.apiguardian.descriptor.model.evolution;

import lombok.*;
import org.apiguardian.contract.ApiElementState;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
@NoArgsConstructor //ugly, but required for serialization
public class ApiStateTransition {
    private ApiElementState previousState;
    private ApiElementState nextState;
}
