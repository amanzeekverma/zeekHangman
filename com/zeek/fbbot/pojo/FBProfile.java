package com.zeek.fbbot.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class FBProfile {

	@SerializedName("first_name")
	@Expose
	private String firstName;
	@SerializedName("last_name")
	@Expose
	private String lastName;
	@SerializedName("profile_pic")
	@Expose
	private String profilePic;
	@SerializedName("locale")
	@Expose
	private String locale;
	@SerializedName("timezone")
	@Expose
	private Number timezone;
	@SerializedName("gender")
	@Expose
	private String gender;

	/**
	 * 
	 * @return The firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * 
	 * @param firstName
	 *            The first_name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * 
	 * @return The lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * 
	 * @param lastName
	 *            The last_name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * 
	 * @return The profilePic
	 */
	public String getProfilePic() {
		return profilePic;
	}

	/**
	 * 
	 * @param profilePic
	 *            The profile_pic
	 */
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	/**
	 * 
	 * @return The locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * 
	 * @param locale
	 *            The locale
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * 
	 * @return The timezone
	 */
	public Number getTimezone() {
		return timezone;
	}

	/**
	 * 
	 * @param timezone
	 *            The timezone
	 */
	public void setTimezone(Number timezone) {
		this.timezone = timezone;
	}

	/**
	 * 
	 * @return The gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * 
	 * @param gender
	 *            The gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

}