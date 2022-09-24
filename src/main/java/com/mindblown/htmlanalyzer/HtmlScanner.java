package com.mindblown.htmlanalyzer;


import com.mindblown.util.FileUtil;
import com.mindblown.util.Util;
import java.io.File;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class HtmlScanner {
    
    private static final String[] autoClosed = new String[]{
    "area", "base", "br", "col", "embed", "hr", "img", "input", "link", "media",
        "meta", "param", "source", "track", "wbr"
    };
    
    private String html;
    private HtmlElements htmlElements;
    private int htmlIndex = 0;
    
    public HtmlScanner(File h) {
        htmlElements = new HtmlElements();
        if(h.isFile() == false){
            return;
        }
        String text = FileUtil.getText(h);
        if(text == null){
            return;
        }
        html = text.replaceAll("[^\\S ]", "");
//        html = text.replaceAll("[^ && \\s]", "");
        analyzeHtml();
    }

    public HtmlElements getHtmlElements() {
        return htmlElements;
    }
    
    private void analyzeHtml(){
        htmlIndex = 0; //To reset the html index count
        //We use a rank to identify the structures inside the html
        int rank = 1;
        //Keep analyzing HTML while there are still tags to be analyzed
        while(hasNextTag()){
            //Get the next tag and its data
            HtmlTag tag = getNextTag(rank);
            HtmlTagData data = tag.getData();
            if(!data.isStart()){
                
                //If the tag is a closed tag or is an auto-closer, that means that any tags after this isn't
                //part of that tag. Since the corresponding starter tag of this current closing tag would have caused
                //an increase in rank, we cause a decrease in rank to bring the rank back to where it started.
                rank--;
                //Set the tag's rank to that decreased rank
                data.setRank(rank);
            } else if(data.isAutoClose()){
                //If the tag doesn't close itself, then don't do anything to the rank, since
                //any tags after this won't be inside this tag.
            } else {
                //If it's a starter then increase the rank (this means that any tags
                //that come after this are nested inside this tag
                rank++;
            }
            //Add the tag
            htmlElements.addTag(tag);
        }
    }
    
    private HtmlTag getNextTag(int rank){
        String cleaned = clean();
        if(!hasNextTag()){
            return null;
        }
        int start = html.indexOf("<", htmlIndex);
        int space = html.indexOf(" ", start+1);
        int end = html.indexOf(">", start+1);
        boolean hasTribs = false;
        if(space < end && space != -1){
            //If there's a space after the < part of the tag and before the > part of the tag,
            //then there are tribs in the tag (or maybe just a blank space. If there is just a blank
            //space with no tribs, the AttribScanner will pick up on this and just return an empty list
            //of tribs
            hasTribs = true;
        }
        //The number of characters the name of the tag is from the < part of the tag.
        //Usually it's one, because the name is usually right after the <
        int nameOff = 1;
        if(html.charAt(start+1) == '/'){
            //However, if the tag is an ending tag, there will be a slash after the
            //<, after which is the name. So if that's true, the distance would be 2 characters
            nameOff = 2;
        }
        //This is the position of the index right after the name of the tag
        int tagNameEnd;
        if(space == -1){
            //If there's no space, then there aren't any tribs, which means the tag name will end
            //at the > part of the tag
            tagNameEnd = end;
        } else {
            //If there is a space, and it's before the > part of the tag, then that space will be the index
            //right after the tag name. If it's after the > part of the tag, then the index right after the name
            //of the tag will be the > part of the tag. The Math.min will take the lesser of the two, and will thus
            //give the right index.
            tagNameEnd = Math.min(space, end);
        }
        
        //If there is a / right before the > part of the tag, that means that the tag will
        //be an auto-closing tag and the slash would thus not be part of the tag name. So, if the tag name is supposed to end
        //at the >, check to see if there is a slash right before the > part of the tag, and if so, decrease the tagNameEnd so that the /
        //isn't included in the tag name.
            
        if(tagNameEnd == end){
            if(html.charAt(tagNameEnd - 1) == '/'){
                tagNameEnd--;
            }
        }
        
        //Get the tag name from the starting index plus the num of characters, and the index that has the end of the tag name
        String tagName = html.substring(start+nameOff, tagNameEnd);
        if(nameOff == 2){
            //If the name offset was 2 (aka if the tag is a closing tag), then return 
            //a closing tag with no tribs, whatever was cleaned, whatever was inside the parent, saying
            //that it's an autoclosing tag (since it does close), and its rank.
            HtmlTagData data = new HtmlTagData(new Attrib[0], cleaned, "", false, true, rank);
            //Increase the html index to after the > part of the tag
            htmlIndex = end + 1;
            return new HtmlTag(tagName, data);
        }
        //Determine whether it closes itself (by its name or if it has a slash at the end, which signifies it closes itself)
        boolean autoClose = isAutoClose(tagName) || html.charAt(end - 1) == '/';
        Attrib[] tribs;
        if(hasTribs){
            tribs = Attrib.getAttribs(html, start + 1 + tagName.length(), end);
        } else {
            tribs = new Attrib[0];
        }
        HtmlTagData data = new HtmlTagData(tribs, "", cleaned, true, autoClose, rank);
        htmlIndex = end + 1;
        return new HtmlTag(tagName, data);
    }
    
    private boolean isAutoClose(String name){
        int ind = Arrays.binarySearch(autoClosed, name.toLowerCase());
        if(ind < 0){
            return false;
        } else {
            return true;
        }
    }
    
    private boolean hasNextTag(){
        int ind = html.indexOf("<", htmlIndex);
        if(ind == -1){
            return false;
        }
        int end = html.indexOf(">", ind+1);
        if(end == -1){
            return false;
        }
        return true;
    }
    
    private String clean(){
        boolean go = true;
        String cleaned = "";
        while(go){
            int ind = html.indexOf("<", htmlIndex);
            if(ind == -1){
                html = "";
                return cleaned;
            }
//            cleaned += html.subSequence(htmlIndex, ind);
            cleaned += html.substring(htmlIndex, ind);
            if(ignore(ind+1)){
                int i = html.indexOf(">", ind+2);
                if(i == -1){
                    html = "";
                    return cleaned;
                }
                
                htmlIndex = i + 1;
                return cleaned;
            } else {
                go = false;
            }
            //RULE: IF TAG STARTS WITH ! IS EITHER IGNORED OR DOCTYPE IF START WITH NOT ALPHA< THEN NOT TAG AND SHOUILD BE IGNORED
        }
        return cleaned;
    }
    
    private boolean ignore(int look){
        char c = html.charAt(look);
        return !Character.isAlphabetic(c) && c != '/';
    }

    public String getHtml() {
        return html;
    }
}