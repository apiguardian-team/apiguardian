package org.apiguardian.descriptor;

public interface Describer<Described, Descriptor> {
    Descriptor describe(Described described);
}
