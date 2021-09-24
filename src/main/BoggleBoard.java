package main;

import java.util.Random;
import java.util.Scanner;

import lepton.optim.tensorlib.main.Tensor;

public class BoggleBoard {
	public static final int SIZE=5;
	private Random rand=new Random();	
	public Tensor<Block> board=new Tensor<Block>(2,new Block((byte)-1),SIZE,SIZE);
	private int weighted_random(int total, int[] wl) {
		int r=rand.nextInt(total-1)+1;
		for(int i=0;i<wl.length;i++) {
			r-=wl[i];
			if(r<=0) {
				return i;
			}
		}
		return -1;
	}
	public void generateRandom(WordList wl) {
		
		System.out.print("BoggleBoard.generateRandom: Generating occurrance-based board generation weights...   ");
		wl.generateOccurrences();
		System.out.println("done!");
		
		int total_occ=wl.occurrences[0];
		for(int i=1;i<wl.occurrences.length;i++) {
			total_occ+=wl.occurrences[i];
		}
		for(int i=0;i<SIZE;i++) {
			for(int j=0;j<SIZE;j++) {
				board.getElement(i,j).internal.block=(byte)weighted_random(total_occ,wl.occurrences);
			}
		}
	}
	public static Scanner in;
	public void getFromConsole() {
		if(in==null) {in=new Scanner(System.in);}
		for(int i=0;i<SIZE;i++) {
			String s=in.nextLine();
			if(s.equals("exit")) {
				System.out.println("exiting...");
				System.exit(0);
			}
			if(s.length()!=SIZE) {
				if(s.length()==Math.pow(SIZE,2)) {
//					System.out.println("Picked up thin mapping.");
					for(int j=0;j<s.length();j++) {
//						System.out.println(j);
						Integer n=Block.representations_hm.get((Integer)(int)s.charAt(j));
						if(n==null) {
							System.err.println("Incorrect character \""+Character.toString(s.charAt(j))+"\".");
							i--;
							break;
						}
						int a=j%SIZE;
						int b=(j-a)/SIZE;
						board.getElement(b,a).internal.block=(byte)(int)n;
					}
					if(i>=0) {
						break;
					}
				} else {
					System.err.println("Incorrect length.");
					i--;
				}
			} else {
				for(int j=0;j<SIZE;j++) {
					Integer n=Block.representations_hm.get((Integer)(int)s.charAt(j));
					if(n==null) {
						System.err.println("Incorrect character \""+Character.toString(s.charAt(j))+"\".");
						i--;
						break;
					}
					board.getElement(i,j).internal.block=(byte)(int)n;
				}
			}
		}
//		in.close();
		System.out.println("board initialized.");
	}
	public void clear() {
		for(int i=0;i<SIZE;i++) {
			for(int j=0;j<SIZE;j++) {
				board.getElement(i,j).internal.block=(byte)-1;
			}
		}
	}
	public void print() {
		for(int i=0;i<SIZE;i++) {
			for(int j=0;j<SIZE;j++) {
				System.out.print(Block.masterDecode[board.get(i,j).block]+", ");
			}
			System.out.println();
		}
	}
}
