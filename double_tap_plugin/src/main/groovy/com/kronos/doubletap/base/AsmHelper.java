package com.kronos.doubletap.base;

import java.io.IOException;

public interface AsmHelper {
    byte[] modifyClass(byte[] srcClass) throws IOException;
}
