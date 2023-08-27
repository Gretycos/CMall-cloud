package com.tsong.cmall.common.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/21 22:19
 */
public class BeanUtil {
    public static Object copyProperties(Object source, Object target, String... ignoreProperties) {
        if (source == null) {
            return target;
        }
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    public static <T> List<T> copyList(List sources, Class<T> clazz) {
        return copyList(sources, clazz, null);
    }

    public static <T> List<T> copyList(List sources, Class<T> clazz, Callback<T> callback) {
        List<T> targetList = new ArrayList<>();
        if (sources != null) {
            try {
                for (Object source : sources) {
                    T target = clazz.getDeclaredConstructor().newInstance();
                    copyProperties(source, target);
                    if (callback != null) {
                        callback.set(source, target);
                    }
                    targetList.add(target);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return targetList;
    }

    public interface Callback<T> {
        void set(Object source, T target);
    }
}
