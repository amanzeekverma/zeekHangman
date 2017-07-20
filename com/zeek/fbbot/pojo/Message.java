
package com.zeek.fbbot.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Message {

    @SerializedName("mid")
    @Expose
    private String mid;
    @SerializedName("seq")
    @Expose
    private Long seq;
    @SerializedName("sticker_id")
    @Expose
    private Long stickerId;
    @SerializedName("attachment")
    @Expose
    private List<Attachment> attachments;
    @SerializedName("text")
    @Expose
    private String text;
    
    
    public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
     * 
     * @return
     *     The mid
     */
    public String getMid() {
        return mid;
    }

    /**
     * 
     * @param mid
     *     The mid
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     * 
     * @return
     *     The seq
     */
    public Long getSeq() {
        return seq;
    }

    /**
     * 
     * @param seq
     *     The seq
     */
    public void setSeq(Long seq) {
        this.seq = seq;
    }

    /**
     * 
     * @return
     *     The stickerId
     */
    public Long getStickerId() {
        return stickerId;
    }

    /**
     * 
     * @param stickerId
     *     The sticker_id
     */
    public void setStickerId(Long stickerId) {
        this.stickerId = stickerId;
    }

    /**
     * 
     * @return
     *     The attachments
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * 
     * @param attachments
     *     The attachments
     */
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

}
