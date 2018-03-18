package org.apiguardian.descriptor.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apiguardian.descriptor.model.ApiDescriptor;

import java.io.File;

public class ApiDescriptorIO {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @SneakyThrows
    public String storeAsString(ApiDescriptor descriptor){
        return mapper.writeValueAsString(descriptor);
    }

    @SneakyThrows
    public void store(ApiDescriptor descriptor, File file){
        mapper.writeValue(file, descriptor);
    }

    @SneakyThrows
    public ApiDescriptor loadFromString(String value){
        return mapper.readValue(value, ApiDescriptor.class);
    }

    @SneakyThrows
    public ApiDescriptor load(File file){
        return mapper.readValue(file, ApiDescriptor.class);
    }
}
