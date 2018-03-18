package org.apiguardian.contract;

import java.util.Collection;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

/**
 * Single rule describing allowed API element state transitions between versions.
 * Package-private, because it shouldn't be used by 3rd parties, but rather wrapped in graph-defining class used by them.
 *
 * Use utility factory methods to create instances for readability.
 */
final class StateTransitionRule {
    private Predicate<APIElementState> previousStatePredicate;
    private Predicate<APIElementState> nextStatePredicate;
    private boolean requiresMajorVersionIncrement;
    private boolean requiresMinorVersionIncrement;

    private StateTransitionRule(Predicate<APIElementState> previousStatePredicate,
                               Predicate<APIElementState> nextStatePredicate,
                               boolean requiresMajorVersionIncrement, boolean requiresMinorVersionIncrement) {
        this.previousStatePredicate = previousStatePredicate;
        this.nextStatePredicate = nextStatePredicate;
        this.requiresMajorVersionIncrement = requiresMajorVersionIncrement;
        this.requiresMinorVersionIncrement = requiresMinorVersionIncrement;
    }

    /**
     * If the transition between states allowed?
     * @param previousState State of a feature in previous analysed version
     * @param nextState State of a feature in next analysed version
     * @param majorVersionChanged Has major version component changed between previous and next analysed version?
     * @param minorVersionChanged Has minor version component changed between previous and next analysed version?
     * @return true if previous and next state predicates match arguments and major and minor version components changed
     *      according to requirements
     */
    public boolean isSatisfied(APIElementState previousState, APIElementState nextState,
                               boolean majorVersionChanged, boolean minorVersionChanged){
        return previousStatePredicate.test(previousState) && nextStatePredicate.test(nextState) &&
            (!requiresMajorVersionIncrement || majorVersionChanged) &&
            //one could argue that following line should have one more alternative branch: ... || majorVersionChanged
            //but this should be covered when defining a graph instead, not inside rule logic
            (!requiresMinorVersionIncrement || minorVersionChanged);
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
        return new StateTransitionRule(previousStatePredicate, nextStatePredicate, false, false);
    }

    static StateTransitionRule onMajorVersionIncrement(Collection<APIElementState> previousStates,
                                                       Collection<APIElementState> nextStates){
        return onMajorVersionIncrement(previousStates::contains, nextStates::contains);
    }

    static StateTransitionRule onMajorVersionIncrement(Predicate<APIElementState> previousStatePredicate,
                                                       Predicate<APIElementState> nextStatePredicate){
        return new StateTransitionRule(previousStatePredicate, nextStatePredicate, true, false);
    }

    static StateTransitionRule onMinorVersionIncrement(APIElementState previousState, APIElementState nextState){
        return onMinorVersionIncrement(previousState::equals, nextState::equals);
    }

    static StateTransitionRule onMinorVersionIncrement(Predicate<APIElementState> previousStatePredicate,
                                                       Predicate<APIElementState> nextStatePredicate){
        return new StateTransitionRule(previousStatePredicate, nextStatePredicate, false,true);
    }
}
