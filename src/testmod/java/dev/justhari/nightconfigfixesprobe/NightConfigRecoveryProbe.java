package dev.justhari.nightconfigfixesprobe;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NightConfigRecoveryProbe {
    private static final System.Logger LOGGER = System.getLogger("NightConfig recovery integration probe");

    public static void run() {
        try {
            Path testDirectory = FabricLoader.getInstance().getGameDir().resolve("nightconfigfixes-integration");
            Files.createDirectories(testDirectory);
            var format = TomlFormat.instance();
            var parser = format.createParser();
            FileNotFoundAction restoreDefaults = (file, configFormat) -> {
                Files.writeString(file, "recovered = 42\n", StandardCharsets.UTF_8);
                return true;
            };

            Path returnedConfig = testDirectory.resolve("returned.toml");
            Files.writeString(returnedConfig, "probe = [\n", StandardCharsets.UTF_8);
            CommentedConfig parsed = parser.parse(returnedConfig, restoreDefaults, StandardCharsets.UTF_8);
            if (parsed.getInt("recovered") != 42 || !Files.readString(returnedConfig).equals("recovered = 42\n")) {
                throw new AssertionError("The returned-config overload did not restore the defaults.");
            }

            Path destinationConfig = testDirectory.resolve("destination.toml");
            Files.writeString(destinationConfig, "probe = [\n", StandardCharsets.UTF_8);
            CommentedConfig destination = format.createConfig();
            parser.parse(
                    destinationConfig,
                    destination,
                    ParsingMode.REPLACE,
                    restoreDefaults,
                    StandardCharsets.UTF_8
            );
            if (destination.getInt("recovered") != 42
                    || !Files.readString(destinationConfig).equals("recovered = 42\n")) {
                throw new AssertionError("The destination-config overload did not restore the defaults.");
            }

            Path validConfig = testDirectory.resolve("valid.toml");
            Files.writeString(validConfig, "probe = 21\n", StandardCharsets.UTF_8);
            CommentedConfig valid = parser.parse(validConfig, FileNotFoundAction.CREATE_EMPTY, StandardCharsets.UTF_8);
            if (valid.getInt("probe") != 21) {
                throw new AssertionError("A valid config changed during parsing.");
            }

            LOGGER.log(System.Logger.Level.INFO, "PASS: both corrupt-config parser overloads recovered, and valid TOML still parses.");
        } catch (Throwable failure) {
            LOGGER.log(System.Logger.Level.ERROR, "FAIL: Night Config recovery integration probe failed.", failure);
            throw new IllegalStateException("Night Config recovery integration probe failed.", failure);
        }
    }
}
