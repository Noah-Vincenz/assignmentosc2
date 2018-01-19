import java.util.LinkedList;

public class MemoryManagementUnit {
    /** The page table */
    private PageTableEntry[] pageTable;

    /** How many bits of logical memory addresses are used for the offset */
    private int bitsForOffset;

    /** A bitmask, where the bits for the offset are set to 1; and everything else is 0 */
    private int offsetMask;

    /** A bitmask, where the bits for the page number are set to 1; and everything else is 0 */
    private int pageNumberMask;

    /** The free frames list */
    private LinkedList<Integer> freeFrames;

    private int counter;

    /** Create an MMU
     *
     * @param pageTableSize  The number of entries in the page table
     * @param bitsForOffset  How many bits of logical memory addresses are used for the offset.  The rest are assumed to be the page number.
     */
    public MemoryManagementUnit(int pageTableSize, int bitsForOffset) {
        counter = 0;
        this.bitsForOffset = bitsForOffset;

        offsetMask = 0;
        for (int o = 0; o < bitsForOffset; ++o) {
            offsetMask = offsetMask | (1 << o);
        }

        pageNumberMask = ~offsetMask;


        pageTable = new PageTableEntry[pageTableSize];

        // Initialise all the page table entries to invalid, by making default page table entries
        for (int i = 0; i < pageTableSize; ++i) {
            pageTable[i] = new PageTableEntry();
        }

        freeFrames = new LinkedList<>();

        // assume there are half as many frames, as there are pages
        for (int i = 0; i < (pageTableSize / 2); ++i) {
            freeFrames.addLast((2 << bitsForOffset) * i);
        }

    }

    /** Access a memory address; returning true if there was a page fault */
    public boolean accessMemoryAddress(int address) {
        int offset = address & offsetMask;
        int pageNumber = (address & pageNumberMask) >> bitsForOffset;

        //System.out.println("Accessing memory with page " + pageNumber + ", offset " + offset);

        if (pageTable[pageNumber].getValidBit()) {
            // no page fault, woot
            // set the reference bit
            pageTable[pageNumber].setReferenceBit(true);
            return false; // no page fault - return false
        } else {
            pageFaultHandler(pageNumber);
            return true; // page fault - return true
        }
    }

    /** An implementation of the second-chance page-fault handler algorithm
     *
     * Your task is to implement this
     *
     * @param pageNumber
     */
      private void pageFaultHandler(int pageNumber) {
          // TODO: your code goes here
          // Implement the second-chance page-fault handler algorithm
          // (Don't forget to check if there is a free frame)
          if (freeFrames.isEmpty()) {
            PageTableEntry pte = new PageTableEntry(freeFrames.get(0));
          }
          else {
            while (pageTable[counter].getReferenceBit() == true) {
                  pageTable[counter].setReferenceBit(false);
                  ++counter;
                  if(counter >= pageTable.length) {
                    counter = 0;
                  }
            }
            pageTable[counter].setValidBit(false);
            PageTableEntry pte = new PageTableEntry(pageTable[counter].getAddress()); //or pageTable[pageNumber]??
            pte.setReferenceBit(true);
          }
      }
      /*
      Your task is to implement the pageFaultHandler() method.  This takes the page number that is being accessed.
      In the easy case,  the freeFrames list is non empty: you can take a frame address from there.
      Otherwise, you will need to choose a victim from the page table, using the second-chance algorithm.
      Once you have a victim, mark it as invalid; then create a new page table entry for the page just accessed, with its frame address,
      and valid bit set to true.
      If necessary, to implement the second-chance algorithm, you may add other member variables to the MemoryManagementUnit class.
      (Note the index into the page table should not be reset to 0 each time a page fault occurs).
      Do not change the other two Java files, though.
      */
}
