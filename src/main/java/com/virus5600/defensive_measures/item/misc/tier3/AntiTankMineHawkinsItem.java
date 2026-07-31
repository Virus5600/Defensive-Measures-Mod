package com.virus5600.defensive_measures.item.misc.tier3;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import com.virus5600.defensive_measures.entity.projectiles.AntiTankHawkinsEntity;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

public class AntiTankMineHawkinsItem extends BlockItem implements ProjectileItem {
	public static final float PROJECTILE_SHOOT_POWER = 1.5F;

	public AntiTankMineHawkinsItem(Block block, Properties settings) {
		super(
			block,
			settings
				.rarity(Rarity.RARE)
				.useCooldown(2.5F)
		);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		level.playSound(
			null, player.getX(), player.getY(), player.getZ(),
			ModSoundEvents.HAWKINS_ANTI_TANK_MINE_THROW, SoundSource.NEUTRAL,
			0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
		);

		if (level instanceof ServerLevel lvl) {
			AntiTankHawkinsEntity mine = Projectile.spawnProjectileFromRotation(
				AntiTankHawkinsEntity::new, lvl,
				stack, player,
				0.0F, PROJECTILE_SHOOT_POWER, 1.0F
			);

			mine.setBlockState(this.getBlock().defaultBlockState());
			mine.setOwner(player);
		}

		player.awardStat(Stats.ITEM_USED.get(this));
		stack.consume(1, player);

		return InteractionResult.SUCCESS;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override
	public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
		return new AntiTankHawkinsEntity(
			level,
			position.x(), position.y(), position.z(),
			itemStack
		);
	}
}
