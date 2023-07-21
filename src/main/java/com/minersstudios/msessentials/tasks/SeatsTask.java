package com.minersstudios.msessentials.tasks;

import com.minersstudios.msessentials.MSEssentials;

public class SeatsTask implements Runnable {

    @Override
    public void run() {
        MSEssentials.getCache().seats.entrySet().stream().parallel()
        .forEach(
                entry -> entry.getValue().setRotation(entry.getKey().getLocation().getYaw(), 0.0f)
        );
    }
}