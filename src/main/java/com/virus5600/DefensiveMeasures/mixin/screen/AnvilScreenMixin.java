package com.virus5600.DefensiveMeasures.mixin.screen;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.virus5600.DefensiveMeasures.interfaces.IAnvilScreen;
import com.virus5600.DefensiveMeasures.item.ModToolMaterials;
import com.virus5600.DefensiveMeasures.util.ItemUtil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

@Mixin(AnvilScreenHandler.class)
@Implements(@Interface(iface = IAnvilScreen.class, prefix = "asi$"))
public abstract class AnvilScreenMixin extends ForgingScreenHandler {
	@Shadow
	private int repairItemUsage;
	@Shadow
	private String newItemName;
	@Shadow
	private final Property levelCost;
	
	public AnvilScreenMixin(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.ANVIL, syncId, inventory, context);
		this.levelCost = Property.create();
		this.addProperty(this.levelCost);
	}
	
	@SuppressWarnings("rawtypes")
	@Intrinsic(displace = true)
	public void asi$updateResult() {
		ItemStack itemStack = this.input.getStack(0);
		this.levelCost.set(1);
		int i = 0;
		int j = 0;
		int k = 0;
		if (itemStack.isEmpty()) {
			this.output.setStack(0, ItemStack.EMPTY);
			this.levelCost.set(0);
		} else {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = this.input.getStack(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack2);
			j += itemStack.getRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getRepairCost());
			this.repairItemUsage = 0;
			if (!itemStack3.isEmpty()) {
				boolean bl = itemStack3.isOf(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantmentNbt(itemStack3).isEmpty();
				int l;
				int m;
				int n;
				if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
					/// MODIFIED AREA ///
					
					// Identifies whether this item is a ToolItem.
					if (ItemUtil.isToolItem(itemStack2.getItem())) {
						// If it is a ToolItem, proceed to get its Material for further checking.
						ToolMaterial mats = ItemUtil.getToolItem(itemStack2.getItem()).getMaterial();
						
						// Check if material is from ModToolMaterials or not.
						if (mats.getClass().isAssignableFrom(ModToolMaterials.class)) {
							// If it is, get the lowest between the repair amount of the said item or the current durability damage of the item
							l = Math.min(itemStack2.getDamage(), ((ModToolMaterials) mats).getRepairAmt(itemStack3.getItem()));
						}
						else {
							// Otherwise, use normal calculations. Lmao
							l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
						}
					}
					else {
						l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					}
					/// MODIFIED AREA END ///
					
					if (l <= 0) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					for(m = 0; l > 0 && m < itemStack3.getCount(); ++m) {
						n = itemStack2.getDamage() - l;
						itemStack2.setDamage(n);
						++i;
						l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					}

					this.repairItemUsage = m;
				} else {
					if (!bl && (!itemStack2.isOf(itemStack3.getItem()) || !itemStack2.isDamageable())) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					if (itemStack2.isDamageable() && !bl) {
						l = itemStack.getMaxDamage() - itemStack.getDamage();
						m = itemStack3.getMaxDamage() - itemStack3.getDamage();
						n = m + itemStack2.getMaxDamage() * 12 / 100;
						int o = l + n;
						int p = itemStack2.getMaxDamage() - o;
						if (p < 0) {
							p = 0;
						}

						if (p < itemStack2.getDamage()) {
							itemStack2.setDamage(p);
							i += 2;
						}
					}

					Map<Enchantment, Integer> map2 = EnchantmentHelper.get(itemStack3);
					boolean bl2 = false;
					boolean bl3 = false;
					Iterator var23 = map2.keySet().iterator();

					label155:
					while(true) {
						Enchantment enchantment;
						do {
							if (!var23.hasNext()) {
								if (bl3 && !bl2) {
									this.output.setStack(0, ItemStack.EMPTY);
									this.levelCost.set(0);
									return;
								}
								break label155;
							}

							enchantment = (Enchantment)var23.next();
						} while(enchantment == null);

						int q = (Integer)map.getOrDefault(enchantment, 0);
						int r = (Integer)map2.get(enchantment);
						r = q == r ? r + 1 : Math.max(r, q);
						boolean bl4 = enchantment.isAcceptableItem(itemStack);
						if (this.player.getAbilities().creativeMode || itemStack.isOf(Items.ENCHANTED_BOOK)) {
							bl4 = true;
						}

						Iterator var17 = map.keySet().iterator();

						while(var17.hasNext()) {
							Enchantment enchantment2 = (Enchantment)var17.next();
							if (enchantment2 != enchantment && !enchantment.canCombine(enchantment2)) {
								bl4 = false;
								++i;
							}
						}

						if (!bl4) {
							bl3 = true;
						} else {
							bl2 = true;
							if (r > enchantment.getMaxLevel()) {
								r = enchantment.getMaxLevel();
							}

							map.put(enchantment, r);
							int s = 0;
							switch (enchantment.getRarity()) {
								case COMMON:
									s = 1;
									break;
								case UNCOMMON:
									s = 2;
									break;
								case RARE:
									s = 4;
									break;
								case VERY_RARE:
									s = 8;
							}

							if (bl) {
								s = Math.max(1, s / 2);
							}

							i += s * r;
							if (itemStack.getCount() > 1) {
								i = 40;
							}
						}
					}
				}
			}

			if (StringUtils.isBlank(this.newItemName)) {
				if (itemStack.hasCustomName()) {
					k = 1;
					i += k;
					itemStack2.removeCustomName();
				}
			} else if (!this.newItemName.equals(itemStack.getName().getString())) {
				k = 1;
				i += k;
				itemStack2.setCustomName(Text.literal(this.newItemName));
			}

			this.levelCost.set(j + i);
			if (i <= 0) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (k == i && k > 0 && this.levelCost.get() >= 40) {
				this.levelCost.set(39);
			}

			if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (!itemStack2.isEmpty()) {
				int t = itemStack2.getRepairCost();
				if (!itemStack3.isEmpty() && t < itemStack3.getRepairCost()) {
					t = itemStack3.getRepairCost();
				}

				if (k != i || k == 0) {
					t = AnvilScreenHandler.getNextCost(t);
				}

				itemStack2.setRepairCost(t);
				EnchantmentHelper.set(map, itemStack2);
			}

			this.output.setStack(0, itemStack2);
			this.sendContentUpdates();
		}
	}
}