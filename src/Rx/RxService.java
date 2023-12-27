package Rx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RxService<T> {

    public static Pattern DividerPattern = Pattern.compile("^\\s*([^:]+?)\\s*:([\\s\\S]*)$");
    public static Pattern ArgPattern = Pattern.compile("^\\s*([^=]*?)\\s*=\\s*([\\s\\S]+?)\\s*$");

    public static HashMap<Class<?>, RxTypeParser<?>> DefaultParsers = new HashMap<>();
    static {
        DefaultParsers.put(int.class, Integer::parseInt);
        DefaultParsers.put(String.class, (v) -> v);
        DefaultParsers.put(float.class, Float::parseFloat);
    }

    HashMap<String, RxMethodContainer> methods = new HashMap<>();

    public RxService(Class<T> cls) throws RxAnnotationExpectedException{
        for (var method : cls.getMethods()) {
            var methodAnnotation = method.getAnnotation(RxMethod.class);

            if (methodAnnotation != null){
                var res = new RxMethodContainer();
                res.method = method;

                Parameter[] parameters = method.getParameters();
                res.paramsOrder = new RxParamContainer[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    var parameterAnnotation = parameter.getAnnotation(RxParam.class);
                    if (parameterAnnotation == null) {
                        throw new RxAnnotationExpectedException();
                    }
                    var paramContainer = new RxParamContainer();
                    paramContainer.name = parameterAnnotation.name();

                    if (DefaultParsers.containsKey(parameter.getType()))
                        paramContainer.valueParser = DefaultParsers.get(parameter.getType());
                    else
                        throw new RxAnnotationExpectedException();

                    res.paramsOrder[i] = paramContainer;
                }

                methods.put(methodAnnotation.name(), res);
            }
        }
    }

    public Object Call(Object obj, String name, HashMap<String, String> namedArgs) throws InvocationTargetException, IllegalAccessException {
        var container = methods.get(name);
        Object[] args = new Object[container.paramsOrder.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = container.paramsOrder[i].valueParser.Convert(namedArgs.get(container.paramsOrder[i].name));
        }
        return container.method.invoke(obj, args);
    }

    public Object Call(Object obj, String strRepresentation) throws RxException, InvocationTargetException, IllegalAccessException {
        var globalMatcher = RxService.DividerPattern.matcher(strRepresentation);
        if (!globalMatcher.find()) {throw new RxException();}
        var methodName = globalMatcher.group(1);
        var args = new HashMap<String, String>();
        {

            var argStrs = globalMatcher.group(2).split(",");

            for (String argStr : argStrs) {
                var argMatcher = RxService.ArgPattern.matcher(argStr);
                if (!argMatcher.find()) {throw new RxException();}
                var argName = argMatcher.group(1);
                var argValue = argMatcher.group(2);

                args.put(argName, argValue);
            }
        }

        return Call(obj, methodName, args);
    }
}
