# TODO

## CURRENT TASK

- Implement Fabrication Matrix (FabMat)
- Create FabMat model

-------------

## `TurretItemRetrievedCriterion` - Class

Change implementation. Make this criterion an advancement trigger for creating
its first turret.

## `TargetPlayerGoal` - Class

Fully implement this class after implementing a UI for the turrets.

## Metal Detector (#43)

To counteract the new [landmines](https://github.com/Virus5600/Defensive-Measures-Mod/issues/42), a new equipment called [Metal Detector](https://en.wikipedia.org/wiki/Metal_detector) needs to be implemented. The metal detector is a device that can detect metal beneath the ground or underwater, allowing the player to detect landmines. Though, this could also be used to detect the following:
- Iron Ore
- Raw Iron Block
- Iron Block
- Ancient Debris
- Netherite Block

It would also be great to detect metallic turrets but as it stands, it might a bad idea due to the amount of metallic turrets the mod has. If the community wishes for it, then this feature will be implemented down the line.

<img width="1200" alt="Image" src="https://github.com/user-attachments/assets/5186dec5-a59f-4eb6-8553-1f680fa682fe" />

This new equipment can be crafted using the basic crafting table and can be crafted in both the T2 and T3 benches.

### Additional Details

To populate the equipments category of the mod, the Metal Detector can be crafted with two core materials:
- Iron Ingot
- Netherite Scrap

These two allows the metal detector to have tier-based effectivity and durability, allowing new enchantments to be planned and implemented in the near future.

Below dictates the attributes of a metal detector item, followed by a table laying the numerical base values of each attributes.
- **Radius**: The effective radius a metal detector can detect metallic "objects".
- **Durability**: The equipment's durability, determining how long it can be used.

| Core Ingredient | Radius | Durability |
|-----------------|--------|------------|
| Iron Ingot      | 4      | 500        |
| Netherite Scrap | 7      | 1200       |

The radius determines the range of the metal detector's detecting range. Higher range meant earlier warning or notification, allowing the player more room to adjust and/or prepare.

On the other hand, the durability decreases by 1 every (irl) second whenever held in the offhand or mainhand (basically, in an active slot). And since a typical metal detector's battery lasts for 10 hours, which is 41.6% of an entire day (24 hours), the standard iron-based metal detector only has 500 seconds (8.33 minutes) of usage and thus, the 500 durability points. Accordingly, the upgraded and more expensive netherite metal detector has an entire Minecraft day, lasting 1200 seconds (20 minutes) of usage.

## Dirt Turret (#15)

A joke turret that shoots dirt
