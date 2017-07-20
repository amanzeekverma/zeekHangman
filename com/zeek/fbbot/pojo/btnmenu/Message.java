
package com.zeek.fbbot.pojo.btnmenu;

import java.util.List;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zeek.fbbot.pojo.btnmenu.QuickReply;

@Generated("org.jsonschema2pojo")
public class Message {

    @SerializedName("attachment")
    @Expose
    private Attachment attachment;

    /**
     * 
     * @return
     *     The attachment
     */
    public Attachment getAttachment() {
        return attachment;
    }

    /**
     * 
     * @param attachment
     *     The attachment
     */
    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }
    
    
    //Adding Quick Replies here
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("quick_replies")
    @Expose
    private List<QuickReply> quickReplies = null;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<QuickReply> getQuickReplies() {
        return quickReplies;
    }

    public void setQuickReplies(List<QuickReply> quickReplies) {
        this.quickReplies = quickReplies;
    }

}
