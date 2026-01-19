package com.coffee.monitoring.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// Configura path base JAX-RS
@ApplicationPath("/api")
public class MonitoringApplication extends Application {
    // Auto-discovery dei resource con @Path
}
