package org.apiguardian.descriptor.model.evolution;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
@NoArgsConstructor //ugly, but required for serialization
public class VersionChange {
    private String previousVersion;
    private String nextVersion;
}
