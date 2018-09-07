package net.lightapi.portal.user.common.event;

/**
 * Functional interface to emitting .
 */
@FunctionalInterface
public interface UserEventEmitter {

  void emit(UserEvent userEvent);

}
