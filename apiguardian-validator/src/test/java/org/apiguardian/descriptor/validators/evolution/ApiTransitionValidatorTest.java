package org.apiguardian.descriptor.validators.evolution;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apiguardian.descriptor.model.ArtifactDescriptor;
import org.apiguardian.descriptor.model.evolution.ApiStateTransition;
import org.apiguardian.descriptor.model.evolution.VersionChange;
import org.apiguardian.descriptor.persistence.IO;
import org.apiguardian.descriptor.validation.Violation;
import org.apiguardian.descriptor.visitor.ElementDefinition;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static org.apiguardian.descriptor.utils.Visibility.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApiTransitionValidatorTest {
    private ArtifactDescriptor<VersionChange, ApiStateTransition> validEvolutionDescriptor;
    private ArtifactDescriptor<VersionChange, ApiStateTransition> invalidEvolutionDescriptor;

    @Before
    @SneakyThrows
    public void setUp(){
        IO.DescriptorIO evolutionIo = IO.forApiEvolution();
        @Cleanup InputStream validEvolutionDescriptorStream = this.getClass().getResourceAsStream("/valid_evolution_descriptor.json");
        validEvolutionDescriptor = evolutionIo.load(validEvolutionDescriptorStream);
        @Cleanup InputStream invalidEvolutionDescriptorStream = this.getClass().getResourceAsStream("/invalid_evolution_descriptor.json");
        invalidEvolutionDescriptor = evolutionIo.load(invalidEvolutionDescriptorStream);
    }

    @Test
    public void validationShouldNotFailForValidEvolutionDescriptor(){
        assertTrue(new ApiTransitionValidator().validate(validEvolutionDescriptor).isEmpty());
    }

    @Test
    public void validationShouldFailForInvalidEvolutionDescriptor(){
        Set<Violation<ArtifactDescriptor<VersionChange, ApiStateTransition>>> violations = new ApiTransitionValidator().
            validate(invalidEvolutionDescriptor);
        Set<Violation<ArtifactDescriptor<VersionChange, ApiStateTransition>>> expectedViolations = new HashSet<>();
        expectedViolations.add(
            Violation.of(
                invalidEvolutionDescriptor,
                "Invalid API transition from STABLE to MAINTAINED for method " +
                    ElementDefinition.builder().
                        packageName("some.pkg").
                        className("A").
                        declaration("java.lang.String foo()").
                        classVisibility(PUBLIC).
                        elementVisibility(PUBLIC).
                        build()
            )
        );
        expectedViolations.add(
            Violation.of(
                invalidEvolutionDescriptor,
                "Invalid API transition from STABLE to NONE for constructor " +
                    ElementDefinition.builder().
                        packageName("some.pkg").
                        className("A").
                        declaration("A()").
                        classVisibility(PUBLIC).
                        elementVisibility(PUBLIC).
                        build()
            )
        );
        assertEquals(expectedViolations, violations);
    }
}