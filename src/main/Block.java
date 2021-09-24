package main;

import java.util.HashMap;

public class Block implements Cloneable {
	public static final char[] blocks=new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','r','s','t','u','v','w','x','y','z'};
	public static final String[] multiBlocks=new String[] {"qu"};
	public static final char[] representations=new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','r','s','t','u','v','w','x','y','z','q'};
	public static final HashMap<Integer,Integer> blocks_hm=createBlocksHashMap(blocks);
	public static final HashMap<Integer,Integer> representations_hm=createBlocksHashMap(representations);
	public static final String[] masterDecode=createMasterDecodeString(blocks,multiBlocks);
	public static final float[] weights=new float[] {};
	
	public static HashMap<Integer,Integer> createBlocksHashMap(char[] blocks) {
		HashMap<Integer,Integer> hm=new HashMap<Integer,Integer>();
		for(int i=0;i<blocks.length;i++) {hm.put((int)blocks[i],i);}
		return hm;
	}
	
	public static String[] createMasterDecodeString(char[] singles, String[] multis) {
		String[] ret=new String[singles.length+multis.length];
		for(int i=0;i<singles.length;i++) {
			ret[i]=Character.toString(singles[i]);
		}
		for(int i=0;i<multis.length;i++) {
			ret[i+singles.length]=multis[i];
		}
		return ret;
	}
	
	public Block(byte b) {
		this.block=b;
	}
	
	@Override
	public Object clone() {
		return new Block(this.block);
	}
	
	public byte block;
}
