package com.umutyenidil.atlas.messaging.publisher;

import com.umutyenidil.atlas.dto.event.UserRegisteredEvent;

public interface UserEventPublisher {
    void publishUserRegisteredEvent(UserRegisteredEvent event);
}
