/*
 * Copyright 2023 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package com.github.dima_dencep.mods.online_emotes;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.net.URI;

@SuppressWarnings("unused")
public class ConfigExpectPlatform {
    @ExpectPlatform
    public static long reconnectionDelay() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean replaceMessages() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean debug() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static URI address() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int maxContentLength() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean useEpoll() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean selfPings() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int threads() {
        throw new AssertionError();
    }
}
