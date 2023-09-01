package com.xiaofan.utils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectUtil {
    private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    /**
     * 根据map中参数，修改传递过来对应实体属性列的值
     * update时前端传递对应实体属性的map，在转换的时候会根据map中的键替换传入t实体的属性
     * 可达到只更新实体中对应map键属性的效果
     */
    public static <T> List<T> getEntityByList(Class<T> clazz, List<Map<String, Object>> mapList) {
        List<T> resultList = Lists.newArrayList();
        if (mapList == null || mapList.isEmpty()) {
            return resultList;
        }
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Map map : mapList) {
                T t = clazz.getDeclaredConstructor().newInstance();
                for (Field field : fields) {
                    String name = field.getName();
                    if (map.get(name) != null) {
                        field.set(t, map.get(name));
                    } else {
                        if ("java.lang.String".equals(field.getType().getName())) {
                            field.set(t, "");
                        } else if ("java.lang.Integer".equals(field.getType().getName())) {
                            field.set(t, 0);
                        } else if ("java.lang.Long".equals(field.getType().getName())) {
                            field.set(t, 0L);
                        }
                    }
                }
                resultList.add(t);
            }
        } catch (Exception e) {
            logger.error("getEntityByList error message:+" + e.getMessage());
        }
        return resultList;
    }


    /**
     * @param
     * @param obj
     * @return key 小写下划线
     * @return java.util.HashMap<java.lang.String, java.lang.Object>
     * @author twan
     * @date 11:41 2023/8/2
     **/
    public static HashMap<String, Object> getAllFieldVales(Object obj) {
        HashMap<String, Object> hashMap = new HashMap<>();
        Field[] fields = getAllFields(obj.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                hashMap.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()), field.get(obj));
            } catch (IllegalAccessException e) {
                logger.error("class :{} getAllFieldVales error:{}", obj, e.getMessage());
            }
        }
        return hashMap;
    }

    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

    public static Method[] getAllMethods(Class<?> clazz) {
        List<Method> methodList = new ArrayList<>();
        while (clazz != null) {
            methodList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods())));
            clazz = clazz.getSuperclass();
        }
        Method[] methods = new Method[methodList.size()];
        return methodList.toArray(methods);
    }


    private static Object convertValue(Object value, Class<?> targetType) {
        if (targetType == String.class) {
            return value.toString();
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value.toString());
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value.toString());
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(value.toString());
        }
        return value;
    }

    public static <T> T setFieldsValue(Class<T> cls, Map<String, Object> valMap) throws Exception {
        T instance = cls.getDeclaredConstructor().newInstance();
        Field[] fields = getAllFields(cls);
        Method[] methods = getAllMethods(cls);
        String fieldName = "";
        for (Field field : fields) {
            try {
                fieldName = field.getName();
                String fieldSetName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                List<Method> setMethodList = Arrays.stream(methods).filter(t -> t.getName().equals(fieldSetName)).collect(Collectors.toList());
                if (setMethodList.isEmpty()) {
                    System.err.println("不存在该方法：" + fieldSetName);
                    continue;
                }
                String fieldKeyName = field.getName();
                Object fieldValue = valMap.get(fieldKeyName);
                if (fieldValue != null) {
                    Method setterMethod = setMethodList.get(0);
                    Class<?> parameterType = setterMethod.getParameterTypes()[0];
                    setterMethod.setAccessible(true);
                    setterMethod.invoke(instance, convertValue(fieldValue, parameterType));
                }
            } catch (Exception e) {
                System.err.println("setFieldsValue error field: " + fieldName + " message: " + e);
            }
        }
        return instance;
    }

    public static Method getMethod(Class<?> clazz, String methodName) {
        List<Method> methods = Arrays.stream(getAllMethods(clazz)).filter(t -> t.getName().equals(methodName)).collect(Collectors.toList());
        return methods.size() > 0 ? methods.get(0) : null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, List<Object> paramTypeList) {
        List<Method> methods = Arrays.stream(getAllMethods(clazz)).filter(t -> t.getName().equals(methodName)).collect(Collectors.toList());
        return methods.stream().map(t -> {
            Method method = null;
            Class<?>[] parameterTypes = t.getParameterTypes();
            if (parameterTypes.length == paramTypeList.size()) {
                boolean flag = Arrays.stream(parameterTypes).allMatch(t1 -> paramTypeList.contains(t1));
                if (flag) {
                    method = t;
                }
            }
            return method;
        }).filter(Objects::nonNull).collect(Collectors.toList()).get(0);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        ArrayList<Object> list = Lists.newArrayList();
        ReflectUtil.getMethod(Class.forName("com.doudian.open.api.order_getSettleBillDetailV3.OrderGetSettleBillDetailV3Request"), "execute", list);

    }
}
