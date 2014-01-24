package org.web4thejob.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
public class ContextUtils implements ApplicationContextAware {
    private static ApplicationContext rootContext;

    public static ApplicationContext getRootContext() {
        return rootContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        rootContext = applicationContext;
    }
}
