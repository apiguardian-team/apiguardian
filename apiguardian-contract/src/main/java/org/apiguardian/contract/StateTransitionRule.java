package org.apiguardian.contract;

import java.util.Collection;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

/**
 * Single rule describing allowed API element state transitions between versions.
 * Package-private, because it shouldn't be used by 3rd parties, but rather wrapped in graph-defining class used by them.
 * <p>
 * Use utility factory methods to create instances for readability.
 */
final class StateTransitionRule {
    private Predicate<APIElementState> previousStatePredicate;
    private Predicate<APIElementState> nextStatePredicate;
    private VersionComponentChange requiredVersionComponentChange;

    private StateTransitionRule(Predicate<APIElementState> previousStatePredicate,
                                Predicate<APIElementState> nextStatePredicate,
                                VersionComponentChange requiredVersionComponentChange) {
        this.previousStatePredicate = previousStatePredicate;
        this.nextStatePredicate = nextStatePredicate;
        this.requiredVersionComponentChange = requiredVersionComponentChange;
    }

    /**
     * If the transition between states allowed?
     * @param previousState State of a feature in previous analysed version
     * @param nextState State of a feature in next analysed version
     * @param versionComponentChange Most general change between analysed versions
     * @see VersionComponentChange
     * @return true if previous and next state predicates match arguments and major and minor version components changed
     *      according to requirements
     */
    public boolean isSatisfied(APIElementState previousState, APIElementState nextState,
                               VersionComponentChange versionComponentChange){
        return previousStatePredicate.test(previousState) && nextStatePredicate.test(nextState) &&
                versionComponentChange.compareTo(requiredVersionComponentChange) >=0;
    }

    static StateTransitionRule anytime(APIElementState previousState, APIElementState nextState){
        return anytime(asList(previousState), asList(nextState));
    }

    static StateTransitionRule anytime(APIElementState previousState, Collection<APIElementState> nextStates){
        return anytime(asList(previousState), nextStates);
    }

    static StateTransitionRule anytime(Collection<APIElementState> previousStates, APIElementState nextState){
        return anytime(previousStates, asList(nextState));
    }

    static StateTransitionRule anytime(Collection<APIElementState> previousStates,
                                       Collection<APIElementState> nextStates){
        return anytime(previousStates::contains, nextStates::contains);
    }

    static StateTransitionRule anytime(Predicate<APIElementState> previousStatePredicate,
                                       Predicate<APIElementState> nextStatePredicate){
        return new StateTransitionRule(previousStatePredicate, nextStatePredicate, VersionComponentChange.NONE);
    }

    static StateTransitionRule onMajorVersionIncrement(Collection<APIElementState> previousStates,
                                                       Collection<APIElementState> nextStates){
        return onMajorVersionIncrement(previousStates::contains, nextStates::contains);
    }

    static StateTransitionRule onMajorVersionIncrement(Predicate<APIElementState> previousStatePredicate,
                                                       Predicate<APIElementState> nextStatePredicate){
        return new StateTransitionRule(previousStatePredicate, nextStatePredicate, VersionComponentChange.MAJOR);
    }

    static StateTransitionRule onMinorVersionIncrement(APIElementState previousState, APIElementState nextState){
        return onMinorVersionIncrement(previousState::equals, nextState::equals);
    }

    static StateTransitionRule onMinorVersionIncrement(Predicate<APIElementState> previousStatePredicate,
                                                       Predicate<APIElementState> nextStatePredicate){
        return new StateTransitionRule(previousStatePredicate, nextStatePredicate, VersionComponentChange.MINOR);
    }
}
