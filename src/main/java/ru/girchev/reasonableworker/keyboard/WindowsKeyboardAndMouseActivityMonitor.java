package ru.girchev.reasonableworker.keyboard;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WindowsKeyboardAndMouseActivityMonitor implements KeyboardAndMouseActiveMonitor {

    private static final WindowsKeyboardAndMouseActivityMonitor INSTANCE = new WindowsKeyboardAndMouseActivityMonitor();

    public static WindowsKeyboardAndMouseActivityMonitor getInstance() {
        return INSTANCE;
    }

    public interface User32Ext extends User32 {
        User32Ext INSTANCE = Native.load("user32", User32Ext.class);
        boolean GetLastInputInfo(WinUser.LASTINPUTINFO result);
    }

    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);
        int GetTickCount();
    }

    @Override
    public long getIdleTimeMillis() {
        WinUser.LASTINPUTINFO lastInputInfo = new WinUser.LASTINPUTINFO();
        lastInputInfo.cbSize = new WinDef.DWORD(Native.getNativeSize(WinUser.LASTINPUTINFO.class)).intValue();

        if (!User32Ext.INSTANCE.GetLastInputInfo(lastInputInfo)) {
            return -1;
        }

        int systemUptime = Kernel32.INSTANCE.GetTickCount(); // Время работы системы
        int lastInputTick = lastInputInfo.dwTime; // Время последнего ввода

        return systemUptime - lastInputTick; // Время простоя в миллисекундах
    }
}
