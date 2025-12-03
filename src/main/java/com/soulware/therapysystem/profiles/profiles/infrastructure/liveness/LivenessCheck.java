package com.soulware.therapysystem.profiles.profiles.infrastructure.liveness;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.Connection;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;



@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {
    @Resource(lookup = "java:/MySqlDS")
            DataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        boolean dbOk = checkDb();
        return dbOk ?
                HealthCheckResponse.up("db") :
                HealthCheckResponse.down("db");
    }

    private boolean checkDb() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}
