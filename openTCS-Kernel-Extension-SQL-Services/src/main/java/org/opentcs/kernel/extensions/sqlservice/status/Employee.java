package org.opentcs.kernel.extensions.sqlservice.status;

import org.javalite.activejdbc.Model;

public class Employee extends Model {
    static{
        validatePresenceOf("first_name", "last_name");
    }
}
