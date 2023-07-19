package com.minersstudios.msessentials.discord;

@FunctionalInterface
public interface WaitingReplyTask {

    /**
     * @return True if task is done and can be removed from waiting list
     *         or false if task is not done and should be kept in waiting list
     */
    boolean run();
}
