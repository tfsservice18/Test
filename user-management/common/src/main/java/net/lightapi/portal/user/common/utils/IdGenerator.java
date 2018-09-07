package net.lightapi.portal.user.common.utils;

import com.networknt.eventuate.common.Int128;

/**
 * interface to Id Generator
 *
 */
public interface IdGenerator {
  Int128 genId();
}
