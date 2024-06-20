package com.flipperdevices.bridge.service.impl.delegate;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RemoveBondHelper {
    public static void removeBand(BluetoothDevice device) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method removeBond = device.getClass().getMethod("removeBond");
        removeBond.invoke(device);
    }
}
