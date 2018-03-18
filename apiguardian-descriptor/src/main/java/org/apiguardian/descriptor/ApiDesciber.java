package org.apiguardian.descriptor;

import com.google.common.reflect.Reflection;
import lombok.SneakyThrows;
import org.apiguardian.api.API;
import org.apiguardian.contract.APIElementState;
import org.apiguardian.descriptor.model.ApiDescriptor;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

public class ApiDesciber {
    private APIElementState map(API.Status status){
        return APIElementState.valueOf(status.toString());
    }

    @SneakyThrows
    public ApiDescriptor describe(File artifact){
        Reflections reflections = new Reflections(
            ConfigurationBuilder.build(
                artifact.toURI().toURL(),
                new MethodAnnotationsScanner(),
                new FieldAnnotationsScanner(),
                new TypeAnnotationsScanner()
            )
        );
        ApiDescriptor descriptor = new ApiDescriptor();
        for (Method method: reflections.getMethodsAnnotatedWith(API.class))
            descriptor.registerMethod(method, map(method.getAnnotation(API.class).status()));
        return descriptor;
    }
}
