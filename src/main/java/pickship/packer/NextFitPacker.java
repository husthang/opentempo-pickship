package pickship.packer;

/**
 * Created by tfd on 3/6/14.
 */
public class NextFitPacker implements Packer1D {

    @Override
    public Integer[] pack(Double[] items, Double capacity) throws Exception {
        Integer[] bins = new Integer[items.length];
        int currBin = 1; // index of the current bin
        double currSize = 0.0; // amount in current bin
        for (int i = 0; i < items.length; i++) {
            if (items[i] > capacity) {
                throw new Exception("Error: Item size greater than bin capacity.");
            } else if (items[i] <= capacity - currSize) {
                // assign item i to the current bin
                bins[i] = currBin;
                currSize += items[i];
            } else {
                // assign item i to a new bin
                currBin++;
                bins[i] = currBin;
                currSize = items[i];
            }
        }
        return bins;
    }
}
