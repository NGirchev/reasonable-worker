package ru.girchev.reasonableworker.mouse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.girchev.reasonableworker.keyboard.KeyboardAndMouseActiveMonitor;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Random;

import static java.lang.Thread.sleep;
import static ru.girchev.reasonableworker.util.Utils.after;

@Slf4j
@Getter
@Setter
public class MouseMover {

    private int inactiveInSeconds;
    private int movingTime;
    private boolean useOnlyMouseInactivity;

    private final Robot robot;

    public MouseMover(final int inactiveInSeconds, final int movingTime, boolean useOnlyMouseInactivity) {
        this.inactiveInSeconds = inactiveInSeconds;
        this.movingTime = movingTime;
        this.useOnlyMouseInactivity = useOnlyMouseInactivity;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        log.info("Started capturing mouse...");
    }

    public MouseMover(final int inactiveInSeconds, final int movingTime) {
        this(inactiveInSeconds, movingTime, false);
    }

    private final Random random = new Random();
    private Point location = MouseInfo.getPointerInfo().getLocation();
    private int prevX = location.x;
    private int prevY = location.y;

    private boolean moving = false;
    private boolean started = false;

    private LocalDateTime prevInactivity = LocalDateTime.now();
    private LocalDateTime prevMoving = LocalDateTime.now();

    public void start() {
        started = true;
        new Thread(() -> {
            try {
                while (started) {
                    if (!moving && isInactive()) {
                        moving = true;
                        prevMoving = LocalDateTime.now();
                        log.info("Moving = true");
                    }

                    if (moving) {
                        move();
                        sleep(100);

                        if (after(prevMoving, movingTime)) {
                            moving = false;
                            log.info("Moving = false");
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
            }
        }).start();
    }
    public void stop() {
        started = false;
    }

    private boolean isInactive() {
        boolean inactive;
        if (useOnlyMouseInactivity) {
            inactive = isTheSameMouseLocation();
        } else {
            try {
                inactive = (KeyboardAndMouseActiveMonitor.getIdleTimeMillisFromAnyOS() / 1000) > inactiveInSeconds;
            } catch (Error e) {
                inactive = isTheSameMouseLocation();
            }
        }

        if (inactive) {
            log.info("inactive...");
        }

        return inactive;
    }

    private boolean isTheSameMouseLocation() {
        location = MouseInfo.getPointerInfo().getLocation();
        if (prevX != location.x || prevY != location.y) {
            prevX = location.x;
            prevY = location.y;
            prevInactivity = LocalDateTime.now();
            return false;
        } else {
            return after(prevInactivity, inactiveInSeconds);
        }
    }

    private void move() {
        int x = location.x + random.nextInt(10) - 3;
        int y = location.y + random.nextInt(10) - 3;
        robot.mouseMove(x, y);
    }
}
