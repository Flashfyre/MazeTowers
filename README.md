# Maze Towers Mod

## Description
This is a Minecraft Forge mod that spawns randomly generated maze towers into your world. With this mod, when a chunk is populated, there is a chance that a 16x16 structure will generate with an entrance on the surface. These structures contain narrow passages that split into other passages and contain obstacles such as doors, arrow-shooting dispensers, and even traps that make you fall through the floor.

## Maze Tower Types
Maze Towers are made of different blocks depending on the biome and dimension and there are 20 unique types in total: Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Sandstone, Red Sandstone, Cobblestone, Stone Brick, Ice, Red Mushroom, Brown Mushroom, Nether Brick, Quartz, Prismarine (filled with water), End Stone Brick, Obsidian, Purpur, and Bedrock.

## Chest Room
Each Maze Tower has a Chest Room with 4 chests as its final floor. This room can only be accessed by opening the Lock of the final door of the previous floor with the same coloured key. There is an exit in the Chest Room that brings you all the way back to the tower entrance, but this exit has no return.

## Locks and Keys
Each Maze Tower has a Lock on the door to the Chest Room and a random chest somewhere in each tower has one Key of its tower type. If this Key is destroyed, you can trade a Locksmith shopkeeper (in a tower of the same type) for another one with an emerald. Details of Locks and Keys are explained in the Custom Blocks and Items section.

## Level and Loot Tiers
All towers are rated with a tier from D to S+ for both Level (difficulty) and Loot (loot rarity) and these tiers increase every 5 floors with a maximum difference of 3 from the base tier (eg. D to C+). A higher Level tier means obstacles will be more dangerous and mob spawners will spawn stronger mobs more often. A higher Level tier means there's also a better chance of a tower having more floors. A higher Loot tier means chests will contain rarer items. There are also other effects of tiers that will be explained in other sections. The base Level and Loot tiers for the different Maze Tower Types are as follows (format: \<TYPE\> \<Level\>/\<Loot\>):

Oak D/D, Spruce D/D+, Birch D+/D+, Jungle D+/D+, Acacia D+/C, Dark Oak C/C, Sandstone C/C, Red Sandstone C/C+, Cobblestone C+/C+, Stone Brick C+/B, Ice C+/B+, Red Mushroom C+/B+, Brown Mushroom C+/B+, Nether Brick B/C+, Quartz B/B, Prismarine B/B+, End Stone Brick B+/B, Obsidian B+/B+, Purpur A/B+, Bedrock A/A

## Mini Towers
Maze Towers usually have smaller, external towers or rooms that come out of its sides that I call "Mini Towers" for lack of a better name. Mini Towers with multiple floors often contain monster spawners (frequency and entity type depend on Level tier) on floors other than the top floor. The top room (or only room for single floored Mini Towers) is carpeted and can contain either a chest or shopkeeper villager. There are 3 possible carpet colours for each tower type where the first is common, the second is uncommon, and the third is rare. If the room has a chest, the chest rarity will increase one or two levels on top of the Loot tier depending on the carpet colour. If the room has a shopkeeper, the Level tier, and thus their trades, will also be affected in the same way. Mini Towers have a small chance of having a connection to another Mini Tower, and this connection is sometimes obscured by a bookshelf so breaking at least all the middle bookshelves of each Mini Tower is recommended. Mini Tower entrances will sometimes only be open during the day or during the night, so keep this in mind when entering one. There is a functioning beacon in the highest Mini Tower of each upward Maze Tower, where its beam colour corresponds to the tower type, but neither the beacon nor the minerals underneath may be broken outside of creative mode as long as Block Protection is enabled.

## Shopkeepers
Mini Towers often contain shopkeeper villagers that you can trade items with like regular villagers. Within Maze Towers, there are two additional villager types, Engineers and Locksmiths, that trade only blocks and items exclusive to this mod and their tradable items are affected by both random chance and Level tier. Shops are usually closed during the night and open during the day, but there is a random chance of this being reversed.

## Upward (Normal) and Underground Maze Towers
On top of tower types, certain tower types also have a chance of generating an Underground Maze Tower where the goal is to go deeper instead of higher. Every floor exit is one-way and one should be cautious to explore an entire floor before exiting. While upward Maze Towers have a maximum floor of 21F (including the Chest Room), Underground Maze Towers have a minimum floor of B10F. An underground tower also adds 2 levels to its base Level tier and 1 level to its base Loot tier, making them harder with a better chance of loot but no chance of the Loot tier of floors 16F to 21F of an upward Maze Tower.

