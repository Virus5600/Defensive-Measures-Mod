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

import com.virus5600.defensive_measures.block.traps.tier3.AntiTankMineHawkinsBlock;
import com.virus5600.defensive_measures.entity.projectiles.AntiTankHawkinsEntity;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

/**
 * The custom {@link BlockItem} class for the {@link AntiTankMineHawkinsBlock}, allowing said
 * block's item to have custom behavior and properties.
 * <br><br>
 * Historically, while the <a href="https://en.wikipedia.org/wiki/Hawkins_grenade">Hawkins AT mine</a>
 * was officially designated as a "grenade," it was designed from the ground up as a dual-purpose
 * weapon. This class mirrors that history by allowing the Hawkins to be thrown as a projectile
 * (like a grenade), which then places itself as a block (a landmine) upon landing.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
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
