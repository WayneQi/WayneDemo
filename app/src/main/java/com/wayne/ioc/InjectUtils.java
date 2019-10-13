package com.wayne.ioc;

import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *  注入工具
 */
public class InjectUtils {

    public static void inject(Object targer){
        injectLayout(targer);
        injectFindViewById(targer);
        injectEvent(targer);
    }

    /**
     * 事件注入
     * @param targer
     */
    private static void injectEvent(Object targer) {
        Class<?> aClass = targer.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<?> annotationClass = annotation.annotationType();
                EventBase eventBase = annotationClass.getAnnotation(EventBase.class);
                if(eventBase == null){
                    continue;
                }
                String listenerSetter = eventBase.listenerSetter();
                Class<?> lisetenerType = eventBase.listenerType();
                String callBackMethod = eventBase.callbackMethoed();
                Method valueMethod = null;
                try{
                    valueMethod = annotationClass.getDeclaredMethod("value");
                    int[] viewId = (int[]) valueMethod.invoke(annotation);
                    for (int id : viewId) {
                        Method findViewByid = aClass.getMethod("findViewById",int.class);
                        View view = (View) findViewByid.invoke(targer,id);
                        if(view == null){
                            continue;
                        }
                        ListenerInvocationHandler invocationHandler = new ListenerInvocationHandler(targer,method);
                        Object proxy =  Proxy.newProxyInstance(lisetenerType.getClassLoader(),new Class[]{lisetenerType},invocationHandler);
                        Method onClickMethod = view.getClass().getMethod(listenerSetter,lisetenerType);
                        onClickMethod.invoke(view,proxy);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param targer
     */
    private static void injectFindViewById(Object targer) {
        Class<?> aClass = targer.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if(viewInject != null){
                int viewId = viewInject.value();
                try {
                    Method findViewByIdMethod = aClass.getMethod("findViewById",int.class);
                    View view = (View)findViewByIdMethod.invoke(targer,viewId);
                    field.setAccessible(true);
                    field.set(targer,view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void injectLayout(Object targer) {
        Class<?> clazz = targer.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if(contentView != null){
            int layout = contentView.value();
            try {
                Method setContentViewMethod = clazz.getMethod("setContentView",int.class);
                setContentViewMethod.invoke(targer,layout);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
