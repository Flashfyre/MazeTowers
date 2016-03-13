package com.samuel.mazetowers.etc;

public interface IVendorTradeable {
	
	int tradeChance = 1000; // 1000 = 100%, 500 = 50%, 0 = 0%, etc.
	int professionId = -1; // -1 = None, 1 = Engineer, 3 = Locksmith
	
	public int getVendorTradeChance(int difficulty);

	int getVendorProfessionId();
}
