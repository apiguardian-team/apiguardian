package org.apiguardian.descriptor;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apiguardian.contract.ApiElementState;
import org.apiguardian.descriptor.model.ArtifactDescriptor;
import org.apiguardian.descriptor.model.evolution.ApiStateTransition;
import org.apiguardian.descriptor.model.evolution.ArtifactEvolution;
import org.apiguardian.descriptor.model.evolution.VersionChange;
import org.apiguardian.descriptor.persistence.IO;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class ApiEvolutionDescriberTest {
    private ArtifactDescriptor<String, ApiElementState> previousDescriptor;
    private ArtifactDescriptor<String, ApiElementState> evolvedDescriptor;
    private ArtifactDescriptor<VersionChange, ApiStateTransition> expectedEvolutionDescriptor;

    @Before
    @SneakyThrows
    public void setUp(){
        IO.DescriptorIO apiIo = IO.forApiDescriptor();
        @Cleanup InputStream expectedDescriptorStream = this.getClass().getResourceAsStream("/expected_example_descriptor.json");
        @Cleanup InputStream evolvedDescriptorStream = this.getClass().getResourceAsStream("/evolved_example_descriptor.json");
        previousDescriptor = apiIo.load(expectedDescriptorStream);
        evolvedDescriptor = apiIo.load(evolvedDescriptorStream);
        IO.DescriptorIO evolutionIo = IO.forApiEvolution();
        @Cleanup InputStream expectedEvolutionDescriptorStream = this.getClass().getResourceAsStream("/expected_evolution_descriptor.json");
        expectedEvolutionDescriptor = evolutionIo.load(expectedEvolutionDescriptorStream);
    }

    @Test
    public void evolutionShouldBeProperlyDescribed(){
        ArtifactDescriptor<VersionChange, ApiStateTransition> descriptor = new ApiEvolutionDescriber().describe(
            ArtifactEvolution.between(previousDescriptor, evolvedDescriptor)
        );
        assertEquals(expectedEvolutionDescriptor, descriptor);
    }

}