package org.apiguardian.descriptor.model;

import lombok.Value;

import java.net.URL;

@Value(staticConstructor = "of")
public class VersionedArtifact {
    private String version;
    private URL location;
}
