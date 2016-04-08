package com.samuel.mazetowers.etc;

public interface IVendorTradable {
	
	int tradeChance = 1000, // 1000 = 100%, 500 = 50%, 0 = 0%, etc.
	professionId = -1, // -1 = None, 1 = Engineer, 3 = Locksmith
	minTradeLevel = 0,
	maxTradeLevel = 9,
	minTradeChance = 1000,
	maxTradeChance = 1000,
	minPrice = 1,
	maxPrice = 1,
	tradeLevelDiff = 9;
	
	public int getVendorTradeChance(int difficulty);

	public int getVendorProfessionId();
}
