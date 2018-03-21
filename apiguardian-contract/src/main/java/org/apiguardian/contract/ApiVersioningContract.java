package org.apiguardian.contract;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.apiguardian.contract.ApiElementState.*;
import static org.apiguardian.contract.StateTransitionRule.*;

/**
 * Class containing static methods for analysing API state graph.
 * Internally holds static graph description and is able to query it to answer questions on transition validity
 * or possible transitions.
 */
public class ApiVersioningContract {
    private ApiVersioningContract() {
    }

    private static final Collection<ApiElementState> ALL_STATES = asList(ApiElementState.values());

    public static List<StateTransitionRule> rules = asList(
        onMajorVersionIncrement(ALL_STATES, ALL_STATES),
        anytime(asList(INTERNAL, EXPERIMENTAL), NONE),
        onMinorVersionIncrement(DEPRECATED, NONE),
        anytime(EXPERIMENTAL, asList(DEPRECATED, MAINTAINED, STABLE)),
        onMinorVersionIncrement(MAINTAINED, DEPRECATED),
        anytime(MAINTAINED, STABLE),
        anytime(NONE, ALL_STATES)
    );

    /**
     * Is given transition valid?
     * @param previousState State of the feature in previous analysed version
     * @param nextState State of the feature in next analysed version
     * @param versionComponentChange Most general change in version components
     * @see VersionComponentChange
     * @return true if transition of feature state is valid, false in other case
     */
    public static boolean isValidTransition(ApiElementState previousState, ApiElementState nextState,
                                            VersionComponentChange versionComponentChange){
        return (previousState == nextState) || rules.stream().
            map(rule ->
                rule.isSatisfied(previousState, nextState, versionComponentChange)
            ).filter(x -> x).
            findAny().
            isPresent();
    }

    /**
     * What state can a feature have in next version that differs from previous version by given component change?
     * @param previousState State of the feature in previous analysed version
     * @param versionComponentChange Most general change in version components
     * @see VersionComponentChange
     * @return Set of valid states in which a feature can be in next version
     */
    public static Set<ApiElementState> findValidNextStates(ApiElementState previousState,
                                                           VersionComponentChange versionComponentChange){
        return Stream.of(ApiElementState.values()).
            filter(state ->
                isValidTransition(previousState, state, versionComponentChange)
            ).
            collect(Collectors.toSet());
    }

    /**
     * What state could a feature be in previously, if it is in given state in next version that differs from previous
     * one by given component change?
     * @param nextState State of the feature in currently analysed version
     * @param versionComponentChange Most general change in version components
     * @see VersionComponentChange
     * @return Set of valid states in which a feature could be in previous version
     */
    public static Set<ApiElementState> findValidPreviousStates(ApiElementState nextState,
                                                               VersionComponentChange versionComponentChange){
        return Stream.of(ApiElementState.values()).
            filter(state ->
                isValidTransition(state, nextState, versionComponentChange)
            ).
            collect(Collectors.toSet());
    }

    /**
     * What is the most specific version change so that transition between feature states would be valid?
     * @param previousState State of the feature in previous analysed version
     * @param nextState State of the feature in next analysed version
     * @return Most specific change in version components for a transition to be valid
     */
    public static VersionComponentChange findMostSpecificChangeForValidTransition(ApiElementState previousState,
                                                                                  ApiElementState nextState){
        return Stream.of(VersionComponentChange.values()).filter(change ->
            isValidTransition(previousState, nextState, change)
        ).sorted(
            Comparator.<VersionComponentChange>naturalOrder().reversed() //sort from most specific to most general
        ).findFirst().get();
    }
}
