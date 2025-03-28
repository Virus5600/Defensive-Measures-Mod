package com.virus5600.defensive_measures.item.equipments;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An item that allows the player to remove turrets from the world.
 * <br><br>
 * When used on a turret, the turret will be removed from the world, allowing
 * it to drop as an item if the player is in survival mode. When used on a turret
 * while in creative mode, the turret will be removed from the world without
 * dropping as an item unless the player is sneaking, in which case the turret
 * will drop as an item.
 * <br><br>
 * A dropped turret item can be placed back into the world by right-clicking
 * on a block. The turret will be placed on the block if it is a valid location,
 * and if it is on the side of a block, it will be placed on the side of the
 * block. Furthermore, the turret will retain its previous state such as its
 * health and status effects.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class TurretRemoverItem extends MiningToolItem {

	public TurretRemoverItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
		super(
			material,
			BlockTags.AIR,
			attackDamage,
			attackSpeed,
			settings.rarity(Rarity.COMMON)
		);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		// CAN'T DESTROY IN CREATIVE
		return !miner.isCreative();
	}
}
