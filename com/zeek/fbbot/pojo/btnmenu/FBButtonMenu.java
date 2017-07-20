
package com.zeek.fbbot.pojo.btnmenu;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zeek.fbbot.pojo.Recipient;

@Generated("org.jsonschema2pojo")
public class FBButtonMenu {

    @SerializedName("recipient")
    @Expose
    private Recipient recipient;
    @SerializedName("message")
    @Expose
    private Message message;

    /**
     * 
     * @return
     *     The recipient
     */
    public Recipient getRecipient() {
        return recipient;
    }

    /**
     * 
     * @param recipient
     *     The recipient
     */
    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    /**
     * 
     * @return
     *     The message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     *     The message
     */
    public void setMessage(Message message) {
        this.message = message;
    }

}
