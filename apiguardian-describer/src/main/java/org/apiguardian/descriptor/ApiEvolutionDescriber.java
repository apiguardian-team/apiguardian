package org.apiguardian.descriptor;

import lombok.AllArgsConstructor;
import org.apiguardian.contract.ApiElementState;
import org.apiguardian.descriptor.model.ArtifactDescriptor;
import org.apiguardian.descriptor.model.evolution.ApiStateTransition;
import org.apiguardian.descriptor.model.evolution.ArtifactEvolution;
import org.apiguardian.descriptor.model.evolution.VersionChange;
import org.apiguardian.descriptor.model.tree.ClassElementNode;
import org.apiguardian.descriptor.model.tree.ClassNode;
import org.apiguardian.descriptor.visitor.ArtifactDescriptorTraverser;
import org.apiguardian.descriptor.visitor.ElementDefinition;
import org.apiguardian.descriptor.visitor.ElementResolvingUtils;
import org.apiguardian.descriptor.visitor.ElementVisitor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ApiEvolutionDescriber implements Describer<ArtifactEvolution, ArtifactDescriptor<VersionChange, ApiStateTransition>>{
    @Override
    public ArtifactDescriptor<VersionChange, ApiStateTransition> describe(ArtifactEvolution artifactEvolution) {
        VersionChange versionChange = new VersionChange(
            artifactEvolution.getPreviousDescriptor().getVersionDescriptor(),
            artifactEvolution.getNextDescriptor().getVersionDescriptor()
        );
        ArtifactDescriptor<VersionChange, ApiStateTransition> resultDescriptor = new ArtifactDescriptor<>(versionChange);
        VisitedElements visitedElements = new VisitedElements();
        // the "meat" of this method is in VisistorImpl, see comments there

        // first we compare previous version with next one, to find state transitions and see if an elements have
        // disappeared, then we compare the other way around to find new elements
        // we keep track of visited elements and skip them, so we won't duplicate them when comparing next version
        // to previous one
        new ArtifactDescriptorTraverser<ApiElementState>().traverse(
            artifactEvolution.getPreviousDescriptor(),
            new VisistorImpl(
                resultDescriptor,
                visitedElements,
                artifactEvolution.getNextDescriptor(),
                (s1, s2) -> new ApiStateTransition(s1, s2)
            )
        );
        new ArtifactDescriptorTraverser<ApiElementState>().traverse(
            artifactEvolution.getNextDescriptor(),
            new VisistorImpl(
                resultDescriptor,
                visitedElements,
                artifactEvolution.getPreviousDescriptor(),
                (s1, s2) -> new ApiStateTransition(s2, s1)
            )
        );
        return resultDescriptor;
    }

    @FunctionalInterface
    private interface TransitionBuilder {
        ApiStateTransition build(ApiElementState state1, ApiElementState state2);
    }

    /**
     * Internal visitor implementation that traverses one state descriptor, compares it with the other one and registers
     * element in result (transition) descriptor.
     */
    @AllArgsConstructor
    private static class VisistorImpl implements ElementVisitor<ApiElementState> {
        private ArtifactDescriptor<VersionChange, ApiStateTransition> resultDescriptor;
        private VisitedElements visitedElements;
        private ArtifactDescriptor<String, ApiElementState> otherDescriptor;
        private TransitionBuilder transitionBuilder;

        @FunctionalInterface
        private interface VisitedElementsProvider {
            Set<ElementDefinition> provideVisistedElements();
        }
        @FunctionalInterface
        private interface ElementResolver {
            Optional<ClassElementNode<ApiElementState>> resolve(ArtifactDescriptor<String, ApiElementState> descriptor,
                                                                 ElementDefinition definition);
        }
        @FunctionalInterface
        private interface ElementRegistrator {
            void register(ClassNode<ApiStateTransition> clazz, ElementDefinition definition, ApiStateTransition transition);
        }

        /**
         * Generic implementation of visit... methods. See comment below for details.
         */
        private void visit(ElementDefinition definition, ApiElementState apiElementState,
                           VisitedElementsProvider visitedElementsProvider,
                           ElementResolver elementResolver,
                           ElementRegistrator registrator){
            // Examples are given for visiting previous versions constructors.
            // When we traverse next version, we build transition in reverse order and use previous version as otherDescriptor.
            // When we visit methods and fields, we use appropriate visistedElements fields, we resolve them with different
            // ElementResolvingUtils methods and we register them with different ClassNode methods;
            // besides that the code would be terribly duplicated.

            // e.g. visitedElements.constructors
            Set<ElementDefinition> visited = visitedElementsProvider.provideVisistedElements();
            if (!visited.contains(definition)){
                visited.add(definition);
                // e.g. resolveConstructor(otherDescriptor, definition)
                Optional<ClassElementNode<ApiElementState>> next = elementResolver.resolve(otherDescriptor, definition);
                ApiElementState otherState;
                if (next.isPresent()){
                    otherState = next.get().getDescriptor();
                } else {
                    otherState = ApiElementState.NONE;
                }
                // e.g. ApiStateTransition.of(apiElementState, otherState)
                ApiStateTransition transition = transitionBuilder.build(apiElementState, otherState);
                // e.g. ...getClass(...).addConstructor(definition.declaration, definition.elementVisibility, transition)
                registrator.register(
                    resultDescriptor.
                        getRootPackage().
                        resolvePackage(definition.getPackageName()).
                        getClass(definition.getClassName(), definition.getClassVisibility()),
                    definition,
                    transition
                );
            }
        }

        @Override
        public void visitConstructor(ElementDefinition definition, ApiElementState apiElementState) {
            visit(
                definition, apiElementState,
                () -> visitedElements.constructors,
                ElementResolvingUtils::resolveConstructor,
                (c, d, t) -> c.addConstructor(d.getDeclaration(), d.getElementVisibility(), t)
            );
        }

        @Override
        public void visitMethod(ElementDefinition definition, ApiElementState apiElementState) {
            visit(
                definition, apiElementState,
                () -> visitedElements.methods,
                ElementResolvingUtils::resolveMethod,
                (c, d, t) -> c.addMethod(d.getDeclaration(), d.getElementVisibility(), t)
            );
        }

        @Override
        public void visitField(ElementDefinition definition, ApiElementState apiElementState) {
            visit(
                definition, apiElementState,
                () -> visitedElements.fields,
                ElementResolvingUtils::resolveField,
                (c, d, t) -> c.addField(d.getDeclaration(), d.getElementVisibility(), t)
            );
        }
    }

    /**
     * Helper class to keep track of already visited API elements, so that we won't duplicate entries.
     */
    private static class VisitedElements {
        Set<ElementDefinition> constructors = new HashSet<>();
        Set<ElementDefinition> methods = new HashSet<>();
        Set<ElementDefinition> fields = new HashSet<>();
    }
}
