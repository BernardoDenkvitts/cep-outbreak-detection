package com.tcc.epidemiologia.utils;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

public class TempoUtils {
    public static LongSupplier clock = System::currentTimeMillis;

    public static void setClock(LongSupplier c) {
        clock = c;
    }

    public static long diasAtras(int dias) {
        return clock.getAsLong() - TimeUnit.DAYS.toMillis(dias);
    }
}
