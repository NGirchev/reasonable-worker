package ru.girchev.reasonableworker.util;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class Utils {

    public static OSType getOperatingSystemType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OSType.WINDOWS;
        } else if (osName.contains("mac")) {
            return OSType.MAC;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OSType.LINUX;
        } else throw new IllegalArgumentException("Unknown OS");
    }

    public static boolean after(LocalDateTime prev, int seconds) {
        boolean res = Duration.between(prev, LocalDateTime.now()).toSeconds() > seconds;
        if (res) {
            log.info("{} after {} seconds", prev, seconds);
        }
        return res;
    }
}
