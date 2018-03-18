package com.linzhou.helper;

import com.linzhou.abnormal.CheckException;
import com.linzhou.annotation.Check;
import com.linzhou.annotation.MyCheck;
import com.linzhou.enumeration.IsNullEnum;
import com.linzhou.result.CheckResult;
import com.linzhou.util.ReflexUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * 包名：   com.linzhou.helper
 * 创建者:  linzhou
 * 创建时间:18/03/16
 * 描述:   校验对象的参数是否合法
 */
public class CheckHelper {

    /**
     * 对象参数校验
     *
     * @param o 要进行参数校验的对象
     * @return 参数校验结果分装了
     * @throws CheckException
     */
    public static CheckResult check(Object o) throws CheckException {
        Class<?> c = o.getClass();
        Field[] fields = c.getDeclaredFields();
        StringBuilder resultMsgBd = new StringBuilder();
        boolean flag;
        for (Field field : fields) {
            flag = true;
            if (field.isAnnotationPresent(Check.class)) {
                Check check = field.getAnnotation(Check.class);
                flag = checkIsNull(field, o, resultMsgBd, check) &&
                        checkRegular(field, o, resultMsgBd, check);
            }
            if (field.isAnnotationPresent(MyCheck.class)) {
                MyCheck myCheck = field.getAnnotation(MyCheck.class);
                flag = flag && checkMyCheck(field, o, myCheck, resultMsgBd);
            }

        }
        String resultMsg = resultMsgBd.toString();
        if (StringUtils.isNotBlank(resultMsg)) {
            return CheckResult.creatErrorResult(resultMsg);
        }
        return CheckResult.creatSuccessResult();

    }

    /**
     * 对象参数校验
     *
     * @param o              要进行参数校验的对象
     * @param checkFieldName 要校验或不校验的成员变量名称
     * @param checkFlag      true则checkFieldName为要校验的成员变量的名称,否则则是不进行校验的成员变量名称
     * @return 参数校验结果分装了
     * @throws CheckException
     */
    public static CheckResult check(Object o, HashSet<String> checkFieldName, boolean checkFlag) throws CheckException {
        Class<?> c = o.getClass();
        Field[] fields = c.getDeclaredFields();
        StringBuilder resultMsgBd = new StringBuilder();
        boolean flag;
        for (Field field : fields) {
            String fieldName = field.getName();
            if (checkFlag ? checkFieldName.contains(fieldName) : !checkFieldName.contains(fieldName)) {
                flag = true;
                if (field.isAnnotationPresent(Check.class)) {
                    Check check = field.getAnnotation(Check.class);
                    flag = checkIsNull(field, o, resultMsgBd, check) &&
                            checkRegular(field, o, resultMsgBd, check);
                }
                if (field.isAnnotationPresent(MyCheck.class)) {
                    MyCheck myCheck = field.getAnnotation(MyCheck.class);
                    flag = flag && checkMyCheck(field, o, myCheck, resultMsgBd);
                }
            }


        }
        String resultMsg = resultMsgBd.toString();
        if (StringUtils.isNotBlank(resultMsg)) {
            return CheckResult.creatErrorResult(resultMsg);
        }
        return CheckResult.creatSuccessResult();

    }

    /**
     * 对象参数校验器
     *
     * @param o              要进行参数校验的对象
     * @param checkFieldName 需要校验的成员变量名称
     * @return 参数校验结果分装了
     * @throws CheckException
     */
    public static CheckResult check(Object o, HashSet<String> checkFieldName) throws CheckException {
        return check(o, checkFieldName, true);
    }


    /**
     * 类的成员变量的正则格式校验
     *
     * @param field
     * @param o
     * @param resultMsgBd
     */
    private static boolean checkRegular(Field field, Object o, StringBuilder resultMsgBd, Check check) throws CheckException {
        //如果不是String类型的话不执行正则校验
        if (String.class != field.getType()) {
            return true;
        }
        String regular = check.regular();
        //没有正则表达式则不执行正则校验
        if (StringUtils.isBlank(regular)) {
            return true;
        }
        Pattern regex = Pattern.compile(regular);
        Matcher matcher = regex.matcher((String) ReflexUtil.getValueForField(field, o));

        String msg = check.regularmsg();
        if (StringUtils.isBlank(msg)) {
            msg = "格式不合法!";
        }
        if (!matcher.matches()) {
            resultMsgBd.append(field.getName()).append(msg);
            return false;
        }
        return true;
    }

    /**
     * 类的成员变量非空校验
     *
     * @param field 成员变量对象
     * @param o     类的对象
     * @param sb    系统提示消息
     */
    private static boolean checkIsNull(Field field, Object o, StringBuilder sb, Check check) throws CheckException {
        IsNullEnum isNull = check.isnull();
        String msg = check.isnullmsg();
        if (StringUtils.isBlank(msg)) {
            msg = isNull.getMsg();
        }
        if (isNull == IsNullEnum.IS_NOT_EMPTY) {
            //如果是非空校验
            if (ReflexUtil.getValueForField(field, o) == null) {
                sb.append(field.getName()).append(msg);
                return false;
            }
        } else if (isNull == IsNullEnum.IS_NOT_BLANK) {
            //如果是非空或者string类型的非空窜("")校验
            if (String.class == field.getType()) {
                //如果是String类型,判断是否为null或""
                if (StringUtils.isBlank((String) ReflexUtil.getValueForField(field, o))) {
                    sb.append(field.getName()).append(msg);
                    return false;
                }
            } else {
                //如果不是string则判断是否为null
                if (ReflexUtil.getValueForField(field, o) == null) {
                    sb.append(field.getName()).append(msg);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 自定义校验方法
     *
     * @param field       要校验的参数对象
     * @param myCheck     自定义校验注解对象
     * @param resultMsgBd 系统提示消息
     * @return
     */
    private static boolean checkMyCheck(Field field, Object o, MyCheck myCheck, StringBuilder resultMsgBd) throws CheckException {
        //获取校验方法所在的类
        Class<?> checkClass = myCheck.CLASS();
        //校验方法的名称
        String methodName = myCheck.mothed();
        String msg = myCheck.msg();
        Method[] methods = checkClass.getDeclaredMethods();
        Object fieldValue = ReflexUtil.getValueForField(field, o);
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                //如果是校验方法则执行方法
                //判断校验方法的返回值是否是布尔类型
                String returnType = ReflexUtil.getReturnType(method);
                if (!"boolean".equals(returnType) && !"Boolean".equals(returnType)) {
                    throw new CheckException("校验方法" + method.getName() + "返回值类型有误,请确保返回类型为布尔值!");
                }
                //获取方法的参数
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length != 1 || !(parameters[0] == field.getType())) {
                    //如果方法的参数个数不为1或方法参数类型与要校验的成员变量类型不同则抛出异常
                    throw new CheckException("方法参数与要校验的参数类型不一致!");
                }
                boolean result = (boolean) ReflexUtil.invokeCls(method, checkClass, fieldValue);
                if (!result) {
                    resultMsgBd.append(field.getName()).append(msg);
                    return false;
                }
            }
        }
        return true;
    }


}
