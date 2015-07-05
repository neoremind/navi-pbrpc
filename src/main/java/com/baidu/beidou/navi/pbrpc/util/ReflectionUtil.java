package com.baidu.beidou.navi.pbrpc.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: ReflectionUtil <br/>
 * Function: 反射工具类
 * 
 * @author Zhang Xu
 */
public class ReflectionUtil {

    /**
     * 获取某个类的所有实例方法
     * 
     * @param clazz
     * @return
     */
    public static Method[] getAllInstanceMethods(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        List<Method> methods = new ArrayList<Method>();
        for (Class<?> itr = clazz; hasSuperClass(itr);) {
            for (Method method : itr.getDeclaredMethods()) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    methods.add(method);
                }
            }
            itr = itr.getSuperclass();
        }

        return methods.toArray(new Method[methods.size()]);

    }

    /**
     * 判断某个类是否含有父类或者接口
     * 
     * @param clazz
     * @return
     */
    public static boolean hasSuperClass(Class<?> clazz) {
        return (clazz != null) && !clazz.equals(Object.class);
    }

    /**
     * 判断某个类是否是void类型
     * 
     * @param cls
     * @return
     */
    public static boolean isVoid(Class<?> cls) {
        if (cls == void.class) {
            return true;
        }
        return false;
    }

}
