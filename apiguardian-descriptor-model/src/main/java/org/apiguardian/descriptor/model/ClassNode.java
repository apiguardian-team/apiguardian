package org.apiguardian.descriptor.model;

import lombok.*;
import org.apiguardian.contract.APIElementState;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@ToString
@EqualsAndHashCode
public class ClassNode {
    String name;
    List<ClassElementNode> constructors;
    List<ClassElementNode> methods;
    List<ClassElementNode> fields;

    ClassNode(String name, List<ClassElementNode> constructors, List<ClassElementNode> methods, List<ClassElementNode> fields) {
        this.name = name;
        this.constructors = constructors;
        this.methods = methods;
        this.fields = fields;
    }

    private String getDeclaration(Method method){
        return method.getReturnType().getCanonicalName()+" "+method.getName()+"("+ Stream.of(method.getParameterTypes()).map(Object::toString).collect(joining(", "))+")";
    }

    private ClassElementNode.Visibility getVisibility(Method method){
        if ((method.getModifiers() & Modifier.PUBLIC) != 0)
            return ClassElementNode.Visibility.PUBLIC;
        if ((method.getModifiers() & Modifier.PROTECTED) != 0)
            return ClassElementNode.Visibility.PROTECTED;
        if ((method.getModifiers() & Modifier.PRIVATE) != 0)
            return ClassElementNode.Visibility.PRIVATE;
        return ClassElementNode.Visibility.PKG;
    }

    public ClassElementNode addMethod(Method method, APIElementState state){
        ClassElementNode node = new ClassElementNode(getDeclaration(method), getVisibility(method), state);
        methods.add(node);
        return node;
    }
}
