package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lepton.util.advancedLogger.LogLevel;
import lepton.util.advancedLogger.Logger;

public class Main {
	public static final int SMALLEST_WORD=BoggleBoard.SIZE-1;
	public WordList words=new WordList();
	public BoggleBoard board=new BoggleBoard();
	public void Boggle() throws IOException {
		words.loadEncoded("words_encoded.txt");
		
		System.out.print("Generating level "+WordLocationCache.CACHE_LEVELS+" location cache with "+(int)Math.pow(Block.masterDecode.length,WordLocationCache.CACHE_LEVELS)+" entries...   ");
		words.generateLocationCache();
		System.out.println("done!");
		
//		System.out.println(words.partiallyExists(false, "gxk", null));
		
//		System.exit(0);
		
		while(true) {
			board.clear();
			System.out.println("Enter board state.");
			board.getFromConsole();
//			board.generateRandom(words);
			
			System.out.println("Confirming board state:");
			board.print();
			System.out.println();
			
			StatisticsObject so=new StatisticsObject();
			
			System.out.print("Running word search...   ");
			Logger.levels[0]=new LogLevel("DEBUG",false,false,false);
			long t1=System.nanoTime();
			for(int i=0;i<BoggleBoard.SIZE;i++) {
				for(int j=0;j<BoggleBoard.SIZE;j++) {
					WordNode startNode=WordNode.WordNodePool.alloc().o();
					startNode.block=board.board.getElement(i,j);
					startNode.pathString+=Character.toString((char)(byte)-board.board.getElement(i,j).internal.block);
					startNode.prevLocations.add(board.board.getElement(i,j));
					startNode.search(board,words,so,0);
				}
			}
			long t=System.nanoTime()-t1;
			System.out.println("done in "+(double)t/1000000.0+"ms! Ran "+so.numSearches+" binary searches with "+so.numSearchIterations[0]+" total iterations.");
			List<String> encodedResults=so.encodedResults.stream().map((s)->new Word(true, s)).sorted().map((s)->s.s).collect(Collectors.toList());
			
			ArrayList<Integer> toDelete=new ArrayList<Integer>();
			for(int i=1;i<encodedResults.size();i++) {
				if(encodedResults.get(i-1).equals(encodedResults.get(i))) {
					toDelete.add(i);
				}
			}
			for(int i=toDelete.size()-1;i>=0;i--) {
				encodedResults.remove((int)toDelete.get(i));
			}
			
			List<String> results=encodedResults.stream().map(Word::decode).collect(Collectors.toList());
			ArrayList<String> toRemove=new ArrayList<String>();
			for(String result : results) {
				if(result.length()<SMALLEST_WORD) {
					toRemove.add(result);
				}
			}
			for(String s : toRemove) {
				results.remove(s);
			}
			
			String longest="";
			int score=0;
			for(String result : results) {
				if(result.length()>longest.length()) {
					longest=result;
				}
				score+=result.length()-SMALLEST_WORD+1;
			}
			for(int id=0;id<results.size();id++) {
				System.out.print(results.get(id)+" ");
				for(int i=0;i<longest.length()-results.get(id).length();i++) {
					System.out.print(" ");
				}
				if(id%15==0) {
					System.out.println();
				}
			}
			System.out.println();
			System.out.println("Longest word: "+longest);
			System.out.println("Number of words: "+results.size());
			System.out.println("SCORE: "+score);
			System.out.println();
			System.out.println();
//			break;
		}
	}
	public static void main(String[] args) throws Exception {
		Main m=new Main();
		System.out.print("Preprocessing wordlists...   ");
		WordList.preprocessWords();
		System.out.println("done!");
		m.Boggle();
	}
}
