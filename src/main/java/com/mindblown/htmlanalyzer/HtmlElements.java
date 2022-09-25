package com.mindblown.htmlanalyzer;


import com.mindblown.evalulator.Evaluator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class HtmlElements {
    private ArrayList<HtmlTag> tags;
    
    /**
     * Creates a HtmlElements object with an empty list of HtmlTags.
     */
    public HtmlElements() {
        tags = new ArrayList<>();
    }
    
    /**
     * Creates a HtmlElements object with a list of HtmlTags.
     * @param tags the HtmlTags that this object should contain
     */
    public HtmlElements(ArrayList<HtmlTag> tags) {
        this.tags = tags;
    }

    /**
     * Set the list of this object's htmlTags to tags
     * @param tags the list of htmlTags to set this object's htmlTags to
     */
    public void setTags(ArrayList<HtmlTag> tags) {
        this.tags = tags;
    }
    
    /**
     * Get the object's list of htmlTags.
     * @return the object's list of htmlTags
     */
    public ArrayList<HtmlTag> getTags() {
        return tags;
    }
    
    /**
     * Add an htmlTag to the object's list of htmlTags.
     * @param tag the tag to add
     */
    public void addTag(HtmlTag tag){
        tags.add(tag);
    }
    
    public static HtmlElements getHtmlElements(File f){
        HtmlScanner scan = new HtmlScanner(f);
        HtmlElements tags = scan.getHtmlElements();
        return tags;
    }
    
    //THESE FOLLOWING FUNCTIONS WERE USED WHILE HTMLELEMENTS SORTED ITS TAGS. HOWEVER, THAT FUNCTIONALITY IS NO LONGER
    //INCLUDED, SO THESE FUNCTIONS HAVE BEEN COMMENTED OUT BUT SAVED IN CASE THEY ARE USEFUL IN THE FUTURE
//    /**
//     * This function returns all the tags that this object contains that when compared to type with comparator, the comparator = 0. 
//     * The type variable essentially is supposed to contain the essential characteristics that you want the tags to be returned 
//     * by this function to have. The comparator passed in will check whether each tag is equal to the type and if it is, that variable 
//     * will be added to the list of tags which will be returned at the end of the function.
//     * @param type the tag that contains the essential characteristics that you want the tags returned to have
//     * @param comparator a comparator that compares the Html tags and the type tag and returns 0 when an html tag has the 
//     * essential characteristics that match type
//     * @return an array of HtmlTags (cast to the object class) that when compared to the comparator, the comparator returns 0
//     */
//    public Object[] getTagsOfType(HtmlTag type, Comparator<HtmlTag> comparator){
//        //Make an evaluator interface that accepts a tag if it has the essential characteristic of type,
//        //and then get all tags from that evaluator
//        return getTagsOfType(new Evaluator<HtmlTag>() {
//            public boolean evalToBool(HtmlTag t) {
//                return comparator.compare(type, t) == 0;
//            }
//        });
//    }
//    
//    /**
//     * This function returns all tags that when used as the argument in the evaler.evalToBool(), 
//     * evaler will return true. 
//     * @param evaler The evaluator used to determine whether a tag should be part of the tag list returned
//     * @return all tags that when used as the argument in the evaler.evalToBool(), evaler will return true 
//     */
//    public Object[] getTagsOfType(Evaluator<HtmlTag> evaler){
//        //Make a new list
//        ArrayList<HtmlTag> tagsOfType = new ArrayList<>();
//        
//        //Go through all tags. If they evaluate to true, add them to the list
//        for(int i = 0; i < tags.size(); i++){
//            HtmlTag tag = tags.get(i);
//            if(evaler.evalToBool(tag)){
//                tagsOfType.add(tag);
//            }
//        }
//        
//        //Return the tags evaluated to true in array form
//        return tagsOfType.toArray();
//    }

    
    /**
     * Returns what the elements in HtmlElements would have looked like had they come from a 
     * source like an HTML file. This would essentially be the tags in order
     * @return the HTML tags formatted into how they would appear in an HTML file
     */
    @Override
    public String toString() {
        String htmlSource = "";
        for(int i = 0; i < tags.size(); i++){
            //Get the tag, turn it into a string, indent it the certain amount of times needed (since the rank starts
            //at 1, and tags at rank 1 in an HTML file would have 0 indents, make the indent number the rank - 1.
            //Then, add that string to the HTML source
            HtmlTag tag = tags.get(i);
            htmlSource += tag.toString();
            if(i < tags.size() - 1){
                //If there are still more tags, add a new line for the next tag.
                htmlSource += "\n";
            }
        }
        return htmlSource;
    }
}