package com.colak.imdg;

import java.io.Serializable;

import static java.util.concurrent.TimeUnit.SECONDS;

public class OneSecondSleepingRunnable implements Runnable, Serializable {

    private void sleepSeconds(int seconds) {
        try {
            SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        sleepSeconds(1);
    }

}
