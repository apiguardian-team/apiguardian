package org.apiguardian.descriptor.validators.evolution;

import com.vdurmont.semver4j.Semver;
import lombok.Getter;
import org.apiguardian.contract.ApiVersioningContract;
import org.apiguardian.contract.VersionComponentChange;
import org.apiguardian.descriptor.model.ArtifactDescriptor;
import org.apiguardian.descriptor.model.evolution.ApiStateTransition;
import org.apiguardian.descriptor.model.evolution.VersionChange;
import org.apiguardian.descriptor.validation.Validator;
import org.apiguardian.descriptor.validation.Violation;
import org.apiguardian.descriptor.visitor.ArtifactDescriptorTraverser;
import org.apiguardian.descriptor.visitor.ElementDefinition;
import org.apiguardian.descriptor.visitor.UniformElementVisitor;

import java.util.HashSet;
import java.util.Set;

public class ApiTransitionValidator implements Validator<ArtifactDescriptor<VersionChange, ApiStateTransition>> {
    @Override
    public Set<Violation<ArtifactDescriptor<VersionChange, ApiStateTransition>>> validate(ArtifactDescriptor<VersionChange, ApiStateTransition> target) {
        VisitorImpl visitor = new VisitorImpl(target);
        new ArtifactDescriptorTraverser<ApiStateTransition>().traverse(target, visitor);
        return visitor.getViolations();
    }

    private static class VisitorImpl implements UniformElementVisitor<ApiStateTransition> {
        private ArtifactDescriptor<VersionChange, ApiStateTransition> traversed;
        @Getter private Set<Violation<ArtifactDescriptor<VersionChange, ApiStateTransition>>> violations = new HashSet<>();

        public VisitorImpl(ArtifactDescriptor<VersionChange, ApiStateTransition> traversed) {
            this.traversed = traversed;
        }

        @Override
        public void visit(ElementType type, ElementDefinition definition, ApiStateTransition transition) {
            if (!isValid(transition)) {
                violations.add(buildViolation(type, definition, transition));
            }
        }

        private Violation<ArtifactDescriptor<VersionChange, ApiStateTransition>> buildViolation(ElementType type,
                                                    ElementDefinition elementDefinition, ApiStateTransition transition){
            return Violation.of(
                traversed,
                "Invalid API transition from "+transition.getPreviousState()+
                    " to "+transition.getNextState()+" for "+
                    type.toString().toLowerCase()+" "+elementDefinition
            );
        }

        private boolean isValid(ApiStateTransition transition){
            return ApiVersioningContract.isValidTransition(
                transition.getPreviousState(), transition.getNextState(),
                getVersionComponentChange(traversed.getVersionDescriptor())
            );
        }
    }

    private static VersionComponentChange getVersionComponentChange(VersionChange versionChange){
        Semver previous = new Semver(versionChange.getPreviousVersion());
        Semver next = new Semver(versionChange.getNextVersion());
        Semver.VersionDiff diff = next.diff(previous);
        return map(diff);
    }

    private static VersionComponentChange map(Semver.VersionDiff diff){
        switch (diff){
            case MAJOR: return VersionComponentChange.MAJOR;
            case MINOR: return VersionComponentChange.MINOR;
            case PATCH: return VersionComponentChange.PATCH;
            default: return VersionComponentChange.NONE;
        }
    }
}
