package my.spring.boot;

import java.lang.reflect.Method;
import java.util.List;

public class ControllerInfo {
    Object controllerInstance;
    Method method;

    public ControllerInfo(Object controllerInstance, Method method) {
        this.controllerInstance = controllerInstance;
        this.method = method;
    }

    public Object invoke(List<Object> args) throws Exception {
        return method.invoke(controllerInstance, args);
    }
}
