package com.zinyakov.ioccontainer.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisteredObject<T> {

    private Class<T> type;
    private LifeCycle lifeCycle;
    private T instance;

}
