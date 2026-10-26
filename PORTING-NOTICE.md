# Porting and third-party notices

This project ports the Fabric-specific recovery workaround from [Fuzs/nightconfigfixes](https://github.com/Fuzss/nightconfigfixes), version 8.0.0. The original source is licensed under MPL-2.0; the original license text is retained in LICENSE.md, and the adapted Java source files carry MPL notices and attribution.

The upstream repository's LICENSE-ASSETS.md states that its visual assets are All Rights Reserved. No upstream logo, banner, or other artwork is included here. src/main/resources/icon.svg is original artwork for this port; icon.png is its raster export for mod platforms.

## Port changes

- Targets Minecraft 1.21.1 with Fabric Loader and Java 21.
- Uses a distinct mod id and release identity.
- Retains the two parser overload recoveries, the caller-provided FileNotFoundAction, and the warning emitted after recovery.
- Retains the upstream incompatibility declarations for ServerCore and C2ME Base.
- Updates only build metadata and presentation; the recovery behavior remains the upstream Fabric behavior.
