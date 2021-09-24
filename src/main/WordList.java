package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class WordList {
	public List<Word> words;
	public int[] occurrences=new int[Block.masterDecode.length];
	public WordLocationCache wlc=new WordLocationCache();
	public WordLocationCache.WordLocationCacheEntry requestEntry=new WordLocationCache.WordLocationCacheEntry();
	public WordList loadEncoded(String fname) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(fname));
		words=br.lines().map((s)->new Word(true,s)).collect(Collectors.toList());
		br.close();
		return this;
	}
	public static void preprocessWords() throws IOException {
		BufferedReader br=new BufferedReader(new FileReader("words.txt"));
		List<Word> words=br.lines().map((s)->new Word(false,s)).sorted().collect(Collectors.toList());
		br.close();
		
		FileWriter fw=new FileWriter("words_encoded.txt");
		for(Word word : words) {
			fw.write(word.s+"\n");
		}
		fw.close();
	}
	
	public void generateOccurrences() {
		for(int i=0;i<occurrences.length;i++) {
			occurrences[i]=0;
		}
		for(Word w : words) {
			for(int i=0;i<w.s.length();i++) {
				occurrences[-(byte)w.s.charAt(i)]+=1;
			}
		}
	}
	public void generateLocationCache() {
		byte[] location=new byte[WordLocationCache.CACHE_LEVELS];
		int prevIndex=0;
		for(int i=0;i<Math.pow(Block.masterDecode.length,WordLocationCache.CACHE_LEVELS);i++) {
			int i2=i;
			for(int j=0;j<WordLocationCache.CACHE_LEVELS;j++) {
				location[j]=(byte)(i2%Block.masterDecode.length);
				i2-=location[j];
				i2/=Block.masterDecode.length;
			}
			StringBuilder sb=new StringBuilder(WordLocationCache.CACHE_LEVELS);
			for(int j=0;j<WordLocationCache.CACHE_LEVELS;j++) {
				sb.append((char)-location[j]);
			}
			int idx=search_raw(true,sb.toString(),prevIndex,words.size()-1, null);
			WordLocationCache.WordLocationCacheEntry entry=new WordLocationCache.WordLocationCacheEntry();
			for(int j=0;j<WordLocationCache.CACHE_LEVELS;j++) {
				entry.location[j]=location[j];
			}
			wlc.cache.put(entry,idx);
			//prevIndex=idx;
		}
	}
	public int getNumStarting(boolean encoded, String ss, int[] st) {
		String ssl=encoded?ss:Word.encode(ss);
		StringBuilder ssu=new StringBuilder(ssl.length());
		for(int i=0;i<ssl.length();i++) {
			ssu.append((char)(byte)Math.max(-(Block.masterDecode.length-1),(((byte)ssl.charAt(i))-(i==ssl.length()-1?1:0))));
		}
//		System.out.println(Word.decode(ssl));
//		System.out.println(Word.decode(ssu.toString()));
		return getNumInRange(true, ssl, ssu.toString(), st);
	}
	public boolean partiallyExists(boolean encoded, String ss, int[] st) {
		Word s=new Word(encoded, ss);
		int c=words.get(search(encoded, ss, st)).compareTo_content(s);
		return c==0;
	}
	public int getNumInRange(boolean encoded, String ssl, String ssu, int[] st) {
		return Math.abs(search(encoded, ssu, st)-search(encoded, ssl, st));
	}
	public boolean exists(boolean encoded, String ss, int[] st) {
		Word s=new Word(encoded, ss);
		int c=words.get(search(encoded, ss, st)).compareTo(s);
		return c==0;
	}
	@SuppressWarnings("unused")
	public int search(boolean encoded, String sss, int[] st) {
//		System.out.println(encoded+":"+sss);
		String ss=encoded?sss:Word.encode(sss);
		if(ss.length()<WordLocationCache.CACHE_LEVELS) {
			for(int i=0;i<WordLocationCache.CACHE_LEVELS;i++) {
				requestEntry.location[i]=0;
			}
		}
		for(int i=0;i<Math.min(WordLocationCache.CACHE_LEVELS,ss.length());i++) {
			requestEntry.location[i]=(byte)-(byte)ss.charAt(i);
		}
		int lower=0;
		int upper=words.size()-1;
		if(WordLocationCache.CACHE_LEVELS>0) {
			lower=wlc.cache.get(requestEntry);
			requestEntry.location[Math.min(WordLocationCache.CACHE_LEVELS,ss.length())-1]=
					(byte)Math.min(Block.masterDecode.length-1,1+requestEntry.location[Math.min(WordLocationCache.CACHE_LEVELS,ss.length())-1]);
			upper=wlc.cache.get(requestEntry);
		}
		return search_raw(true, ss, lower, upper, st);
	}
	
	public int search_raw(boolean encoded, String ss, int l, int u, int[] st) {
//		System.out.println(":"+ss+":"+encoded);
		int lower=l;
		int upper=u;
		int middle=(lower+upper)/2;
		Word s=new Word(encoded,ss);
		while(true) {
			int c=words.get(middle).compareTo(s);
			if(st!=null) {
				if(st.length==1) {st[0]++;}
			}
//			System.out.println(lower+"    "+upper+", "+c);
			if(c>-1) {
				upper=middle;
			} else if (c==-1) {
				lower=middle;
			}
			if(upper-lower<=1) {
				return upper;
			}
			middle=(lower+upper)/2;
		}
	}
}
