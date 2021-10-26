package com.lvsrobot.kernel.extensions.sqlservice.database;

import org.opentcs.components.kernel.KernelExtension;
import com.lvsrobot.kernel.extensions.sqlservice.SQLServiceConfiguration;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

public class SQLService
    implements KernelExtension {

    private final SQLServiceConfiguration configuration;

    private boolean initialized;

    @Inject
    public SQLService(SQLServiceConfiguration configuration) {
        this.configuration = requireNonNull(configuration, "configuration");
    }

    @Override
    public void initialize() {
        if (isInitialized()) {
            return;
        }
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void terminate() {

    }

}
