# Night Config Fixes (UNOFFICIAL) — Fabric 1.21.1

An unofficial Fabric port of the Fabric workaround from [Fuzs' Night Config Fixes](https://github.com/Fuzss/nightconfigfixes), which is licensed under the Mozilla Public License 2.0.

## What it does

When a Night Config parser reports a ParsingException while reading a config file, this port applies the original Fabric workaround: it removes the unreadable file and invokes the parser's existing FileNotFoundAction. If that action recreates defaults and returns true, the parser reads the recovered file and logs a warning. If it returns false, the original parsing exception is rethrown. This only applies when another loaded mod supplies Night Config and uses the affected path-based parser methods.

**Recovery can discard the unreadable file's contents.** The original data cannot be reconstructed by this workaround. Modpack authors should keep intended defaults in defaultconfigs where the config system supports them and keep backups of valuable configurations.

This is the Fabric-specific workaround only. The two additional workarounds in the upstream project are Forge-specific and are not part of its Fabric version, so they are not included here. The upstream incompatibility declarations for ServerCore and C2ME Base are retained.

## Compatibility

- Minecraft 1.21.1
- Fabric Loader 0.16.5 or newer
- Java 21 or newer
- Conflicts with the upstream nightconfigfixes mod id, ServerCore, and C2ME Base

## Attribution and license

The recovery logic is adapted from Night Config Fixes v8.0.0 by Fuzs. The upstream project and its MPL-2.0 license are available at [Fuzs/nightconfigfixes](https://github.com/Fuzss/nightconfigfixes). The full license is included in LICENSE.md. This port has a separate mod id and release identity.

The original repository's LICENSE-ASSETS.md states that its visual assets are All Rights Reserved. This port does not redistribute those assets; its icon is an independently created vector design.

## Local integration check

The source includes a test-only integration probe that exercises both path-based recovery overloads and confirms valid TOML still parses. It is compiled only in the development test source set and excluded from the release jar. Launch the Fabric client with runClient to execute it; the probe writes only under the development run directory.

## AI assistance

This unofficial port and its release materials were prepared with AI assistance.
