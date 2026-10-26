/*
 * Adapted from Night Config Fixes v8.0.0 by Fuzs.
 * Original source: https://github.com/Fuzss/nightconfigfixes
 * Ported for Fabric on Minecraft 1.21.1 by Hari / JustHari01.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package fuzs.nightconfigfixes.mixin;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingException;
import fuzs.nightconfigfixes.NightConfigFixes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Specify target class as string to maybe help when night config is not present on Fabric, not sure if it really makes a different, after all Mixin is very forgiving when the target class does not exist.
 * <p>Also we are forced to use {@link Overwrite} instead of {@link org.spongepowered.asm.mixin.injection.Inject} as the latter is not supported in interfaces.
 */
@Mixin(targets = "com.electronwill.nightconfig.core.io.ConfigParser")
@SuppressWarnings("target")
interface ConfigParserFabricMixin<C extends Config> {

    @Shadow(remap = false)
    ConfigFormat<C> getFormat();

    /**
     * Replaces the path parser so a caller-provided recovery action can recreate a damaged config.
     * @author Hari / JustHari01
     * @reason Restore the config through its existing missing-file callback after a parse failure.
     */
    @Overwrite(remap = false)
    default C parse(Path file, FileNotFoundAction nefAction, Charset charset) {
        try {
            if (Files.notExists(file) && !nefAction.run(file, this.getFormat())) {
                return this.getFormat().createConfig();
            }
            try (InputStream input = Files.newInputStream(file)) {
                return this.parse(input, charset);
            } catch (ParsingException e) {
                // we come in here and catch the ParsingException, just try to recreate the config via the FileNotFoundAction
                // if we fail just throw the ParsingException like we aren't even here
                try {
                    Files.delete(file);
                    if (nefAction.run(file, this.getFormat())) {
                        C config;
                        try (InputStream input = Files.newInputStream(file)) {
                            config = this.parse(input, charset);
                        }
                        NightConfigFixes.LOGGER.warn("Configuration file {} could not be parsed. Correcting", file.toAbsolutePath());
                        return config;
                    }
                } catch (Throwable t) {
                    e.addSuppressed(t);
                }
                throw e;
            }
        } catch (IOException e) {
            throw new WritingException("An I/O error occurred", e);
        }
    }

    @Shadow(remap = false)
    C parse(InputStream input, Charset charset);

    /**
     * Replaces the destination parser with the same recovery behavior for configs parsed in place.
     * @author Hari / JustHari01
     * @reason Preserve in-place parsing while allowing the caller's callback to recreate the file.
     */
    @Overwrite(remap = false)
    default void parse(Path file, Config destination, ParsingMode parsingMode, FileNotFoundAction nefAction, Charset charset) {
        try {
            if (Files.notExists(file) && !nefAction.run(file, this.getFormat())) {
                return;
            }
            try (InputStream input = Files.newInputStream(file)) {
                this.parse(input, destination, parsingMode, charset);
            } catch (ParsingException e) {
                // we come in here and catch the ParsingException, just try to recreate the config via the FileNotFoundAction
                // if we fail just throw the ParsingException like we aren't even here
                try {
                    Files.delete(file);
                    if (nefAction.run(file, this.getFormat())) {
                        try (InputStream input = Files.newInputStream(file)) {
                            this.parse(input, destination, parsingMode, charset);
                        }
                        NightConfigFixes.LOGGER.warn("Configuration file {} could not be parsed. Correcting", file.toAbsolutePath());
                        return;
                    }
                } catch (Throwable t) {
                    e.addSuppressed(t);
                }
                throw e;
            }
        } catch (IOException e) {
            throw new WritingException("An I/O error occurred", e);
        }
    }

    @Shadow(remap = false)
    void parse(InputStream input, Config destination, ParsingMode parsingMode, Charset charset);
}
