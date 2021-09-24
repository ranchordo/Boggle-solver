package main;

import java.util.HashMap;

public class WordLocationCache {
	public static final int CACHE_LEVELS=2;
	public static class WordLocationCacheEntry {
		public byte[] location=new byte[CACHE_LEVELS];
		@Override
		public int hashCode() {
			int ret=0;
			for(int i=0;i<location.length;i++) {
				ret+=(location[i])>>((5*i)%(32-5));
			}
			return ret;
		}
		@Override
		public boolean equals(Object a) {
			WordLocationCacheEntry w=(WordLocationCacheEntry) a;
			boolean equal=true;
			for(int i=0;i<CACHE_LEVELS;i++) {
				if(w.location[i]!=this.location[i]) {
					equal=false;
					break;
				}
			}
			return equal;
		}
	}
	public HashMap<WordLocationCacheEntry,Integer> cache=new HashMap<WordLocationCacheEntry,Integer>();
}
