package org.apiguardian.descriptor;

import lombok.SneakyThrows;
import org.apiguardian.api.API;
import org.apiguardian.contract.ApiElementState;
import org.apiguardian.descriptor.model.ArtifactDescriptor;
import org.apiguardian.descriptor.model.VersionedArtifact;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ApiDesciber implements Describer<VersionedArtifact, ArtifactDescriptor<String, ApiElementState>>{
    private ApiElementState map(API.Status status){
        return ApiElementState.valueOf(status.toString());
    }

    @SneakyThrows
    public ArtifactDescriptor<String, ApiElementState> describe(VersionedArtifact artifact){
        Reflections reflections = new Reflections(
            ConfigurationBuilder.build(
                artifact.getLocation(),
                new MethodAnnotationsScanner(),
                new FieldAnnotationsScanner(),
                new TypeAnnotationsScanner(),
                new SubTypesScanner()
            )
        );
        ArtifactDescriptor<String, ApiElementState> descriptor = new ArtifactDescriptor<>(artifact.getVersion());
        for (Constructor constructor: reflections.getConstructorsAnnotatedWith(API.class))
            descriptor.registerConstructor(constructor, map(((API)constructor.getAnnotation(API.class)).status()));
        for (Method method: reflections.getMethodsAnnotatedWith(API.class))
            descriptor.registerMethod(method, map(method.getAnnotation(API.class).status()));
        for (Field field: reflections.getFieldsAnnotatedWith(API.class))
            descriptor.registerField(field, map(field.getAnnotation(API.class).status()));
        for (Class clazz: reflections.getTypesAnnotatedWith(API.class)){
            ApiElementState state = map(((API)clazz.getAnnotation(API.class)).status());
            descriptor.registerClass(clazz, state);
        }
        return descriptor;
    }
}
