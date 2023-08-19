/*  An implementation of the Mersenne Twister RNG.
	   
    s. p. strazza
    dec 2021
*/
class merTwist {

	/*  Don't blame me for these terrible variable initializations, but this
        is how the algorithm is specified in the literature.
    */
	final private
	  int WIDTH		  = 32,                       // Number of bits for array element value size.
	          f       = 1812433253,               // Provides bit pattern
	          r       = (WIDTH-1),                // maximum shift given size WIDTH
	          m       = 397,					  // not a clue what this does
	          u       = 11,                       // Prime shift
	          s       = 7,                        // Second prime shift
	          t       = (WIDTH/2)-1,              // half-word shift without sign
	          l       = 18,                       // Why they want the lower 18 bits, I don't know
	          b       = 0x9D2C5680,               // Who knows?
	          c       = 0xEFC0000;                //
	
	/*  Stuff we need to define for the whole mathematics of the thing.
	*/
	final private
	  int ASIZE       =  624,                      // Array size (algorithm memory state)
	      BAD_REQUEST = -999,                      // used with get_next()
	      LOWERMASK   = (1 << r) - 1,              // Results in 0x7fffffff
	      UPPERMASK   = (int) -1 & (~(LOWERMASK)); // Results in 0x800000000
	final private
	int[] MAGIC       = { 0 , 0x9908B0DF };        // Long story about this...

	/*  Instance and published.
	*/
	private int       index;
		    boolean   RETURN_CODE;

	/*  The array size determines the algorithm period.
	 *  The period length is 2^(19,937-1). The period is the distance between
	 *  the same value being generated. Note that 2^(19,937-1) is a prime number
	 *  and being one less than a power of 2, makes it a Mersenne Prime.
	 *  
	 *  The array size allows 31 unused bits to migrate through the array as
	 *  the next seed is calculated so that no single array element can be
	 *  replaced with a one-bit value.
	 *  
	 *  http://www.quadibloc.com/crypto/co4814.htm
	 */
	private int[] MT         = new int[ASIZE];
	
	/*  Constructor call depends on who the caller is.
	*/
	merTwist() {
		return;
	}
	
	merTwist(int seed) {
		seedMethod1(seed);
		return;
	}
	
	int getNext() {
		int y;
		
		/*  Mersenne Twist RNG algorithm
		*/
		RETURN_CODE = false;
		if  (index > ASIZE)
			return(BAD_REQUEST);
		
		/*  When the end of the array is reached, 
		    the algorithm re-shuffles the array contents.
		*/
		if  (index == ASIZE)
		    {  for (index = 0 ; index < (ASIZE-m) ; index++)
		         {  y         = (MT[index] & UPPERMASK) | (MT[index+1] & LOWERMASK);
		            MT[index] = MT[index+m] ^ (y >>> 1) ^ (MAGIC[y & 0x1]);
		         }
		       for (; index < (ASIZE-1) ; index++)
	             {  y         = (MT[index] & UPPERMASK) | (MT[index+1] & LOWERMASK);
	                MT[index] = MT[index+(m-ASIZE)] ^ (y >>> 1) ^ (MAGIC[y & 0x1]);
	             }
		       y              = (MT[ASIZE - 1] & UPPERMASK) | (MT[0] & LOWERMASK);
		       MT[ASIZE-1]    = MT[m-1] ^ (y >>> 1) ^ MAGIC[y & 0x1];
		       index          = 0;
		}
		
		/*  I guess I could figure this out,
		    just never bothered. It works. 
		    Good enough.
		*/
		y  = MT[index++];
		y ^=  (y >> u);
		y ^= ((y << s) & b);
		y ^= ((y << t) & c);
		y ^=  (y >> l);
		++index;
		RETURN_CODE = true;
		return(y);			
	}
	
	/*  Initialize the array. This is the memory state of
        the RNG.
    */
    private
    void	seedMethod1(int seed) {
    			
	     	RETURN_CODE = false;
	        if  (seed  == 0)
	        	return;
	        index       = ASIZE;
	
	        /*  Calculate the next entry using the previous. Why zeroth
	        	entry is set outside the loop because its initial value is
	        	the seed.
	        */
	        MT[0]       = seed;
	        
	        /*  Dancin', dancin' all these bits, doo-daa, doo-daa...
	           
	            XOR the previous value with the two MSBs shifted to the
	            LSB, add the index position to guarantee value uniqueness,
	            then multiply it by some constant to force an overflow.
	            What remains after the overflow becomes the next entry.
	         */
	        for (int i  = 1 ; i < MT.length ; i++)
	        	MT[i]   = f * (MT[i-1] ^ (MT[i-1] >> (WIDTH-2))) + i;
	        RETURN_CODE = true;
	        return;
}

/*  Used for debugging only.

    private
	void	arrayDump() {
		
		    for (int i = 0 ; i < MT.length ; i++)
		        {  System.out.printf("%8x ",MT[i]);
		
		          //  Simply for output esthetics
		          if  (i % 6 == 0)
			           System.out.println();
		        }
	        System.out.printf("\n %4x %4x",LOWERMASK,UPPERMASK);
	        return;
}
*/
}
