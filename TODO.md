# TODO

## CURRENT TASK

- Implement 9-slice texture for the GUI.
- Refactor GUI to use the new 9-slice texture.

-------------

## `TurretItemRetrievedCriterion` - Class

Change implementation. Make this criterion an advancement trigger for creating
its first turret.

## `TargetPlayerGoal` - Class

Fully implement this class after implementing a UI for the turrets.

## Workshop (#48) & Fabrication Matrix (#63)

With the current work leading to the refactorization of the entire crafting system,
the Workshop and Fabrication Matrix will be simultaneously worked on.

The Workshop is a direct upgrade to the Turret Assembly Station, allowing the player
to craft Tier 3 turrets and equipments. It will offer a 9x9 crafting grid with a
blueprint system (recipe book), alongside the same features as the Turret Assembly
Station. The Workshop will also have a custom GUI and crafting system. Furthermore,
the Workshop will also offer a flattened recipe for all turrets below Tier 3 while
Tier 3 turrets will require the crafting of modular parts before crafting the turret
itself.

The Fabrication Matrix is the final and direct workbench upgrade that allows the
player to craft all turrets and equipments the mod has to offer. At the same time,
the Fabrication Matrix, like its predecessors, will offer a discounted flat recipe
for all turrets.

### Workshop

Workshop is a workbench similar to [Turret Assembly Station or TAS](https://github.com/Virus5600/Defensive-Measures-Mod/issues/35) that allows the player to craft Tier 3 turrets and equipments. This custom workbench will act like a crafting table but only allows turrets and equipments related to the Defensive Measures to be crafted with it.

In this new workbench, tiers 0 to 2 are available for creation as well with a flattened recipe. Tier 3 will use the "modular parts" recipe, creating the parts for the turrets before crafting the turrets themselves.

Workbench will have the following features:

- [x] 9x9 Crafting Grid
- [x] Blueprint System
- [ ] Custom GUI
- [x] Recipe Book (Blueprint) Integration
- [x] Custom Crafting System

### Fabrication Matrix

Fabrication Matrix is an upgrade to the [Workshop](https://github.com/Virus5600/Defensive-Measures-Mod/issues/48) that allows the player to craft Tier 3 turrets and equipments as a flat recipe instead of crafting it in parts. This custom workbench will act like a crafting table but only allows turrets and equipments related to the Defensive Measures to be crafted with it.

In this upgraded workbench, tiers 0 to 3 are available for creation. All recipes are now flattened and will retain the discounted recipe cost making it promising for any players focused on creating items from this mod.

Fabrication Matrix will have the following features:

- [ ] 9x9 Crafting Grid
- [ ] Blueprint System
- [ ] Custom GUI
- [ ] Recipe Book (Blueprint) Integration
- [ ] Custom Crafting System
