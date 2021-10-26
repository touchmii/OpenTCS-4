/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.lvsrobot.kernel.extensions.sqlservice.order.binding;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class Command {
    @JsonPropertyDescription("The command of the Vehicle")
    @JsonProperty(required = true)
//    @Size(min = 1)
    private String command = null;

    public Command() {
    }
    public String getCommand() { return command; }

    public void setCommand(String _command) { command = _command; }
}
