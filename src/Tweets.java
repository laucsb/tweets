import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
/**
 * I use TreeMap to record the total number of times each word appears.
 * For each tweet, I use HashSet to calculate the number of unique words.
 * For median, I maintain two heaps to represent two halves of a ('sorted') array in ascending order when updating.
 * for the left half of the array, I use a max-heap, and a min-heap for the right half.
 * the size of the max-heap will either be equal to that of the min-heap, or greater exactly 1.
 * the median will either be the root of the max-heap, or the mean of the roots of the max-heap and min-heap. 
 * @author LiZheng
 *
 */
class Heap{
	//we maintain two heaps to represent two halves of an array, max-heap for left half, min-heap for right half.
	private ArrayList<Integer> heap;
	//flag decides max-heap or min-heap. 1 means max-heap, 2 means min-heap
	private int flag;	
	
	public Heap(int flag) {
		heap = new ArrayList<>();
		this.flag = flag;
	}
	
	public int getRoot(){
		return heap.get(0);
	}
	
	public int getSize(){
		return heap.size();
	}
	
	/**adjust a node in a heap, while the two children of this node are already heap*/
	public void downAdjust(int index, int flag){
		int lidx = 2 * index + 1;
		int ridx = 2 * index + 2;
		int curIdx = index;
		if (lidx <= heap.size() - 1){
			if (flag == 1 && heap.get(curIdx) < heap.get(lidx)){
				curIdx = lidx;
			}
			else if (flag == 2 && heap.get(curIdx) > heap.get(lidx)){
				curIdx = lidx;
			}
		}
		if (ridx <= heap.size() - 1){
			if (flag == 1 && heap.get(curIdx) < heap.get(ridx)){
				curIdx = ridx;
			}
			else if (flag == 2 && heap.get(curIdx) > heap.get(ridx)){
				curIdx = ridx;
			}
		}
		if (curIdx != index){
			int curValue = heap.get(index);
			int newValue = heap.get(curIdx);
			heap.set(index, newValue);
			heap.set(curIdx, curValue);
			downAdjust(curIdx, flag);
		}
	}
	
	/**adjust a node which has just been inserted into the heap*/
	public void upAdjust(int flag){
		int index = heap.size() - 1;
		while(true){
			if (index == 0){
				break;
			}
			int curValue = heap.get(index);
			int parentIndex = (index - 1) / 2;
			int parentValue = heap.get(parentIndex);
			if (flag == 1){
				if (curValue <= parentValue){
					break;
				}
			}
			else if (flag == 2){
				if (curValue >= parentValue){
					break;
				}
			}
			heap.set(index, parentValue);
			heap.set(parentIndex, curValue);
			index = parentIndex;
		}
	}
	
	/**insert a node to the heap*/
	public void insert(int value){
		heap.add(value);
		upAdjust(flag);
	}
	
	/**remove the root of a heap*/
	public void removeRoot(){
		heap.set(0, heap.get(heap.size() - 1));
		heap.remove(heap.size() - 1);
		downAdjust(0, flag);
	}
}

public class Tweets {
	private TreeMap<String, Integer> stats;//stats is used to record the total number of times each word appears
	private Heap maxHeap, minHeap;
	
	public Tweets(){
		stats = new TreeMap<>();
		maxHeap = new Heap(1);
		minHeap = new Heap(2);
	}
	
	/**process each tweet, record unique words, update stats*/
	public int processOneTweet(String tweet){
		HashSet<String> line = new HashSet<>();
		String words[] = tweet.split(" ");
		for (int i = 0; i < words.length; i++){
			if (!line.contains(words[i])){
				line.add(words[i]);
			}
			Integer value = stats.get(words[i]);
			if (value != null){
				stats.put(words[i], value + 1);	
			}
			else{
				stats.put(words[i], 1);
			}
		}
		return line.size();
	}

	/**update two heaps, maintain their sizes*/
	public void updateMediean(int unique){
		if (maxHeap.getSize() == 0){
			maxHeap.insert(unique);
			return;
		}
		if (maxHeap.getSize() == minHeap.getSize()){
			if (unique <= maxHeap.getRoot()){
				maxHeap.insert(unique);
			}
			else{
				minHeap.insert(unique);
				maxHeap.insert(minHeap.getRoot());
				minHeap.removeRoot();
			}
		}
		else if (maxHeap.getSize() == minHeap.getSize() + 1){
			if (unique >= maxHeap.getRoot()){
				minHeap.insert(unique);
			}
			else{
				maxHeap.insert(unique);
				minHeap.insert(maxHeap.getRoot());
				maxHeap.removeRoot();
			}
		}
	}
	
	/**write feature 1 to file*/
	public void writeFt1(BufferedWriter bw) throws Exception{
		for (Entry<String, Integer> entry : stats.entrySet()){
			String key = entry.getKey();
			Integer value = entry.getValue();
			bw.write(String.valueOf(key) + "\t");
			bw.write(String.valueOf(value) + "\n");
		}
	}
	
	/**write feature 2 to file*/
	public void writeFt2(BufferedWriter bw)throws Exception{
		DecimalFormat df = new DecimalFormat("0.00");
		double median = 0;
		if (maxHeap.getSize() == minHeap.getSize()){
			int md1 = maxHeap.getRoot();
			int md2 = minHeap.getRoot();
			median = ((double)md1 + (double)md2) / 2;
		}
		else{
			median = (double)(maxHeap.getRoot());
		}
		median = (int)(100 * median) / 100.0;
		bw.write(df.format(median) + "\n");
	}
	
	/**process whole tweet txt file*/
	public void processTweets(String inpath, String outpath1, String outpath2) throws Exception{
		File inputfile = new File(inpath);
		File outputfile1 = new File(outpath1);
		File outputfile2 = new File(outpath2);
		BufferedReader in = new BufferedReader(new FileReader(inputfile));
		BufferedWriter ft1 = new BufferedWriter(new FileWriter(outputfile1));
		BufferedWriter ft2 = new BufferedWriter(new FileWriter(outputfile2));
		while(true){
			String tweet = in.readLine();
			if (tweet == null){
				break;
			}
			else{
				int unique = processOneTweet(tweet);
				updateMediean(unique);
				writeFt2(ft2);
			}
		}
		writeFt1(ft1);
		in.close();
		ft1.close();
		ft2.close();
	}
	
	public static void main(String args[])throws Exception{
		Tweets tweets = new Tweets();
		tweets.processTweets(args[0], args[1], args[2]);
	}
}
