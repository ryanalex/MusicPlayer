package player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import player.Tokens.NoteToken;

/**
 * A mutation dependent interface for the ADT of ABC files, allows recursive access to the NoteToken
 * elements at its base, as well as a toString method for printing and a method by which to clear the given
 * ADT
 * 
 * Highly mutation dependent, with specific sub methods for returning copies and safely or unsafely accessing
 * elements in the ADT to ensure that mutation is controlled somewhat
 * 
 *
 *
 */
public interface ABC_ADT {
    
    
    public List<NoteToken> getElts();
    public void clear();
    @Override public String toString();
    
    /**
     * The Voice Class, contains list of Bars as well as a Name    
     * 
     *
     */
    public class Voice implements ABC_ADT{
        private final String name;
        
        List<Bars> BarsList = new ArrayList<Bars>();
        
        // A voice requires a name
        public Voice(String name){
            this.name = name;
        }
        
        /**
         * Recursively retrieves the NoteTokens of its Bars
         */
        public List<NoteToken> getElts(){
            List<NoteToken> returnList = new ArrayList<NoteToken>();
            for (Iterator<Bars> i = BarsList.iterator(); i.hasNext();){
                returnList.addAll(i.next().getElts());
            }
            return returnList;
        }
        
        /**
         * Mutates the Voice to contain an additional appended Bar
         * 
         * @param bar, the Bar to be added to the Voice
         */
        public void addElts(Bars bar){
            BarsList.add(bar);
        }
        
        /**
         * Mutates the Voice to contain an additional appended Bar,
         * However, the bar added != the bar given
         * 
         * @param bar, a Bar from which to construct a copy to add
         */
        public void addCopyElts(Bars bar){
            Bars tempBar = new Bars();
            for(Iterator<Meters> i = bar.MetersList.iterator(); i.hasNext();){
                tempBar.addCopyElts(i.next());
            }
            addElts(tempBar);
        }
        
        /**
         * @return The Name of the Voice
         */
        public String getName(){
            return this.name;
        }
        
        /**
         * Mutates the Voice by deleting all contained Bars
         */
        public void clear(){
            BarsList.clear();
        }
        
       /**
        * Returns a recursive string of the internal Bars of the Voice
        */
        @Override public String toString(){
            StringBuilder output = new StringBuilder();
            for (Iterator<Bars> i = BarsList.iterator(); i.hasNext();){
                output.append(i.next().toString());
            }
            return output.toString();
        }
    }
    
    /**
     * The Bars Class, contains a list of Meters   
     * 
     *
     */
    public class Bars implements ABC_ADT{
        
        List<Meters> MetersList = new ArrayList<Meters>();
        //Empty constructor
        public Bars(){};
        
        /**
         * Recursively retrieves the NoteTokens of its Meters
         */
        public List<NoteToken> getElts(){
            List<NoteToken> returnList = new ArrayList<NoteToken>();
            for (Iterator<Meters> i = MetersList.iterator(); i.hasNext();){
                returnList.addAll(i.next().getElts());
            }
            return returnList;
        }
        
        /**
         * Mutates the Bar to contain an additional appended Meter
         * 
         * @param meter, the Meter to be added to the Bar
         */
        public void addElts(Meters meter){
            MetersList.add(meter);
        }
        
        /**
         * Mutates the Bar to contain an additional appended Meter,
         * However, the meter added != the meter given
         * 
         * @param meter, the Meter to be added to the Bar
         */
        public void addCopyElts(Meters meter){
            Meters tempMeter = new Meters();
            for(Iterator<NoteToken> i = meter.getElts().iterator(); i.hasNext();){
                tempMeter.addElts(i.next());
            }
            addElts(tempMeter);
        }
        
        /**
         * Mutates the Bar by deleting all contained Meters
         */
        public void clear(){
            MetersList.clear();
        }
        
        /**
         * Returns a recursive string of the internal Meters of the Bars
         */
        @Override public String toString(){
        	int counter =1;
            StringBuilder output = new StringBuilder();
            for (Iterator<Meters> i = MetersList.iterator(); i.hasNext();){
            	output.append("Meter #:"+counter+" ");
                output.append(i.next().toString());
                output.append("\n");
                counter ++;
            }
            return output.toString();
        }
    }
    
    /**
     * The Meters Class, contains a list of NoteTokens
     * 
     * 
     *
     */
    public class Meters implements ABC_ADT{
        
        List<NoteToken> NotesList = new ArrayList<NoteToken>();
        // Empty Constructor
        public Meters(){};
        
        /**
         * Returns a list of the contained NoteTokens such that,
         * returnList != internal NoteTokens list
         */
        public List<NoteToken> getElts(){
            List<NoteToken> returnList = new ArrayList<NoteToken>();
            for (Iterator<NoteToken> i = NotesList.iterator(); i.hasNext();){
                returnList.add(i.next());
            }
            return returnList;
        }
        
        /**
         * Mutates the Meter to contain an additional appended NoteToken
         * 
         * Since NoteTokens are observed to be immutable, this method does
         * not need an addendum addCopy variant
         * 
         * @param note, the NoteToken to be added to the Bar
         */
        public void addElts(NoteToken note){
            NotesList.add(note);
        }       

        /**
         * Mutates the Meter by deleting all contained NoteTokens
         */
        public void clear(){
            NotesList.clear();
        }
        
        /**
         * Returns a recursive string of the internal NoteTokens of the Meters
         */
        @Override public String toString(){
            StringBuilder output = new StringBuilder();
            for (Iterator<NoteToken> i = NotesList.iterator(); i.hasNext();){
                output.append(i.next().toString());
                output.append(" ");
                
            }
            return output.toString();
        }
    }
}
