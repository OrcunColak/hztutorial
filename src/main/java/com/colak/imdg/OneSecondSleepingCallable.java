package com.colak.imdg;

import java.io.Serializable;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;

public class OneSecondSleepingCallable implements Callable<Integer>, Serializable {

    private void sleepSeconds(int seconds) {
        try {
            SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Integer call() {
        sleepSeconds(1);
        return 1;
    }

}
