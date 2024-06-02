package org.example.s01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author qlk
 */
@Component
public class Component1 {

    private static final Logger log = LoggerFactory.getLogger(Component1.class);

    @Autowired
    private ApplicationEventPublisher publisher;

    public void register() {
        log.debug("用户注册");
        publisher.publishEvent(new UserRegisteredEvent(this));
    }
}
