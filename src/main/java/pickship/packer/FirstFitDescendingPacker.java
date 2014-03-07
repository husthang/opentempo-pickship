package pickship.packer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by tfd on 3/6/14.
 */
public class FirstFitDescendingPacker implements Packer1D {

    /**
     * Generate an array of indexes corresponding to a descending sort of doubles.
     *
     * Sort an array of Doubles.  Return an array of indices, s.t. the
     * value at the first position corresponds to the index in doubles of
     * the first largest element in doubles, etc.
     *
     * For example, if doubles is [2.0, 3.0, 1.0, 4.0], the sorted array
     * would be [4.0, 3.0, 2.0, 1.0], and the returned indices would be [3, 1, 0, 2].
     * The first element of the index array contains the index (3) in doubles of
     * the largest element in doubles (4.0).
     *
     * @param doubles
     * @return An array of indices indicating the element in each position of the sorted array of Doubles.
     */
    private static Integer[] descendingSortIndices(final Double[] doubles) {
        // http://stackoverflow.com/questions/951848/java-array-sort-quick-way-to-get-a-sorted-list-of-indices-of-an-array

        // Array of indices, from 0 to N-1.
        Integer[] idx = new Integer[doubles.length];
        for (Integer i = 0; i < idx.length; i++) {
            idx[i] = i;
        }
        // Sort indices s.t. the first element
        Arrays.sort(idx, new Comparator<Integer>() {
            @Override
            public int compare(final Integer o1, final Integer o2) {
                return Double.compare(doubles[o2], doubles[o1]); // descending sort
            }
        });
        return idx;
    }

    /**
     * An implementation of the First Fit Descending bin packing algorithm.
     *
     * The First Fit Descending algorithm first sorts the items by descending size.
     * Then it assigns each item in turn to the first bin it will fit in.  If an item
     * fits in no bin, it assigns the item to a new bin.  If item i is assigned to bin j,
     * then the ith element of the return array will be j.
     *
     * See <a href="http://en.wikipedia.org/wiki/Bin_packing_problem">Wikipedia</a> for more details on the algorithm.
     *
     * @param items an array representing the weight/size/amount of each item.
     * @param capacity the maximum holding capacity of each bin.
     * @return an array of bin assignments for each item in items.
     * @throws Exception
     */
    @Override
    public Integer[] pack(Double[] items, Double capacity) throws Exception {
        // Assign each item to a bin
        Integer[] binIndices = new Integer[items.length];

        // Sort the indexes of items in descending order of the corresponding item size.
        Integer[] sorted = FirstFitDescendingPacker.descendingSortIndices(items);
        // Confirm that the sorted indices appears correct
        //System.err.println(Arrays.toString(items));
        //System.err.println(Arrays.toString(sorted));

        // Track amount in each bin
        List<Double> bins = new ArrayList<>();
        bins.add(0.0);

        // Add items to bins in descending size order.
        for (int i = 0; i < sorted.length; i++) {
            if (items[sorted[i]] > capacity) {
                throw new Exception("Error: Item size greater than bin capacity.");
            }

            // Find the first bin that will hold the item, if any.
            Boolean fitsInBin = false;
            Integer binIdx = -1;
            for (int j = 0; j < bins.size(); j++) {
                if (items[sorted[i]] <= capacity - bins.get(j)) {
                    binIdx = j;
                    fitsInBin = true;
                    break;
                }
            }

            // Assign item to bin, and increment the bin size.
            if (fitsInBin) {
                // assign the item to the bin
                binIndices[sorted[i]] = binIdx;
                // add the item size to the bin
                bins.set(binIdx, bins.get(binIdx) + items[sorted[i]]);
            } else {
                // assign the item to a new bin
                binIndices[sorted[i]] = bins.size();
                // add the item size to a new bin
                bins.add(items[sorted[i]]);
            }
        }
        return binIndices;
    }
}
