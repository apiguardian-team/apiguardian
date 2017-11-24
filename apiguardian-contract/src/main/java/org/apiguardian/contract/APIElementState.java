package org.apiguardian.contract;

/**
 * todo: javadocs
 */
public enum APIElementState {
    NONE,
    INTERNAL,
    DEPRECATED,
    EXPERIMENTAL,
    MAINTAINED,
    STABLE;

    public boolean featureExists(){
        switch (this){
            case NONE: return false;
            default: return true;
        }
    }

    public boolean canBeUsed(){
        switch (this){
            case INTERNAL:
            case DEPRECATED: return false;
            default: return featureExists();
        }
    }

    //todo better name
    public boolean willBeInNextMinorVersion(){
        switch (this){
            case MAINTAINED:
            case STABLE: return true;
            default: return false;
        }
    }
}
