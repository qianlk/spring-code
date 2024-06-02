package org.example.s01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 用作事件监听器
 *
 * @author qlk
 */
@Component
public class Component2 {

    private static final Logger log = LoggerFactory.getLogger(Component2.class);

    @EventListener
    public void listen(UserRegisteredEvent event) {
        log.debug("事件监听: {}", event);
        log.debug("发送短信");
    }
}
