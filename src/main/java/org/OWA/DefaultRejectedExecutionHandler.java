package org.OWA;

import java.util.logging.Logger;

public class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
    private static final Logger LOGGER = Logger.getLogger(DefaultRejectedExecutionHandler.class.getName());

    @Override
    public void rejectedExecution(Runnable r, CustomThreadPool executor) {
        LOGGER.warning("[Rejected] Task " + r.toString() + " was rejected due to overload!");
    }
}