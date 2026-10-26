/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package fuzs.nightconfigfixes;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NightConfigFixes implements ModInitializer {
    public static final String MOD_ID = "nightconfigfixes_unofficial";
    public static final String MOD_NAME = "Night Config Fixes (UNOFFICIAL)";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public NightConfigFixes() {
    }

    @Override
    public void onInitialize() {
        if (!Boolean.getBoolean("nightconfigfixes.integrationProbe")) {
            return;
        }
        try {
            Class.forName("dev.justhari.nightconfigfixesprobe.NightConfigRecoveryProbe")
                    .getMethod("run")
                    .invoke(null);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Could not run the Night Config integration probe.", failure);
        }
    }
}
