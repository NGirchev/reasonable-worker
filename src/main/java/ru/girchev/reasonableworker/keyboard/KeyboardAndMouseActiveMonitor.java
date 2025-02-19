package ru.girchev.reasonableworker.keyboard;

import ru.girchev.reasonableworker.util.Utils;

public interface KeyboardAndMouseActiveMonitor {

    static long getIdleTimeMillisFromAnyOS() {
        return switch (Utils.getOperatingSystemType()) {
            case WINDOWS -> WindowsKeyboardAndMouseActivityMonitor.getInstance().getIdleTimeMillis();
            case MAC ->  MacKeyboardAndMouseActivityMonitor.getInstance().getIdleTimeMillis();
            default -> throw new UnsupportedOperationException("Not supported os");
        };
    }

    long getIdleTimeMillis();
}
