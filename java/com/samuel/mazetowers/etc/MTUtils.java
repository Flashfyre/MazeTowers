package com.samuel.mazetowers.etc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import scala.Char;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class MTUtils {
	
	private static IBlockState air = Blocks.air.getDefaultState();
	private static final int minY = 49;
	
	public static int getSurfaceY(World world, int x, int z, int range) {
		int cy = minY;
		boolean nextY = true;
		
		for (; cy < 127; cy++) {
			nextY = false;
			for (int cz = z - range; cz <= z + range; cz++) {
				for (int cx = x - range; cx <= x + range; cx++) {
					BlockPos cpos = new BlockPos(cx, cy, cz);
					Block cBlock = null;
					IBlockState cstate = world.getBlockState(cpos);
					if (cstate != air && (cBlock = cstate.getBlock()) != Blocks.leaves && 
						cBlock != Blocks.leaves2 && cBlock != Blocks.log) {
						nextY = true;
						break;
					}
				}
				
				if (nextY)
					break;
			}
			
			if (!nextY)
				break;
		}
		
		return cy;
	}
	
	public static int getGroundY(World world, int x, int y, int z, int range) {
		int cy = y - 1;
		boolean nextY = true;
		for (; cy >= minY; cy--) {
			nextY = false;
			for (int cz = z - range; cz <= z + range; cz++) {
				for (int cx = x - range; cx <= x + range; cx++) {
					BlockPos cpos = new BlockPos(cx, cy, cz);
					Block cBlock = null;
					IBlockState cstate = world.getBlockState(cpos);
					if (cstate == air || (cBlock = cstate.getBlock()) == Blocks.leaves ||
						cBlock == Blocks.leaves2 || cBlock == Blocks.log ||
						cBlock == Blocks.water) {
						nextY = true;
						break;
					}
				}
				
				if (nextY)
					break;
			}
			
			if (!nextY)
				break;
		}
		
		return cy;
	}

	public static ItemStack getEnchantmentBookById(int index, int level) {
		ItemStack stack = new ItemStack(Items.enchanted_book);
		stack.addEnchantment(Enchantment.getEnchantmentById(index), level);
		return stack;
	}
	
	public static String getEncodedItemName(String itemName, Random rand) {
		int charCode;
		StringBuilder encodedName = new StringBuilder();
		List<Integer> switchableChars = new ArrayList<Integer>();
		Iterator iterator;
		List<Integer> charPos = new ArrayList<Integer>();
		List<Integer> charPosTemp;
		List<Integer> charPosShuffled = new ArrayList<Integer>();
		
		itemName = itemName.toLowerCase();
		
		for (int c = 0; c < itemName.length(); c++) {
			charCode = Char.char2int(itemName.charAt(c));
			if (charCode >= 97 && charCode <= 122) {
				charPos.add(c);
				switchableChars.add(c);
			}
		}
		
		charPosTemp = new ArrayList<Integer>(charPos);
		
		while (!charPos.isEmpty()) {
			int charIndex = rand.nextInt(charPos.size());
			charPosShuffled.add(charPos.get(charIndex));
			charPos.remove(charIndex);
		}
		
		charPos = charPosTemp;
		
		int charCount = 0;
		int charIndex;
		char lastChar = ' ';
		
		iterator = switchableChars.iterator();
		while (iterator.hasNext()) {

			while (charPos.get(charCount).intValue() != charCount) {
				charPos.add(charCount, -1);
				charPosShuffled.add(charCount, -1);
				encodedName.append(lastChar = itemName.charAt(charCount++));
			}
			
			char tempChar = itemName.charAt(charPosShuffled.get((charIndex = (Integer) iterator.next())));
			
			if (lastChar == ' ')
				tempChar = Character.toUpperCase(tempChar);
					
			encodedName.append(tempChar);
			lastChar = tempChar;
			charCount++;
		}
		
		return encodedName.toString();
	}
}