## Exiting Underground Maze Towers
Underground towers also have a different kind of one-way exit from the Chest Room where a block blocks the way up the ladder that leads out. To get out you must press the nearby button, which makes a piston move the block, and place a ladder (released from the dispenser below the button) in the empty space that appears on the wall. After a short period of time, a piston will push the block back into place and you will not be able to return as long as Block Protection is enabled (unless you are in creative mode). Be careful not to wait too long or the piston may push the block into you and possibly kill you after all that hard work.

## Block Protection
By default, for the sake of balanced gameplay, most blocks in Maze Towers quickly reappear after being broken by a player. Protected blocks are also unaffected by explosions, but still allow explosions to affect unprotected blocks through them as if they were air. Any block that is not meant to be easily obtained or would provide an unintended entrance or exit is protected in this way. After enough gameplay, it should come naturally which blocks are protected and which are not. Examples of unprotected blocks are torches, levers, window panes, chests, and carpets. Block protection can be disabled in the mod's settings but this is highly discouraged as players will be able to skip floors of Maze Towers and obtain blocks such as beacons and diamond blocks far too easily.

## Custom Blocks and Items
This mob contains many additional blocks and items to enhance the gameplay experience:

### Extra Walls **(Compatible with Texture Packs)**
Walls made from Vanilla blocks that don't yet have their own wall type. There are walls for the following blocks: sandstone, red sandstone, stone brick, mossy stone brick, packed ice, prismarine bricks, quartz block, end stone, purpur block, obsidian, and bedrock.

### Extra Stairs **(Compatible with Texture Packs)**
Walls made from Vanilla blocks that don't yet have their own stair type. There are stairs for the following blocks: packed ice, prismarine bricks, end stone bricks, obsidian and bedrock.

### Extra Doors
Doors made from Vanilla blocks that don't yet have their own door type. There are doors for the following blocks: prismarine bricks, quartz block, end stone bricks, purpur block, obsidian, and bedrock.

### Iron Chest
A chest with an iron texture. In Maze Towers, these contain items with a loot tier from C to C+. They can also be smelted into iron ingots **[NOT YET IMPLEMENTED]**.

### Gold Chest
A chest with an gold texture. In Maze Towers, these contain items with a loot tier from B to B+. They can also be smelted into gold ingots **[NOT YET IMPLEMENTED]**.

### Diamond Chest
A chest with an diamond texture. In Maze Towers, these contain items with a loot tier from A to A+. They can also be smelted into diamonds **[NOT YET IMPLEMENTED]**.

### Spectrite Chest
A chest with a spectrite texture. In Maze Towers, these contain items with a loot tier from S to S+. They can also be smelted into spectrite gems **[NOT YET IMPLEMENTED]**.

### Spectrite Ore
An extremely rare ore that can be found in the same place as diamonds in the surface world, though significantly rarer, but can also be found more commonly in the Nether and The End. 

### Spectrite Gem
An extremely rare mineral that can be acquired by mining Spectrite Ore or in Maze Tower chests of loot tiers A+ (extremely rare) to S+ (still very rare).

### Lock
A block that can be placed on upper halves of doors. This prevents doors from being opened or broken **[NOT YET IMPLEMENTED]**. There are 20 colour variants with one unique colour per Maze Tower type and only key items of the same colour may be used to open a lock so use them carefully. These can be acquired by trading a Locksmith shopkeeper for an emerald.

### Key
An item that removes lock blocks. There are 20 colour variants with one unique colour per Maze Tower type and only locks of the same colour may be opened. These can be acquired by trading a Locksmith shopkeeper for an emerald.

### Spectrite Key
A rainbow-coloured key that can open every type of door.

### Diamond Rod
A stick of diamond that is currently only used to make a Spectrite Pickaxe.

### RAM Stick
A material item used in crafting **[RECIPES NOT YET IMPLEMENTED]**.

### Hidden Button **(Compatible with Texture Packs)**
A button that assumes the texture of the block it's placed on. These can be acquired by trading an Engineer shopkeeper for emeralds.

