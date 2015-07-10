===============================================================================================================
Algorithm:

1.  For calculating the total number of times each word has been tweeted,
I use a TreeMap in Java to do recording.

2.  For each tweet, I use a HashMap to record unique words.

3.  To update median when receiving a new tweet, the key problem is the identify
the maximal number of the left half and the minimal number of the right half of 
a sequence, considering the sequence is already ordered. 

    However, we do not need to keep the seuqnce ordered, we only need to know the exactly two values. 
So I maintain two heaps for updating median. max-heap for left half, min-heap for right half.
The size of the max-heap will either be equal to that of the min-heap, or greater exactly 1.
The median will either be the root of the max-heap, or the mean of the roots of the max-heap and min-heap. 

    Consider there are n tweets, the time complexity of updating is O(lgn) for each tweet, so the total time
complexity is O(nlgn). Actually, it should be O(lg1*lg2*...*lg(n/2)), which is O(lg(n/2)!), less than O(nlgn).

===============================================================================================================
Implementation:

I implement two classes, one class is Heap, which includes the heap representation of an array and related heap
inseart and delete operations. The other one is Tweets, which is used to do the other things, including read 
and write file, process data and other stuff.

===============================================================================================================
How to run the program?

With a Java environment(I develop the program under JavaSE-1.7), run the program with the provided script run.sh
by command 'source run.sh'.