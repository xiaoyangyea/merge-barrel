[English](README.md) | [简体中文](README_zh.md)

# Merge Barrel

A lightweight Minecraft Fabric mod that allows two adjacent vanilla barrels to merge into a double-chest-like container with 54 slots. Check [Features](#features) section for placement rules.

> **Note:** This mod requires [Fabric API](https://modrinth.com/mod/fabric-api) and is designed for Minecraft 26.3+.

## Features

- **Barrel Merging**
  - Automatically merges when two barrels with the same facing direction are placed along the facing axis.
  - The merged barrel is treated as a single container.
- **Redstone Compatibility**
  - The merged barrel can be detected as a single entity by Redstone Comparators.
- **Hopper Automation**
  - Hoppers can interact with the merged barrel as a whole, rather than only being able to input/output to a single-block container.

## Credits

- **Inspiration**: The concept and logic of merging double barrels are inspired by the **Large Barrel** feature from the [Carpet TIS Addition](https://github.com/Fallen-Breath/carpet-tis-addition) mod by Fallen_Breath and contributors.
- **Implementation**: This mod is a standalone implementation written from scratch specifically for modern Minecraft versions (26.3+).

## A Note from the Author

Honestly, this is my very first Minecraft mod. During development, I used a large amount of AI-assisted code and documentation (including the drafting and cross-translation of bilingual documents). Although I attempted to understand the principles, due to my current lack of in-depth research into the source code and development docs, and given my known weak foundation, the depth and frequency of my questions to the AI were similarly limited. This objectively results in gaps in my capabilities, inevitably leading to errors and omissions in this project.

If you find any issues while using it, please feel free to submit an Issue or PR. I will try to understand and learn the reasons behind them, and attempt to fix them as best as I can. Thank you for your time reading this and for any feedback you provide to improve this project.

## License

This project is licensed under the **GNU Lesser General Public License v3.0 (LGPL-3.0)**. See the [LICENSE](LICENSE) file for details.