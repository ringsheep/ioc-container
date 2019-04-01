package com.zinyakov.ioccontainer.models;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Container {

    private List<RegisteredObject> registeredObjects = new ArrayList<>();

    public <T> void register(Class<T> type) {
        register(type, LifeCycle.TRANSIENT);
    }

    public <T> void register(Class<T> type, LifeCycle lifeCycle) {
        registeredObjects.add(RegisteredObject.<T>builder()
                .lifeCycle(lifeCycle)
                .type(type)
                .build()
        );
    }

    @SneakyThrows
    public <T> T resolve(Class<T> type) {
        Optional<RegisteredObject> resolvedObject = registeredObjects.stream()
                .filter(registeredObject -> type.equals(registeredObject.getType()))
                .findFirst();

        if (!resolvedObject.isPresent()) {
            throw new IllegalArgumentException(String.format("%s type was not registered :(", type));
        }

        RegisteredObject object = resolvedObject.get();
        if (object.getInstance() == null || object.getLifeCycle() == LifeCycle.TRANSIENT) {
            T instance = createInstance(type);
            object.setInstance(instance);
        }

        return (T)object.getInstance();
    }

    @SneakyThrows
    private <T> T createInstance(Class<T> type) {
        if (type.getDeclaredConstructors().length == 0) {
            throw new IllegalArgumentException(String.format("%s type has no available constructors :(", type));
        }

        Constructor<?> constructor = type.getDeclaredConstructors()[0];
        Class[] parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0) {
            return (T)constructor.newInstance();
        }

        List<Object> parameters = new ArrayList<>();
        for (Class parameterType : parameterTypes) {
            parameters.add(resolve(parameterType));
        }

        return (T)constructor.newInstance(parameters.toArray());
    }
}
