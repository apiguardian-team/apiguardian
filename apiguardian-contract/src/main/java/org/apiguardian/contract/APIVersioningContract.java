package org.apiguardian.contract;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.apiguardian.contract.StateTransitionRule.*;
import static org.apiguardian.contract.APIElementState.*;

/**
 * Class containing static methods for analysing API state graph.
 * Internally holds static graph description and is able to query it to answer questions on transition validity
 * or possible transitions.
 */
public class APIVersioningContract {
    private APIVersioningContract() {
    }

    private static final Collection<APIElementState> ALL_STATES = asList(APIElementState.values());

    public static List<StateTransitionRule> rules = asList(
        onMajorVersionIncrement(ALL_STATES, ALL_STATES),
        anytime(asList(INTERNAL, EXPERIMENTAL), NONE),
        onMinorVersionIncrement(DEPRECATED, NONE),
        anytime(EXPERIMENTAL, asList(DEPRECATED, MAINTAINED, STABLE)),
        onMinorVersionIncrement(MAINTAINED, DEPRECATED),
        anytime(MAINTAINED, STABLE)
    );

    public static boolean isValidTransition(APIElementState previousState, APIElementState nextState,
                                            boolean majorVersionChanged, boolean minorVersionChanged){
        return rules.stream().
            map(rule ->
                rule.isSatisfied(previousState, nextState, majorVersionChanged, minorVersionChanged)
            ).
            findAny().
            isPresent();
    }

    public static Set<APIElementState> findValidNextStates(APIElementState previousState,
                                                           boolean majorVersionChanged, boolean minorVersionChanged){
        return Stream.of(APIElementState.values()).
            filter(state ->
                isValidTransition(previousState, state, majorVersionChanged, minorVersionChanged)
            ).
            collect(Collectors.toSet());
    }

    public static Set<APIElementState> findValidPreviousStates(APIElementState nextState,
                                                           boolean majorVersionChanged, boolean minorVersionChanged){
        return Stream.of(APIElementState.values()).
            filter(state ->
                isValidTransition(state, nextState, majorVersionChanged, minorVersionChanged)
            ).
            collect(Collectors.toSet());
    }

    //todo: introduce VersionChangeEnum, replace two bool args with it
    //todo: provide queries like "major/minor version has to change for this transition to be valid?"
}
