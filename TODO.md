# TODO

## CURRENT TASK

- Implement Block Projectile Entity Configuration (#54)

---

## `TurretItemRetrievedCriterion` - Class

Change implementation. Make this criterion an advancement trigger for creating
its first turret.

## `TargetPlayerGoal` - Class

Fully implement this class after implementing a UI for the turrets.

## Dirt Turret (#15)

A joke turret that shoots dirt

## Block Projectile (#55)

Implement Block Projectile for the implementation of towers/turrets that utilizes blocks as its projectile. Mainly, this is also for the upcoming implementation of the Catapult Tower (#45).

The implementation of the entity should extend `ExplosiveProjectileEntity` abstract class while also allowing said projectile to be configurable using a JSON configuration file. In that regard, the implementation of configurability falls under #47.

## Block Projectile Entity Configuration (#54)

### Block Projectile Entity Configuration

Provides a configurable file that could be used to define blocks that could be used as projectiles for certain turrets that uses blocks as projectiles.

A new interface must also be created for this special case of projectile.

#### Structure

The JSON structure for this config file will go as:

- `entities`: `<object>`
  - `<entityId>`: `<object>`
    - `default`: `<object>`
    - `blocks`: `<array><object></array>`
      - `block`: `<array><string></array>`
      - `drag`: 0.0 - 1.0
      - `gravity`: 0.000 - 0.100
      - `damage`: 0 - 1000
      - `piercing`: 0 - 100
      - `fire`: `<boolean>`
      - `explosive`: `<boolean>` | `<object>`
        - `damage`: 0 - 1000
        - `radius`: 1.0 - 100.0

#### Key Description

To understand how what each key does, a comprehensive documentation of what each key does to the projectile:


| Key                                  | Type                           | Default | Description                                                                                                                                                                                                                                                                                                                                                                                  |
| ------------------------------------ | ------------------------------ | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entities`                           | JSON object                    | **TBD** | Contains the list of entities that shoots the Block Projectile like the**Catapult Tower** (`dm:catapult_tower`). An entity that does not shoot this projectile will be ignored.                                                                                                                                                                                                              |
| `entities.entityId`                  | Entity ID                      | **TBD** | The entity ID that will have its projectile modified (i.e.:`dm:catapult_tower`). If a default turret isn't defined in the file yet the `replace` key's value is `true`, it will use the default configuration.                                                                                                                                                                               |
| `entities.entityId.default`          | JSON object                    | **TBD** | Contains a default fallback for all values in the other attributes. Basically, it hold a fallback value for the`drag`, `gravity`, `damage`, `piercing`, `fire`, and `explosive` attributes.                                                                                                                                                                                                  |
| `entities.entityId.blocks`           | Array (JSON object)            | **TBD** | An array containing the attributes for this/these block/s.                                                                                                                                                                                                                                                                                                                                   |
| `entities.entityId.blocks.block`     | Array (Item ID OR Item Tag ID) | **TBD** | An array of item IDs or item tag IDs that the entity it is attached to will be able to shoot. An example of this would be the Catapult Tower (`dm:catapult_tower`) lobbing a Cobblestone, so `minecraft:cobblestone` is defined as one of its item ID, or `#minecraft:anvil` to include all the anvils.                                                                                      |
| `entities.entityId.blocks.drag`      | `<float>` (`0.0` to `1.0`)     | `0.99`  | The velocity multiplier after the drag is applied. Value will clamp between 0 and 1 as negative value will leave the projectile inverting its velocity every tick. Values more than 1 will instead increase its velocity. The default`0.99` value means that it loses 1% of its velocity every tick, simulating drag as it moves.                                                            |
| `entities.entityId.blocks.gravity`   | `<float>` (`0.000` to `0.100`) | `0.05`  | The gravity defines how much downward velocity will be applied to the projectile every tick. Clamping it to`0.0` to `0.1` allows the projectile to act more in line with the Minecraft world's fictional gravity.                                                                                                                                                                            |
| `entities.entityId.blocks.damage`    | `<int>` (`0` to `1000`)        | `4`     | Do not confuse this with the`entities.entityId.blockId.explosive.damage`. This `damage` key defines the damage dealt when the projectile **HITS** or **COLLIDES** with an entity. Capping the damage to 1000 is a design choice to allow said turret to balance out with potentially unhinge modded blocks and mods with inflated monster HP.                                                |
| `entities.entityId.blocks.piercing`  | `<int>` (`0` to `100`)         | `0`     | The piercing of the mod works differently than the vanilla piercing. The defined value for the`piercing` key will determine how many unarmored entities the projectile can pass through before getting destroyed. Armored entities may reduce the number of entities it could pierce and may even reduce the projectile's speed.                                                             |
| `entities.entityId.blocks.fire`      | `<boolean>`                    | `false` | Sets the projectile on fire when launched. This allows the projectile to also set the entity it hits on fire, providing additional fire damage to anyone it hits.                                                                                                                                                                                                                            |
| `entities.entityId.blocks.explosive` | `<boolean>` or JSON Object     | `false` | Determines whether the block projectile launched/shot by the turret explodes on contact. When this key is set to`true` or has a defined custom value inside, it disregards the `piercing` key regardless of its value.<br><br>Having its value as `true` makes the projectile use the hardcoded default value. However, providing an object will allow the explosion to be a bit customized. |
| `entities.entityId.explosive.damage` | `<int>` (`0` to `1000`)        | `5`     | Do not confuse this with the`entities.entityId.blockId.damage`. This `damage` key defines the damage dealt by the explosion of this projectile. Capping the damage to 1000 is a design choice to allow the projectile to allow said turret to balance out with potentially unhinge modded blocks and mods with inflated monster HP.                                                          |
| `entities.entityId.explosive.radius` | `<float>` (`1.0` to `100.0`)   | `2.5`   | Determines the radius of the explosion, damaging all entities within said radius.                                                                                                                                                                                                                                                                                                            |

#### Config Priority

As such, to create a player-friendly configuration implementation, a priority hierarchy will also be implemented.


| File      | Location                                    | Authority                   |
| --------- | ------------------------------------------- | --------------------------- |
| Built-in  | Inside the JAR                              | Lowest (fallback/reference) |
| Global    | `config/block-projectiles.json`             | Medium (server-wide)        |
| Per-World | `saves/world/config/block-projectiles.json` | Highest (per-save override) |

#### Additional Information

While the config file allows modded blocks as projectile, the config file only affect turrets that has uses `UsesBlockProjectile` interface. This meant that, unless modified, other entities that does not shoot use said interface will be ignored.

As for ignoring stuff, the implementation of this feature will also ignore entries under the `entities.entityId` that aren't blocks instead of crashing. This will make the debugging easier and not frustrating.

Lastly, to specify what blocks the turret can shoot, a separate file will be created to define the ammo list (blocks). It will be located under the item tags `tags/item/turret_block_ammo` directory, with all tag files containing the `_ammo` suffix while the main file name will be the turret's ID. For example, for the Dirt Turret (`dm:dirt_turret`), it's tag file will be named as `dirt_turret_ammo.json`. And like other item tag files, it will follow the same semantics and structures a normal item tag uses.

Basically, the config file will define the projectile's behavior while the item tag file will define what blocks the turret can shoot. This allows for a more flexible and modular approach to configuring turrets and their projectiles.
