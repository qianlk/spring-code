package org.example.s01;

import org.springframework.context.ApplicationEvent;

/**
 * 事件
 * @author qlk
 */
public class UserRegisteredEvent extends ApplicationEvent {
    /**
     *
     * @param source 事件源
     */
    public UserRegisteredEvent(Object source) {
        super(source);
    }
}
