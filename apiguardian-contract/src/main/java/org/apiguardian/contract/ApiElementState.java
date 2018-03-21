package org.apiguardian.contract;

/**
 * Describes state of the API element (a feature) in context of some version.
 * Explicitly maps to org.apiguardian.api.API.Status and extends status set with NONE.
 * <p>
 * There is no mapping method (that would turn mentioned enum to this one) to avoid dependency on API module.
 */
public enum ApiElementState {
    /**
     * Feature is not present in analysed version.
     */
    NONE,
    /**
     * Feature is described as o.a.a.API.Status.INTERNAL in analysed version.
     */
    INTERNAL,
    /**
     * Feature is described as o.a.a.API.Status.DEPRECATED in analysed version.
     */
    DEPRECATED,
    /**
     * Feature is described as o.a.a.API.Status.EXPERIMENTAL in analysed version.
     */
    EXPERIMENTAL,
    /**
     * Feature is described as o.a.a.API.Status.MAINTAINED in analysed version.
     */
    MAINTAINED,
    /**
     * Feature is described as o.a.a.API.Status.STABLE in analysed version.
     */
    STABLE;

    /**
     * Is the feature available in analysed version?
     * @return true if feature is available (with any status) in analysed version, false in other case
     */
    public boolean featureExists(){
        switch (this){
            case NONE: return false;
            default: return true;
        }
    }

    /**
     * Should this feature be used when solving a new case?
     * @return true if feature exists and is not internal or deprecated, false in other case
     */
    public boolean canBeSafelyUsed(){
        switch (this){
            case INTERNAL:
            case DEPRECATED: return false;
            default: return featureExists();
        }
    }


    /**
     * Is this feature expected to still exist in next minor version?
     * @return true if the feature is maintained or stable, false in other case
     */
    //todo better name - suggestions welcome
    public boolean willNotDisappear(){
        switch (this){
            case MAINTAINED:
            case STABLE: return true;
            default: return false;
        }
    }
}
