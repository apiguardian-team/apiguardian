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
    private Predicate<ApiElementState> previousStatePredicate;
    private Predicate<ApiElementState> nextStatePredicate;
    private VersionComponentChange requiredVersionComponentChange;

    private StateTransitionRule(Predicate<ApiElementState> previousStatePredicate,
                                Predicate<ApiElementState> nextStatePredicate,
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
    public boolean isSatisfied(ApiElementState previousState, ApiElementState nextState,
                               VersionComponentChange versionComponentChange){
        return previousStatePredicate.test(previousState) && nextStatePredicate.test(nextState) &&
                versionComponentChange.compareTo(requiredVersionComponentChange) >=0;
    }

    static StateTransitionRule anytime(ApiElementState previousState, ApiElementState nextState){
        return anytime(asList(previousState), asList(nextState));
    }

    static StateTransitionRule anytime(ApiElementState previousState, Collection<ApiElementState> nextStates){
        return anytime(asList(previousState), nextStates);
    }

    static StateTransitionRule anytime(Collection<ApiElementState> previousStates, ApiElementState nextState){
        return anytime(previousStates, asList(nextState));
    }

    static StateTransitionRule anytime(Collection<ApiElementState> previousStates,
                                       Collection<ApiElementState> nextStates){
        return anytime(previousStates::contains, nextStates::contains);
    }

    static StateTransitionRule anytime(Predicate<ApiElementState> previousStatePredicate,
                                       Predicate<ApiElementState> nextStatePredicate){
        return new StateTransitionRule(previousStatePredicate, nextStatePredicate, VersionComponentChange.NONE);
    }

    static StateTransitionRule onMajorVersionIncrement(Collection<ApiElementState> previousStates,
                                                       Collection<ApiElementState> nextStates){
        return onMajorVersionIncrement(previousStates::contains, nextStates::contains);
    }

    static StateTransitionRule onMajorVersionIncrement(Predicate<ApiElementState> previousStatePredicate,
                                                       Predicate<ApiElementState> nextStatePredicate){
        return new StateTransitionRule(previousStatePredicate, nextStatePredicate, VersionComponentChange.MAJOR);
    }

    static StateTransitionRule onMinorVersionIncrement(ApiElementState previousState, ApiElementState nextState){
        return onMinorVersionIncrement(previousState::equals, nextState::equals);
    }

    static StateTransitionRule onMinorVersionIncrement(Predicate<ApiElementState> previousStatePredicate,
                                                       Predicate<ApiElementState> nextStatePredicate){
        return new StateTransitionRule(previousStatePredicate, nextStatePredicate, VersionComponentChange.MINOR);
    }
}
