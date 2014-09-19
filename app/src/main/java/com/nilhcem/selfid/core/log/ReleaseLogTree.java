package com.nilhcem.selfid.core.log;

import timber.log.Timber;

/**
 * Only log warning + errors
 */
public class ReleaseLogTree extends Timber.DebugTree {

    @Override
    public void d(String message, Object... args) {
        // Do not log
    }

    @Override
    public void d(Throwable t, String message, Object... args) {
        // Do not log
    }

    @Override
    public void i(String message, Object... args) {
        // Do not log
    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        // Do not log
    }
}
