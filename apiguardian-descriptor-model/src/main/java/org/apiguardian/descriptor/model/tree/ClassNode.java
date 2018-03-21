package org.apiguardian.descriptor.model.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apiguardian.descriptor.utils.Visibility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor //ugly, but required for serialization
public class ClassNode<ElementDescriptor> {
    @JsonProperty
    private String name;
    @JsonProperty
    private Visibility visibility;
    @JsonProperty
    private List<ClassElementNode<ElementDescriptor>> constructors;
    @JsonProperty
    private List<ClassElementNode<ElementDescriptor>> methods;
    @JsonProperty
    private List<ClassElementNode<ElementDescriptor>> fields;

    public ClassNode(String name, Visibility visibility, List<ClassElementNode<ElementDescriptor>> constructors,
                     List<ClassElementNode<ElementDescriptor>> methods, List<ClassElementNode<ElementDescriptor>> fields) {
        //todo: add null checks, remove lists from parameters
        this.name = name;
        this.visibility = visibility;
        this.constructors = constructors;
        this.methods = methods;
        this.fields = fields;
    }

    private String getDeclaration(Method method){
        return method.getReturnType().getCanonicalName()+" "+
            method.getName()+"("+
                Stream.of(method.getParameterTypes()).map(Object::toString).collect(joining(", "))+
            ")";
    }

    private String getDeclaration(Field field){
        return field.getType().getCanonicalName()+" "+field.getName();
    }

    private String getDeclaration(Constructor constructor){
        return constructor.getDeclaringClass().getSimpleName()+"("+
            Stream.of(constructor.getParameterTypes()).map(Object::toString).collect(joining(", "))+
        ")";
    }


    public ClassElementNode<ElementDescriptor> addMethod(Method method, ElementDescriptor elementDescriptor){
        return addMethod(getDeclaration(method), Visibility.of(method), elementDescriptor);
    }

    public ClassElementNode<ElementDescriptor> addMethod(String declaration, Visibility visibility,
                                                         ElementDescriptor elementDescriptor){
        ClassElementNode<ElementDescriptor> node = new ClassElementNode<>(
            declaration,
            visibility,
            elementDescriptor
        );
        methods = addAndSort(methods, node);
        return node;
    }

    public ClassElementNode<ElementDescriptor> addField(Field field, ElementDescriptor elementDescriptor){
        return addField(getDeclaration(field), Visibility.of(field), elementDescriptor);
    }

    public ClassElementNode<ElementDescriptor> addField(String declaration, Visibility visibility,
                                                        ElementDescriptor elementDescriptor){
        ClassElementNode<ElementDescriptor> node = new ClassElementNode<>(
            declaration,
            visibility,
            elementDescriptor
        );
        fields = addAndSort(fields, node);
        return node;
    }

    public ClassElementNode<ElementDescriptor> addConstructor(Constructor constructor, ElementDescriptor elementDescriptor){
        return addConstructor(getDeclaration(constructor), Visibility.of(constructor), elementDescriptor);
    }

    public ClassElementNode<ElementDescriptor> addConstructor(String declaration, Visibility visibility,
                                                              ElementDescriptor elementDescriptor){
        ClassElementNode<ElementDescriptor> node = new ClassElementNode<>(
            declaration,
            visibility,
            elementDescriptor
        );
        constructors = addAndSort(constructors, node);
        return node;
    }

    //todo extract, reuse in pkg node
    private List<ClassElementNode<ElementDescriptor>> addAndSort(List<ClassElementNode<ElementDescriptor>> list,
                                                                 ClassElementNode<ElementDescriptor> node){
        //fixme a dirty hack that also happens in other tree nodes - we need that for proper comparison;
        // I had some weird issues with equals when I've used Sets instead of Lists, and sorting them makes
        // it possible to predict the order of elements
        list.add(node);
        return list.stream().sorted(Comparator.comparing(n -> n.getDeclaration())).collect(toList());
    }
}
