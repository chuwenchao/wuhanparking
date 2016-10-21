package com.mgear.wuhanparking.common.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtil {
	/**
     * 获取obj对象fieldName的Field
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Field getFieldByFieldName(Object obj, String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            }
        }
        return null;
    }

    /**
     * 获取obj对象fieldName的属性值
     *
     * @param obj
     * @param fieldName
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object getValueByFieldName(Object obj,
                                             String fieldName) throws SecurityException,
                                                                      NoSuchFieldException,
                                                                      IllegalArgumentException,
                                                                      IllegalAccessException {
        Field field = getFieldByFieldName(obj, fieldName);
        Object value = null;
        if (field != null) {
            if (field.isAccessible()) {
                value = field.get(obj);
            } else {
                field.setAccessible(true);
                value = field.get(obj);
                field.setAccessible(false);
            }
        }
        return value;
    }

    /**
     * 设置obj对象fieldName的属性值
     *
     * @param obj
     * @param fieldName
     * @param value
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setValueByFieldName(Object obj, String fieldName,
                                           Object value) throws SecurityException,
                                                                NoSuchFieldException,
                                                                IllegalArgumentException,
                                                                IllegalAccessException {
        Field field = getFieldByFieldName(obj, fieldName);
        if (field.isAccessible()) {
            field.set(obj, value);
        } else {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        }
    }
    
	public static void setFieldValue(Object object, String fieldName,Object value){
        Field field = getDeclaredField(object, fieldName);
        if (field == null)
            throw new IllegalArgumentException("Could not find field ["
                    + fieldName + "] on target [" + object + "]");
        makeAccessible(field);
        try {
            field.set(object, value);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }
	
	public static Object getFieldValue(Object object, String fieldName) {
		Field field = getDeclaredField(object, fieldName);
		if (field == null)
			throw new IllegalArgumentException("Could not find field ["
					+ fieldName + "] on target [" + object + "]");
		makeAccessible(field);
		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static Field getDeclaredField(Object object, String filedName) {
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(filedName);
			} catch (NoSuchFieldException e) {
				// Field 不在当前类定义, 继续向上转型 
			}
		}
		return null;
	}
	
	private static void makeAccessible(Field field) {
		if (!Modifier.isPublic(field.getModifiers())) {
			field.setAccessible(true);
		}
	}
}