### Hidden Pressure Plate **(Compatible with Texture Packs)**
A pressure plate that assumes the texture of the block under it. These can be acquired by trading an Engineer shopkeeper for emeralds. For the sake of gameplay, if used inside a Maze Tower, Hidden Pressure Plates cannot be powered unless there is a player on the same tower floor.

### Memory Piston
A special piston marked with a capital 'M' on the sides that, while on (head and 'M' are coloured green), works like a sticky piston but remembers how many blocks it has pushed and pulls the same number of blocks back upon retraction, making it easier to make self-resetting redstone mechanisms. It has also been modified from the normal piston to allow entities to fall through when retracting, allowing for one-way downward entrances and exits. These can be acquired by trading an Engineer shopkeeper for emeralds. Memory Pistons can also be turned off (head and 'M' are coloured blue) by toggling the block while in its retracted state. While off, a Memory Piston works almost the same as a normal piston.

### Item Scanner
A wall-mounted block that provides redstone power for a longer period than buttons or pressure plates. It contains an inventory and a slot for a "key" item that items will be compared to when the scanner is used by players other than the placer of the Item Scanner. If a scanned item matches, it will provide power, otherwise it will tell the player that the item was not a match. These can be acquired by trading an Engineer shopkeeper for emeralds.

### Gold Item Scanner
An Item Scanner made of gold that takes an item from a player if it matches a key item. This allows for toll booth mechanics. Like regular Item Scanners, these can be acquired by trading an Engineer shopkeeper for emeralds.

### Redstone Clock
A special clock item placeable as a block that works like a daylight sensor except according to time instead of light. The Redstone Clock's power output is highest during the day and lowest during the night. These can be acquired by trading an Engineer shopkeeper for emeralds.

### Inverse Redstone Clock
A slightly darker Redstone Clock with a power output highest during the night and lowest during the day. Like regular Redstone Clocks, these can be acquired by trading an Engineer shopkeeper for emeralds.

### Chaotic Sludge Bucket
A bucket containing Chaotic Sludge. Chaotic Sludge is a custom liquid sometimes found in fall traps in Maze Towers floors with Level tiers of A+ or greater. When submerged in Chaotic Sludge, players will contract bad potion effects that worsen with prolonged exposure.

### Spectrite Sword
A ridiculously powerful sword made of diamond and spectrite. Creates an explosion when attacking an entity that hits nearby entities but has no affect on blocks nor allies.

### Spectrite Key Sword
A Spectrite Sword that doubles as a Spectrite Key and therefore can be used to open all types of Maze Tower doors.

### Explosive Arrow
A special arrow that explodes shortly after hitting a block or instantly after hitting an entity. These may only be fired from an Explosive Bow. They can be crafted with a fire charge on top of an arrow on top of gunpowrder. These will probably be dropped by Explosive Creepers eventually but currently are not.

### Explosive Bow
A special bow that can shoot Explosive Arrows. There is a slim, random chance that increases with the Level tier of Maze Towers that an item frame will contain an Explosive Bow.

### Explosive Creeper Head
A wearable head of an Explosive Creeper that provides the same protection as iron and gold helmets but without breakability. When worn, regular arrows fired from an Explosive Bow become Explosive Arrows. These can be obtained as a rare drop from Explosive Creepers.

### Spectrite Armour (Helmet, Chestplate, Leggings, Boots)
A set of armour made of spectrite. It's just as protective as diamond but lasts a lot longer.

## Special Purpose Custom Blocks (Not Obtainable without Commands)

### Special Monster Spawner
A modified monster spawner used in MazeTowers to prevent mobs from spawning in the inaccessible medians between ceilings and floors and sometimes to spawn modified mobs (ie. charged creeper). This block is not intended to be used for any other purpose.

### Vendor Spawner
A special block that cannot normally be obtained that handles Maze Tower shopkeepers. This block is not intended to be used for any other purpose.

## Custom Entities

### Villager Vendor
A custom villager that sometimes appears as a Maze Tower shopkeeper. They can currently be either an Engineer or Locksmith and each has a different set of items that may be sold where the set can change with the Level tier. There is usually a Locksmith located at the same floor as the locked door that leads to the Chest Room.

### Explosive Creeper
A special creeper with the explosive power of a charged normal creeper. When charged, their explosive power is even higher. They can only be found in high Level tiers.

## What Still Needs to Be Done?

* Chest loot list needs to be completed
* Possible additional features may be added depending on how much they enhance the gameplay
* Block protection data corruption bug needs to be fixed