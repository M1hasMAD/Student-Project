package com.ares.tienda.videojuegos;

import com.ares.tienda.videojuegos.interfaces.ServiceSetUp;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SetupRunner implements CommandLineRunner {

    private final ServiceSetUp serviceSetUp;

    public SetupRunner(ServiceSetUp setupService) {
        this.serviceSetUp = setupService;
    }

    @Override
    public void run(String... args) {
        serviceSetUp.prepareInitialRecords();
    }
}