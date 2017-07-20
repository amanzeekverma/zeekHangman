
package com.zeek.fbbot.pojo.btnmenu;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Payload {

    @SerializedName("template_type")
    @Expose
    private String templateType;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("buttons")
    @Expose
    private List<Button> buttons = new ArrayList<Button>();

    /**
     * 
     * @return
     *     The templateType
     */
    public String getTemplateType() {
        return templateType;
    }

    /**
     * 
     * @param templateType
     *     The template_type
     */
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    /**
     * 
     * @return
     *     The text
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @param text
     *     The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 
     * @return
     *     The buttons
     */
    public List<Button> getButtons() {
        return buttons;
    }

    /**
     * 
     * @param buttons
     *     The buttons
     */
    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

}
