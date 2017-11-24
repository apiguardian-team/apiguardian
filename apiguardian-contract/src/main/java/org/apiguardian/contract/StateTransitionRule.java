package org.apiguardian.contract;

import java.util.Collection;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

public final class StateTransitionRule {
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

    public boolean isSatisfied(APIElementState previousState, APIElementState nextState,
                               boolean majorVersionChanged, boolean minorVersionChanged){
        return previousStatePredicate.test(previousState) && nextStatePredicate.test(nextState) &&
            (!requiresMajorVersionIncrement || majorVersionChanged) &&
            (!requiresMinorVersionIncrement || minorVersionChanged);
    }

    //todo: better style in these utility methods (prevStatePREDICATE, break lines, etc)

    static StateTransitionRule anytime(APIElementState previousState, APIElementState nextState){
        return anytime(asList(previousState), asList(nextState));
    }

    static StateTransitionRule anytime(APIElementState previousState, Collection<APIElementState> nextStates){
        return anytime(asList(previousState), nextStates);
    }

    static StateTransitionRule anytime(Collection<APIElementState> previousStates, APIElementState nextState){
        return anytime(previousStates, asList(nextState));
    }

    static StateTransitionRule anytime(Collection<APIElementState> previousStates, Collection<APIElementState> nextStates){
        return anytime(previousStates::contains, nextStates::contains);
    }

    static StateTransitionRule anytime(Predicate<APIElementState> previousState, Predicate<APIElementState> nextState){
        return new StateTransitionRule(previousState, nextState, false, false);
    }

    static StateTransitionRule onMajorVersionIncrement(Collection<APIElementState> previousStates, Collection<APIElementState> nextStates){
        return onMajorVersionIncrement(previousStates::contains, nextStates::contains);
    }

    static StateTransitionRule onMajorVersionIncrement(Predicate<APIElementState> previousState, Predicate<APIElementState> nextState){
        return new StateTransitionRule(previousState, nextState, true, false);
    }

    static StateTransitionRule onMinorVersionIncrement(APIElementState previousState, APIElementState nextState){
        return onMinorVersionIncrement(previousState::equals, nextState::equals);
    }

    static StateTransitionRule onMinorVersionIncrement(Predicate<APIElementState> previousState, Predicate<APIElementState> nextState){
        return new StateTransitionRule(previousState, nextState, false,true);
    }
}
