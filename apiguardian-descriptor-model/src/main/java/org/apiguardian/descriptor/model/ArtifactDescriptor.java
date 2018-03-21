package org.apiguardian.descriptor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apiguardian.descriptor.model.tree.ClassNode;
import org.apiguardian.descriptor.model.tree.PackageNode;
import org.apiguardian.descriptor.utils.Visibility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
public class ArtifactDescriptor<VersionDescriptor, ElementDescriptor> {
    @JsonProperty
    private VersionDescriptor versionDescriptor;
    @JsonProperty
    private List<PackageNode<ElementDescriptor>> library;

    //ugly, but required for serialization
    public ArtifactDescriptor() {
    }

    public ArtifactDescriptor(VersionDescriptor versionDescriptor) {
        if (versionDescriptor == null)
            throw new NullPointerException("Version descriptor cannot be null!");
        this.versionDescriptor = versionDescriptor;
        this.library = new LinkedList<>();
    }

    @JsonIgnore
    public PackageNode<ElementDescriptor> getRootPackage(){
        return new PackageNode<>("", library, new LinkedList<>());
    }

    public void registerMethod(Method method, ElementDescriptor elementDescriptor){
        getClass(method.getDeclaringClass()).addMethod(method, elementDescriptor);
    }

    public void registerField(Field field, ElementDescriptor elementDescriptor){
        getClass(field.getDeclaringClass()).addField(field, elementDescriptor);
    }

    public void registerConstructor(Constructor constructor, ElementDescriptor elementDescriptor){
        getClass(constructor.getDeclaringClass()).addConstructor(constructor, elementDescriptor);
    }

    public void registerClass(Class clazz, ElementDescriptor elementDescriptor){
        for (Constructor constructor: clazz.getDeclaredConstructors())
            if (Visibility.of(constructor) == Visibility.PUBLIC)
                registerConstructor(constructor, elementDescriptor);
        for (Method method: clazz.getDeclaredMethods())
            if (Visibility.of(method) == Visibility.PUBLIC)
                registerMethod(method, elementDescriptor);
        for (Field field: clazz.getDeclaredFields())
            if (Visibility.of(field) == Visibility.PUBLIC)
                registerField(field, elementDescriptor);
    }

    private PackageNode<ElementDescriptor> resolvePackage(String name){
        return getRootPackage().resolvePackage(name);
    }

    private ClassNode<ElementDescriptor> getClass(Class<?> clazz){
        PackageNode<ElementDescriptor> pkg = resolvePackage(clazz.getPackage().getName());
        ClassNode<ElementDescriptor> classNode = pkg.getClass(clazz.getSimpleName(), Visibility.of(clazz));
        return classNode;
    }
}
