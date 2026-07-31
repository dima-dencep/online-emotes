/*
 * Copyright 2023 - 2026 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes;

import org.redlance.common.services.AdvancedService;
import org.redlance.common.services.ServiceUtils;

import java.nio.file.Path;

public interface OnlineEmotesPlatform extends AdvancedService {
    OnlineEmotesPlatform INSTANCE = ServiceUtils.loadService(OnlineEmotesPlatform.class);

    Path getModFile(String modid);

    Path getConfigPath();
}
