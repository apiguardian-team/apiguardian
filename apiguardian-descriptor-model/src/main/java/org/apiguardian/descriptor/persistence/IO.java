package org.apiguardian.descriptor.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apiguardian.contract.ApiElementState;
import org.apiguardian.descriptor.model.ArtifactDescriptor;
import org.apiguardian.descriptor.model.evolution.ApiStateTransition;
import org.apiguardian.descriptor.model.evolution.VersionChange;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class IO {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference apiDescriptorType = new TypeReference<ArtifactDescriptor<String, ApiElementState>>() {};
    private static final TypeReference evolutionDescriptorType = new TypeReference<ArtifactDescriptor<VersionChange, ApiStateTransition>>() {};

    private IO(){}

    public static DescriptorIO forApiDescriptor(){
        return new DescriptorIO(apiDescriptorType);
    }

    public static DescriptorIO forApiEvolution(){
        return new DescriptorIO(evolutionDescriptorType);
    }

    //todo: ugly; requires IO.DescriptorIO type or static import
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static public class DescriptorIO {
        private TypeReference descriptorType;

        @SneakyThrows
        public String storeAsString(ArtifactDescriptor descriptor){
            return mapper.
                writerWithDefaultPrettyPrinter().
                without(SerializationFeature.FAIL_ON_EMPTY_BEANS).
                writeValueAsString(descriptor);
        }

        @SneakyThrows
        public void store(ArtifactDescriptor descriptor, File file){
            mapper.
                writerWithDefaultPrettyPrinter().
                without(SerializationFeature.FAIL_ON_EMPTY_BEANS).
                writeValue(file, descriptor);
        }

        @SneakyThrows
        public void store(ArtifactDescriptor descriptor, OutputStream outputStream){
            mapper.
                writerWithDefaultPrettyPrinter().
                without(SerializationFeature.FAIL_ON_EMPTY_BEANS).
                writeValue(outputStream, descriptor);
        }

        @SneakyThrows
        public ArtifactDescriptor loadFromString(String value){
            return mapper.readValue(value, descriptorType);
        }

        @SneakyThrows
        public ArtifactDescriptor load(File file){
            return mapper.readValue(file, descriptorType);
        }

        @SneakyThrows
        public ArtifactDescriptor load(InputStream inputStream){
            return mapper.readValue(inputStream, descriptorType);
        }
    }
}
