package com.xiaofan.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);


    public static void copy(Object to, Object from) {
        if ((to == null) || (from == null)) {
            return;
        }
        Map<String, Method> getters = getMethods(from, "get");
        Map<String, Method> setters = getMethods(to, "set");
        for (Map.Entry<String, Method> entry : setters.entrySet()) {
            String fieldName = entry.getKey();
            Method setter = entry.getValue();
            if (getters.containsKey(fieldName)) {
                Method getter = getters.get(fieldName);
                if (setter.getParameterTypes().length == 1 && !getter.getReturnType().equals(void.class)) {
                    try {
                        if (setter.getParameterTypes()[0].getName().equals(getter.getReturnType().getName())) {
                            setter.invoke(to, getters.get(fieldName).invoke(from));
                        }
                    } catch (Exception e) {
                        LOG.error("fieldName:{}, copy error message:{}", fieldName, e.getMessage());
                    }
                }
            }
        }
    }

    public static Map<String, Method> getMethods(Object o, String pattern) {
        Method[] methods = o.getClass().getMethods();
        Map<String, Method> methodHashMap = new ConcurrentHashMap<>();
        for (Method method : methods) {
            if (method.getName().toLowerCase().startsWith(pattern.toLowerCase())) {
                methodHashMap.put(method.getName().substring(3), method);
            }
        }
        return methodHashMap;
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    }
}