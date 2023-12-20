package com.minersstudios.msessentials.discord;

@FunctionalInterface
public interface WaitingReplyTask {

    /**
     * @return True if a task is done and can be removed from a waiting list or
     *         false if a task is not done and should be kept in a waiting list
     */
    boolean run();
}
