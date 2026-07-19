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

## M14 Anti-Personnel (AP) Mine (#42) & British No. 75 "Hawkins" Grenade/Mine Anti-Tank (AT) Mine (#68)

With the implementation of all the crafting benches done, adding the landmines will be the next
milestone, allowing a short reprieve from the complex implementations of the GUI, crafting system,
and the likes. This also serves as a break of relaxation prior to another complex implementation
of a feature down the line.

### Land Mine (AP - M14)

Implement a modern defense/area denial "trap" which explodes when stepped on. This feature will be
implemented as a block and not as an entity. The land mine will be designed based on the in-real life
[M14 Anti-Personnel Landmine](https://en.wikipedia.org/wiki/M14_mine), also known as the "Toepopper" which aims to maim than to kill. Its
lightweight design means that it wasn't blast resistant and thus, will trigger when blasted by an
explosion.

<center>
	<img width="500" height="500" alt="Image" src="https://github.com/user-attachments/assets/89d5f1f2-dc87-48cf-be9d-d892ad84dba3" />
</center>

A landmine is craftable using the new Tier-2 bench called [Workshop](https://github.com/Virus5600/Defensive-Measures-Mod/issues/48) with some ingredients only available from the Tier-2 bench.

#### Additional Details

For starters, the landmine will have its own (and new) damage type called `dm:landmine` for its own
custom death messages:

```json
{
	"death.attack.landmine": "%1$s stepped on a forgotten landmine and blew up.",
	"death.attack.landmine.player": "%2$s violated the Ottawa Treaty and killed %1$s."
}
```

For its damage, it is difficulty based with a damage penalty when waterlogged or not.

Thus, it will follow the formula $(10 + (2 * d)) * p$ where:
- $d$ is `difficultyId`
- $p$ is `waterlogPenalty`

The `difficultyId` is defined as follows:
- Peaceful: `0`
- Easy: `1`
- Normal: `2`
- Hard: `3`

The penalty of being waterlogged will be a constant 25% leaving the `waterlogPenalty` at $0.75$.

This creates a damage table of such (assuming an unarmored entity):

| Difficulty | Base Damage | Multiplier (Difficulty ID) | Total Damage | Waterlogged Damage |
|------------|-------------|----------------------------|--------------|--------------------|
| Peaceful   | 10          | ×0                         | 10           | 8                  |
| Easy       | 10          | ×1                         | 12           | 9                  |
| Normal     | 10          | ×2                         | 14           | 11                 |
| Hard       | 10          | ×3                         | 16           | 12                 |

The implementation of the landmine will need to also store who deployed it (placed it) and check its
neighboring blocks for water so it can waterlog itself depending on the highest value and lowest value
beside it, allowing it to dynamically and inconspicuously camouflage itself on the ground.

The block design will be as small as it can be and will look flush on the surface with its lower model
embedded inside the block below it. This meant that on thin blocks like trapdoors placed on the upper
half, the landmine's body can be seen on the bottom side.

## Anti-Tank Mine (Hawkins Mine)

Implement a throwable anti-tank explosive modeled on the real-life
[British No. 75 "Hawkins" Grenade/Mine](https://en.wikipedia.org/wiki/Hawkins_grenade), a WWII-era device notable for functioning as both a
thrown grenade and a placed anti-tank mine using the exact same impact-triggered mechanism —
historically, a plate on top of the device would crush and crack a chemical igniter when struck with
sufficient force, whether from a thrown impact or a vehicle driving over it. This mod's implementation
mirrors that duality mechanically: the Hawkins Mine is a single throwable item that behaves as a
projectile in flight (dealing blunt damage on entity impact) and settles as a placed block on landing,
unifying "grenade" and "mine" behavior under one trigger system rather than two.

<center>
	<img width="474" height="315" alt="British No. 75 'Hawkins' Grenade/Mine" src="https://github.com/user-attachments/assets/5715429f-38a6-455b-8d8b-37ab3dea0eac" />
</center>

Unlike the [AP Land Mine](https://github.com/Virus5600/Defensive-Measures-Mod/issues/42) (M14-based), which is designed to trigger on any entity stepping on
it, the Hawkins Mine is specifically gated to avoid triggering on regular foot traffic, matching the
real AT/AP distinction where anti-tank mines require far greater applied force than a person's
footstep to detonate.

### Additional Information

#### - Deployment states

The mine has a `THROWN` boolean state.

- If thrown and it lands after hitting a block (rather than an entity), it settles as an exposed,
unburied block (`THROWN = true`).
- If manually placed directly onto a block, it embeds/hides itself the same way the Land Mine does
(`THROWN = false`).
- Regardless of deployment method, it retains the same dynamic waterlogging/camouflage system used
by the Land Mine, checking neighboring blocks to blend into terrain.

#### - Trigger conditions

Uses a new `#dm:vehicles` entity tag to identify qualifying targets, combined with size/motion checks
to approximate real-world applied force:

```
bool doingTriggerActions = isRunning || isFallingFastEnough;
bool triggerMine = (isVehicle || isLarge || (isMedium && doingTriggerActions));
```

- `isVehicle`: entity is tagged `#dm:vehicles` — always sufficient to trigger alone.
- `isLarge`: entity is large enough (e.g. Iron Golem, Ravager) to trigger alone regardless of current
motion.
- `isMedium`: entity is a mid-sized non-vehicle entity; only triggers the mine if also `isRunning`
or falling with sufficient downward velocity, approximating enough applied force/impact to set it off.
- Downward velocity (rather than a jump-state check) is used to detect fall/landing-based triggering,
since it directly measures impact force rather than an abstract "is jumping" game state, and
naturally excludes cases like an entity under Slow Falling that lack sufficient impact velocity.
- 10% misfire chance.

#### Explicitly a one-off filter

This trigger scheme (vehicle/large/medium+motion) is specific to the Hawkins Mine's real-world
dual-use behavior. Future/other AT mines added later are not expected to inherit this same dynamic
filter unless individually designed to.
