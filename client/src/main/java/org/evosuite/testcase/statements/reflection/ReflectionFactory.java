package org.evosuite.testcase.statements.reflection;

import org.evosuite.runtime.Reflection;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.utils.Randomness;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Class used to get private fields/methods to construct statements in the generated tests
 *
 * Created by Andrea Arcuri on 22/02/15.
 */
public class ReflectionFactory {

    private final Class<?> target;
    private final List<Field> fields;
    private final List<Method> methods;


    public ReflectionFactory(Class<?> target) throws IllegalArgumentException{
        this.target = target;
        if(target==null){
            throw new IllegalArgumentException("Target class cannot be null");
        }

        fields = new ArrayList<>();
        methods = new ArrayList<>();

        for(Method m : target.getDeclaredMethods()){
            if(Modifier.isPrivate(m.getModifiers())){
                //only interested in private methods, as the others can be called directly
                methods.add(m);
            }
        }

        for(Field f : target.getDeclaredFields()){
            if(Modifier.isPrivate(f.getModifiers())){
                fields.add(f);
            }
        }
    }

    public boolean hasPrivateFieldsOrMethods(){
        return  !(fields.isEmpty() && methods.isEmpty());
    }

    public boolean nextUseField(){
        if(fields.isEmpty()){
            return false;
        }
        if(methods.isEmpty()){
            assert !fields.isEmpty();
            return true;
        }

        assert !fields.isEmpty() && !methods.isEmpty();

        int tot = fields.size() + methods.size();
        double ratio = (double)fields.size() / (double) tot;

        return Randomness.nextDouble() <= ratio;
    }

    public Field nextField() throws IllegalStateException{
        if(fields.isEmpty()){
            throw new IllegalStateException("No private field");
        }
        return Randomness.choice(fields);
    }

    public Method nextMethod()  throws IllegalStateException{
        if(methods.isEmpty()){
            throw new IllegalStateException("No private method");
        }
        return Randomness.choice(methods);
    }

    public Class<?> getReflectedClass(){
        return target;
    }
}
