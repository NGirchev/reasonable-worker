package ru.girchev.reasonableworker.keyboard;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MacKeyboardAndMouseActivityMonitor implements KeyboardAndMouseActiveMonitor {

    private static final MacKeyboardAndMouseActivityMonitor INSTANCE = new MacKeyboardAndMouseActivityMonitor();

    public static MacKeyboardAndMouseActivityMonitor getInstance() {
        return INSTANCE;
    }

    public interface IOKit extends Library {
        IOKit INSTANCE = Native.load("IOKit", IOKit.class);

        Pointer IOServiceGetMatchingService(Pointer masterPort, Pointer matching);

        Pointer IOServiceMatching(String serviceName);

        int IORegistryEntryCreateCFProperty(Pointer entry, String key, Pointer allocator, int options);

        int IOObjectRelease(Pointer object);
    }

    @Override
    public long getIdleTimeMillis() {
        Pointer ioKitMatching = IOKit.INSTANCE.IOServiceMatching("IOHIDSystem");
        if (ioKitMatching == null) {
            return -1;
        }

        Pointer ioKitService = IOKit.INSTANCE.IOServiceGetMatchingService(null, ioKitMatching);
        if (ioKitService == null) {
            return -1;
        }

        IntByReference idleTimeRef = new IntByReference();
        int result = IOKit.INSTANCE.IORegistryEntryCreateCFProperty(ioKitService, "HIDIdleTime", null, 0);

        IOKit.INSTANCE.IOObjectRelease(ioKitService);

        if (result != 0) {
            return -1;
        }

        long idleTimeNano = idleTimeRef.getValue();
        return idleTimeNano / 1_000_000; // Преобразуем наносекунды в миллисекунды
    }
}