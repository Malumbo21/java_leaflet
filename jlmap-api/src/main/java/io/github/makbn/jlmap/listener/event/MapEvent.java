package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.JLAction;

public record MapEvent(JLAction action) implements Event {
}
