package com.virus5600.DefensiveMeasures;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.virus5600.DefensiveMeasures.advancement.criterion.ModCriterion;
import com.virus5600.DefensiveMeasures.block.ModBlocks;
import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.item.ModItems;
import com.virus5600.DefensiveMeasures.sound.ModSoundEvents;

public class DefensiveMeasures implements ModInitializer {
	public static final String MOD_ID = "dm";
	public static final String MOD_NAME = "DefensiveMeasures";
	public static final Logger LOGGER = LoggerFactory.getLogger(DefensiveMeasures.MOD_ID);
	
	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModSoundEvents.registerSoundEvents();
		ModBlocks.registerModBlocks();
		ModEntities.registerModEntities();
		ModCriterion.registerModItems();
	}
	
	public static void sendChat(String text, ChatType type) {
		sendChat(type.format(text));
	}
	public static void sendChat(String text) {	
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.inGameHud.getChatHud().addMessage(Text.literal(ChatFormat.YELLOW.format("[" + MOD_ID.toUpperCase() + "] ") + text));
	}
	public static void sendEmptyChat() {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.inGameHud.getChatHud().addMessage(Text.literal(""));
	}
	
	
	/////////////
	/// ENUMS ///
	/////////////
	
	/**
	 * Provides all the chat text formatting available in Minecraft; from colors to text formats.
	 * @author Virus5600
	 */
	public enum ChatFormat {
		BLACK('0'),
		DARK_BLUE('1'),
		DARK_GREEN('2'),
		DARK_AQUA('3'),
		DARK_RED('4'),
		DARK_PURPLE('5'),
		GOLD('6'),
		GRAY('7'),
		DARK_GRAY('8'),
		BLUE('9'),
		GREEN('a'),
		AQUA('b'),
		RED('c'),
		LIGHT_PURPLE('d'),
		YELLOW('e'),
		WHITE('f'),
		OBFUSCATED('k'),
		BOLD('l'),
		STRIKETHROUGH('m'),
		UNDERLINE('n'),
		ITALIC('o'),
		RESET('r');

		private char formatCode;
		
		ChatFormat(char formatCode) {
			this.formatCode = formatCode;
		}
		
		public String format(String msg) {
			return "§"+this.formatCode + msg + "§r";
		}
		
		/**
		 * Converts the format to its {@code char} representation used by Minecraft in formatting texts.
		 * @return {@code char} value representing the character used for the format.
		 */
		public char getFormatCode() {return this.formatCode;}
		@Override
		public String toString() {return this.formatCode+"";}
	}
	
	/**
	 * Identifies what type of chat message is to send. Good for debugging while in-game.
	 * @author Virus5600
	 *
	 */
	public enum ChatType {
		NORMAL(new ChatFormat[]{ChatFormat.WHITE}),
		INFO(new ChatFormat[]{ChatFormat.DARK_AQUA}),
		WARNING(new ChatFormat[]{ChatFormat.YELLOW}),
		ERROR(new ChatFormat[]{ChatFormat.RED, ChatFormat.BOLD});
		
		private ChatFormat colorCode[];
		
		public String format(String msg) {
			String str = "";
			for (ChatFormat c : colorCode)
				str += "§"+c;
			return str+msg;
		}
		
		ChatType(ChatFormat colorCode[]) {
			this.colorCode = colorCode;
		}
	}
}