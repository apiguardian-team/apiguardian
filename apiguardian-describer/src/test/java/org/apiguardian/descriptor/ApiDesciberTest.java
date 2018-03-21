package org.apiguardian.descriptor;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apiguardian.contract.ApiElementState;
import org.apiguardian.descriptor.model.ArtifactDescriptor;
import org.apiguardian.descriptor.model.VersionedArtifact;
import org.apiguardian.descriptor.persistence.IO;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ApiDesciberTest {
    private URL jarUrl;
    private ArtifactDescriptor<String, ApiElementState> expectedDescriptor;
    private static final String CONSTANT_VERSION_STRING = "0.0.1-SNAPSHOT";

    @Before
    @SneakyThrows
    public void setUp(){
//        @Cleanup BufferedReader r = new BufferedReader(
//            new InputStreamReader(
//                this.getClass().getResourceAsStream("/example_path.txt")
//            )
//        );
//        String path = r.readLine();
        //fixme: ugly, but hopefully will work with Travis CI
        String path = "../apiguardian-descriptor-example/build/libs/apiguardian-descriptor-example-1.1.0-SNAPSHOT.jar";
        jarUrl = new File(path).toURI().toURL();
        IO.DescriptorIO io = IO.forApiDescriptor();
        @Cleanup InputStream expectedDescriptorStream = this.getClass().getResourceAsStream("/expected_example_descriptor.json");
        expectedDescriptor = io.load(expectedDescriptorStream);
    }

    @Test
    @SneakyThrows
    public void exampleArtifactShouldBeProperlyDescribed(){
        ArtifactDescriptor descriptor = new ApiDesciber().describe(VersionedArtifact.of(CONSTANT_VERSION_STRING, jarUrl));
        assertEquals(expectedDescriptor, descriptor);
    }
}