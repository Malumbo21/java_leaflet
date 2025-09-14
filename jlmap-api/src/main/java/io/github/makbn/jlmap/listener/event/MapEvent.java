package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.JLMap;
import io.github.makbn.jlmap.listener.JLAction;

public record MapEvent(JLMap source, JLAction action) implements Event {
}
