package main;

public class Word implements Comparable<Word> {
	public static String encode(String s) {
//		System.out.println("encoding "+s);
		int idx=0;
		int arr_idx=0;
		byte[] ret=new byte[s.length()];
		for(int i=0;i<ret.length;i++) {
			ret[i]=-1;
		}
		int num_removed_chars=0;
		while(idx<s.length()) {
			byte c=-1;
			//Try to encode as a single-char block
			Integer ri=Block.blocks_hm.get((int)s.charAt(idx));
			if(ri!=null) {
				//Single block encode success!
				idx++;
				c=(byte)((int)ri);
			}
			if(c==-1) {
				//Try to encode as a multi-char block
				for(int bi=0;bi<Block.multiBlocks.length;bi++) {
					String mb=Block.multiBlocks[bi];
					if(idx+mb.length()-1>=s.length()) {
						continue;
					}
					boolean a=true;
					for(int i=0;i<mb.length();i++) {
						if(mb.charAt(i)!=s.charAt(idx+i)) {
							a=false;
							break;
						}
					}
					if(a) {
						//Multi-block encode success!
						idx+=mb.length();
						num_removed_chars+=mb.length()-1;
						c=(byte)(Block.blocks.length+bi);
						break;
					}
				}
			}
			if(c==-1) {
				System.err.println("Failed to encode \""+s+"\" at index "+idx+".");
				System.exit(1);
			}
			ret[arr_idx]=(byte)-c;
			arr_idx++;
		}
		StringBuilder sb=new StringBuilder(s.length()-num_removed_chars);
		for(int i=0;i<s.length()-num_removed_chars;i++) {
			sb.append((char)ret[i]);
		}
		return sb.toString();
	}
	
	public static String decode(String s) {
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<s.length();i++) {
			try {
				sb.append(Block.masterDecode[-(byte)s.charAt(i)]);
			} catch(ArrayIndexOutOfBoundsException e) {
				System.out.println(-(byte)s.charAt(i));
				throw e;
			}
		}
		return sb.toString();
	}
	
	public Word(boolean encoded, String i) {
		if(encoded) {
			this.s=i;
		} else {
			this.s=Word.encode(i);
		}
	}
	
	public int compareTo_content(Word w) {
		for(int i=0;i<Math.min(w.s.length(),this.s.length());i++) {
			if(((int)w.s.charAt(i)) != ((int)this.s.charAt(i))) {
				if((-(byte)this.s.charAt(i)) > (-(byte)w.s.charAt(i))) {
					return 1;
				} else {
					return -1;
				}
			}
		}
		return 0;
	}
	
	@Override
	public int compareTo(Word w) {
		int ctc=compareTo_content(w);
		if(ctc!=0) {return ctc;}
		if(this.s.length()>w.s.length()) {
			return 1;
		}
		if(this.s.length()<w.s.length()) {
			return -1;
		}
		return 0;
	}
	
	public String s;
}
