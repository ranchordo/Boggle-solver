package main;

import java.util.ArrayList;

import lepton.optim.objpoollib.AbstractObjectPool;
import lepton.optim.objpoollib.PoolElement;
import lepton.optim.objpoollib.PoolInitCreator_clone;
import lepton.optim.tensorlib.main.Tensor;
import lepton.optim.tensorlib.main.TensorElement;

public class WordNode implements Cloneable {
	
	public static AbstractObjectPool<WordNode> WordNodePool=new AbstractObjectPool<WordNode>("WordNode", new PoolInitCreator_clone<WordNode>(new WordNode())) {
		@Override public PoolElement<WordNode> alloc() {
			PoolElement<WordNode> ret=super.alloc();
			ret.o().reset();
			ret.o().THIS=ret;
			return ret;
		}
	};
	
	public ArrayList<WordNode> paths=new ArrayList<WordNode>();
	public TensorElement<Block> block;
	public String pathString="";
	ArrayList<TensorElement<Block>> prevLocations=new ArrayList<TensorElement<Block>>();
	public PoolElement<WordNode> THIS;

	private <T extends Cloneable> TensorElement<T> getElementBounds(int a, int b, Tensor<T> board) {
		if(a<0 || a>=board.dim[0] || b<0 || b>=board.dim[1]) {
			return null;
		}
		return board.getElement(a,b);
	}
	public void search(BoggleBoard board, WordList list, StatisticsObject so, int depth) {
//		System.out.println("Depth: "+depth);
		if(list.exists(true,pathString, so.numSearchIterations)) {
			so.encodedResults.add(pathString+"");
		}
		@SuppressWarnings("unchecked") TensorElement<Block>[] moves=(TensorElement<Block>[]) new TensorElement[8];
		moves[0]=getElementBounds(this.block.pos[0]-1, this.block.pos[1]-1, board.board);
		moves[1]=getElementBounds(this.block.pos[0]-1, this.block.pos[1]+0, board.board);
		moves[2]=getElementBounds(this.block.pos[0]-1, this.block.pos[1]+1, board.board);
		moves[3]=getElementBounds(this.block.pos[0]+0, this.block.pos[1]-1, board.board);
		moves[4]=getElementBounds(this.block.pos[0]+0, this.block.pos[1]+1, board.board);
		moves[5]=getElementBounds(this.block.pos[0]+1, this.block.pos[1]-1, board.board);
		moves[6]=getElementBounds(this.block.pos[0]+1, this.block.pos[1]+0, board.board);
		moves[7]=getElementBounds(this.block.pos[0]+1, this.block.pos[1]+1, board.board);
		for(int i=0;i<moves.length;i++) {
			if(moves[i]==null) {continue;}
			if(prevLocations.contains(moves[i])) {
				moves[i]=null;
			} else {
//				Word.decode(pathString+Character.toString((char)(byte)-moves[i].internal.block));
				so.numSearches++;
				if(!list.partiallyExists(true,pathString+Character.toString((char)(byte)-moves[i].internal.block),so.numSearchIterations)) {
					moves[i]=null;
				}
			}
		}
		for(int i=0;i<moves.length;i++) {
			if(moves[i]!=null) {
				PoolElement<WordNode> np=WordNodePool.alloc();
				WordNode n=np.o();
				this.paths.add(n);
				n.pathString=this.pathString+Character.toString((char)(byte)-moves[i].internal.block);
				n.block=moves[i];
				n.prevLocations.addAll(this.prevLocations);
				n.prevLocations.add(moves[i]);
				n.search(board,list,so,depth+1);
			}
		}
		THIS.free();
		reset();
	}
	
	@Override
	public Object clone() {
		WordNode ret=new WordNode();
		ret.paths.addAll(this.paths);
		ret.block=this.block;
		ret.prevLocations.addAll(this.prevLocations);
		ret.pathString=this.pathString+"";
		return ret;
	}
	
	public void reset() {
		paths.clear();
		block=null;
		prevLocations.clear();
		pathString="";
		THIS=null;
	}
	
	public void freeRecursive() {
		THIS.free();
		reset();
		for(WordNode wn : paths) {
			wn.freeRecursive();
		}
	}
}