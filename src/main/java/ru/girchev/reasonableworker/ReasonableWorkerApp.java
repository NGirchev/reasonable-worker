package ru.girchev.reasonableworker;

import lombok.extern.slf4j.Slf4j;
import ru.girchev.reasonableworker.mouse.MouseMover;

@Slf4j
public class ReasonableWorkerApp {

    public static void main(String[] args) {
        new MouseMover(180, 3).start();
        log.info("Started...");
    }
}
