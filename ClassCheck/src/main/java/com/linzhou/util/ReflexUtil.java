package com.linzhou.util;

import com.linzhou.abnormal.CheckException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 　　　　　　　　┏┓　　　┏┓+ +
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 * 佛曰:
 * 写字楼里写字间，写字间里程序员；
 * 程序人员写程序，又拿程序换酒钱。
 * 酒醒只在网上坐，酒醉还来网下眠；
 * 酒醉酒醒日复日，网上网下年复年。
 * 但愿老死电脑间，不愿鞠躬老板前；
 * 奔驰宝马贵者趣，公交自行程序员。
 * 别人笑我忒疯癫，我笑自己命太贱；
 * 不见满街漂亮妹，哪个归得程序员？
 * ---------------------------
 * 项目名： ClassCheck
 * 包名：   com.linzhou.util
 * 创建者:  linzhou
 * 创建时间:18/03/17
 * 描述:
 */
public class ReflexUtil {

    /**
     * 获取参数的值
     *
     * @param field 要获取值的参数反射对象
     * @param o     参数所在的对象
     * @return
     */
    public static Object getValueForField(Field field, Object o) throws CheckException {
        //设置这个参数可以访问,这样private属性的参数的值就可以获取了
        try {
            field.setAccessible(true);
            return field.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new CheckException(e.getMessage());
        }
    }

    /**
     * 判断方法是否是静态方法
     *
     * @param method 要判断是否为静态的方法
     * @return static ? true : false
     */
    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * 调用方法
     *
     * @param method 方法对象
     * @param obj    方法所在的对象(如果是静态方法则为null)
     * @param args   方法所需要的参数
     * @return 执行结果
     * @throws CheckException
     */
    public static Object invoke(Method method, Object obj, Object... args) throws CheckException {
        try {
            //如果是则直接调用
            return method.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CheckException(e.getMessage());
        }
    }

    /**
     * 调用方法
     * @param method   方法对象
     * @param cls      方法所在的类
     * @param args     法所需要的参数
     * @return 执行结果
     * @throws CheckException
     */
    public static Object invokeCls(Method method, Class<?> cls, Object... args) throws CheckException {
        if (isStatic(method)){
            return invoke(method,null,args);
        }else {
            Object obj = null;
            try {
                obj = cls.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new CheckException(e.getMessage());
            }
            return invoke(method,obj,args);
        }
    }

    /**
     * 获取方法的返回值类型
     * @param method
     * @return
     */
    public static String getReturnType(Method method){
        return method.getGenericReturnType().toString();
    }



}
