# TODO

## `TurretItemRetrievedCriterion` - Class

Change implementation. Make this criterion an advancement trigger for creating
its first turret.

## `TargetPlayerGoal` - Class

Fully implement this class after implementing a UI for the turrets.

## Bug

- TAS Shapeless not showing ghost recipe in TAS

## Pellet Turret

Implementing this will serve as the most basic and rudimentary turret that even day-1 players could make, addressing the #20 Low end turret proposal made 3 years ago. Said turret will most likely be similar in design to a [Blowgun](https://en.wikipedia.org/wiki/Blowgun) but will have some few "creative" liberties with the leather bag to serve as the "blowing" mechanism, allowing the pellet turret to shoot.

![Image](https://github-production-user-asset-6210df.s3.amazonaws.com/19548426/573839557-f9ab1024-6eac-4b19-9fd0-c295aad59b28.png)

<p align="center">
	<small>
		An Aztec Blowgun and Darts 3D model from Sketchfab. by <a href="https://sketchfab.com/mjblosser">Mark Blosser</a>.
	</small>
</p>

Said turret will only have 6 crafting materials with only 3 unique items:
`s` = Stick
`f` = Flint
`l` = Leather

Recipe:

```
_ s f
s _ s
l s _
```

Result: 1x Pellet Turret

<div align="center">
	<img width="523" height="297" alt="Image" src="https://github.com/user-attachments/assets/ffb15dce-2c44-47ac-9253-5aed37a3f991" />
	<p align="center"><small>Pellet Turret recipe visualized</small></p>
</div>

---

This turret will have a base stat of:


| Stat            | Value             |
| --------------- | ----------------- |
| Range           | 10 blocks         |
| Damage          | 2 HP (1 Heart)    |
| Projectile      | `dm:flint_pellet` |
| Target Priority | Nearest Hostile   |

For future references once turret leveling and evolution system is implemented, this turret will not have the same evolution system as the others but instead, will have Level 3 variations depending on potion status effect given to it using a bottled potion:


| Potion                                             | Effect                                                                        | Stat Changes                                                                                                                                                   |
| -------------------------------------------------- | ----------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Strength                                           | Deals increased damage to all targets                                         | +2 HP (+1 Heart) of damage totaling to 4 HP (2 Hearts) of damage                                                                                               |
| Weakness                                           | Applies Weakness effect to targets                                            | -                                                                                                                                                              |
| Instant Health                                     | Heals its targets and damages entities considered "Undead"                    | Targeting Logic changes and pellets do not damage targets that it heals: prioritizes player healing and if full, targets "Undead" entities only                |
| Instant Damage                                     | Damages its targets but consequently heals entities considered "Undead"       | Targeting Logic changes, prioritizing living entities instead of the "Undead" entities                                                                         |
| Regeneration                                       | Applies regeneration to its targets                                           | Has the same mechanic with the Instant Health pellets                                                                                                          |
| Poison                                             | Applies poison to its targets                                                 | Has the same mechanic as the Instant Damage pellets                                                                                                            |
| Wither                                             | Applies damage-over-time (DoT) to its targets                                 | DoT applies to all entities including undead                                                                                                                   |
| Slowness                                           | Applies Slowness to targets                                                   | -                                                                                                                                                              |
| Speed                                              | Increase projectile travel speed, reducing likelihood of dodging the pellet   | no damage change                                                                                                                                               |
| Haste                                              | Increases turret's rate-of-fire (RoF)                                         | -                                                                                                                                                              |
| Slow Falling                                       | Increases range significantly                                                 | Range increases from 10 blocks to 16 blocks                                                                                                                    |
| Blindness                                          | Target loses track of its target and makes them unaware of their surroundings | -                                                                                                                                                              |
| Fire Resistance                                    | Sets target on fire                                                           | Elemental effect: When target has "Wet" elemental effect, triggers Vaporize reaction, damaging the target once more                                            |
| Water Breathing                                    | Applies "Wet" elemental effect while also slowing them down a bit             |                                                                                                                                                                |
| Elemental effect: When target is literally on fire |                                                                               |                                                                                                                                                                |
| Jump Boost                                         | Pellet ricochet to nearby secondary target                                    | less damage per bounce                                                                                                                                         |
| Levitation                                         | Applies Levitation to target                                                  | -                                                                                                                                                              |
| Night Vision                                       | Makes the target glow, increasing its target priority among other turrets     | Prioritizes highest max HP enemies; nearest are the second                                                                                                     |
| Mining Fatigue                                     | Increases target's attack cooldown                                            | -                                                                                                                                                              |
| Invisibility                                       | Makes the turret translucent                                                  | "Translucent" turrets are considered "Stealth" and won't be targeted by enemies until it shoots. Reactivates "Stealth" status after 20 seconds of not shooting |
| Oozing                                             | Holds the target in place for a fixed duration                                | -                                                                                                                                                              |
| Wind Charged                                       | Applies the Wind Charged status effect to its target                          | -                                                                                                                                                              |
