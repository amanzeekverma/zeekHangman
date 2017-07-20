package com.zeek.fbbot.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Postback {

@SerializedName("payload")
@Expose
private String payload;

public String getPayload() {
return payload;
}

public void setPayload(String payload) {
this.payload = payload;
}

}