package net.huwng.vietnamdelight.client;

import net.minecraftforge.client.event.ViewportEvent;
import java.util.Random;

public class ScreenShakeHandler {
    private static int shakeTicks = 0;
    private static final Random random = new Random();

    public static void triggerShake(int duration) {
        shakeTicks = duration;
    }

    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        if (shakeTicks > 0) {
            float shakeStrength = 4.0f;
            float yawOffset = (random.nextFloat() - 1.0f) * shakeStrength;
            float pitchOffset = (random.nextFloat() - 1.0f) * shakeStrength;

            event.setYaw(event.getYaw() + yawOffset);
            event.setPitch(event.getPitch() + pitchOffset);

            shakeTicks--;
        }
    }
}
