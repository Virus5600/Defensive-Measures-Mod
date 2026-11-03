# TODO

## CURRENT TASK

- [Implement Catapult Tower (#45)](#implement-catapult-tower-45)
- [Reduce Projectile Persistence After Block Hit (#76)](#reduce-projectile-persistence-after-block-hit-76)

---

## `TurretItemRetrievedCriterion` - Class

Change implementation. Make this criterion an advancement trigger for creating
its first turret.

## `TargetPlayerGoal` - Class

Fully implement this class after implementing a UI for the turrets.

## Update Code to 26.3 for Defensive Measures v1.2.0-beta (#75)

## Implement Catapult Tower (#45)

### Catapult Tower

In the medieval times, catapults are not only used as siege weapons but also defensive weapons as part of their defensive fortifications.

<img alt="Image" src="https://github.com/user-attachments/assets/80d48bd6-07e1-49ec-9d84-3756b482ff8a" />

[By Thomas Ralph Spence](www.artfinding.com/Biography/Spence-Thomas-Ralph) [Public Domain](https://commons.wikimedia.org/w/index.php?curid=30470075)

The defensive emplacement is considered as a Tier-3 turret and thus, can only be crafted in the Workshop.

As for the catapult's model, it will use the Mangonel:

<img alt="Image" src="https://github.com/user-attachments/assets/43abe1ca-c267-4599-936f-23d38943c97f" />

This allows the implementation to have an easier time lobbing projectiles than a sling-launched projectile from an Onager.

As for its projectile, it will lob blocks as a custom block entity under the Defensive Measures namespace, allowing for a broader customization. Said projectile will be under the Explosive Projectile base class and for now, will use Cobblestone. The reasoning behind the use of Explosive Projectile as base class is for a future implementation that will allow Block Projectiles to be explosive, depending on a configuration.

This turret will be part of the Tier-3 turrets, occupying a 3x3 area and can only be crafted with the 9x9 bench called [Workshop](https://github.com/Virus5600/Defensive-Measures-Mod/issues/48).

## Reduce Projectile Persistence After Block Hit (#76)

According to **[PlagueDottore](https://www.curseforge.com/members/plaguedottore)** in CurseForge:
> Small feedback, if possible, reduce the time it takes for ballista bolts to disappear (maybe by half). If you have a few, like 10-20 and they keep shooting, the bolts start to stack up and it hits the FPS.
> Maybe of course I missed some menu where you can edit it, but if it is part of the mod, would be amazing if you adjust it - really helps with performance.

With this in mind, persistence time will be reduced to 30 seconds (600 ticks), halving the persistence time of any kinetic projectiles' life from the original 60 seconds (1200 ticks).

If issue persist, re-open this issue and peg the persistence time further to just 10 seconds (200 ticks).
