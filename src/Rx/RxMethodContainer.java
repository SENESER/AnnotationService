package Rx;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class RxMethodContainer {
    public RxParamContainer[] paramsOrder;
    public Method method;
}

class RxParamContainer{
    public String name;
    public RxTypeParser<?> valueParser;
}