package com.trioangle.goferdriver.payouts.payout_model_classed;

/**
 *
 * @package     com.makent.trioangle
 * @subpackage  model
 * @category    Makent_model
 * @author      Trioangle Product Team
 * @version     1.1
 */

import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Makent_model implements Serializable {
	private String Explore_room_image,roomid,roomname,roomprice,roomrating,roomreview,roomiswishlist,roomcountryname,currencycode,currencysymbol,roomlat,roomlong,roomtype,instantbook;
	private String wishlistImage,wishlistId,wishlistName,wishlistPrivacy,tripstype,tripstypecount,countryid;
	private String id;
	private String amenities,amenitiesid,name,countryname,gendertype,sharename;
	private boolean amenitiesselected;
	private char amenities_image;
	private String review_user_name,review_user_image,review_date,review_message;
	String reservation_id,service_fee,host_fee,hostid,room_id,trip_status,specialofferid,specialofferstatus,booking_status,trip_date,room_name,room_location,host_user_name,host_thumb_image;
	String message,is_message_read,total_cost,check_in_time,check_out_time,requestuserid;
	Drawable shareicon;
	ResolveInfo shareitem;
	private String coupon_amount,host_penalty_amount;



	public String type;

	public String days;
	public String percentage;

	public String discount_id,discount_type,period,discount;
	public String available_id,available_type,minimum_stay,maximum_stay,start_date,end_date,during;
	public String text;
	public String start_date_formatted;
	public String end_date_formatted;

	public String countryCode;


	public Makent_model() {

	}
	public Makent_model(String type) {
		this.type = type;
	}

	public Makent_model(String type, String Explore_room_image, String roomid, String roomname, String roomprice, String roomrating, String roomreview, String roomiswishlist, String roomcountryname, String currencycode, String currencysymbol,
                        String id, boolean amenitiesselected, char amenities_image, String amenities, String amenitiesid, String name, String countryname, String countryid, String gendertype, String sharename, Drawable shareicon, ResolveInfo shareitem,
                        String wishlistImage, String wishlistId, String wishlistName, String wishlistPrivacy, String tripstypecount, String tripstype, String roomlat, String roomlong, String roomtype, String instantbook) {
		this.type=type;
		this.Explore_room_image=Explore_room_image;
		this.roomid=roomid;
		this.roomname=roomname;
		this.roomprice=roomprice;
		this.roomrating=roomrating;
		this.roomreview=roomreview;
		this.roomiswishlist=roomiswishlist;
		this.roomcountryname=roomcountryname;
		this.currencycode=currencycode;
		this.currencysymbol=currencysymbol;

		this.roomtype=roomtype;
		this.roomlat=roomlat;
		this.roomlong=roomlong;

		this.id=id;
		this.amenities_image=amenities_image;
		this.amenitiesid=amenitiesid;
		this.amenities=amenities;
		this.amenitiesselected=amenitiesselected;
		this.name=name;
		this.countryname=countryname;
		this.gendertype=gendertype;
		this.sharename=sharename;
		this.shareicon=shareicon;
		this.shareitem=shareitem;
		this.wishlistImage=wishlistImage;
		this.wishlistId=wishlistId;
		this.wishlistName=wishlistName;
		this.wishlistPrivacy=wishlistPrivacy;
		this.tripstypecount=tripstypecount;
		this.tripstype=tripstype;
		this.instantbook=instantbook;
	}

	public Makent_model(String type, String review_user_name, String review_user_image, String review_date, String review_message)
	{
		this.type = type;
		this.review_user_name=review_user_name;
		this.review_user_image=review_user_image;
		this.review_date=review_date;
		this.review_message=review_message;
	}

	public Makent_model(String discount_id, String discount_type, String period, String discount)
	{
		this.discount_id = discount_id;
		this.discount_type=discount_type;
		this.period=period;
		this.discount=discount;
	}

	public Makent_model(String days, String percentage) {
		this.days = days;
		this.percentage = percentage;
	}

	public Makent_model(String available_id, String available_type, String minimum_stay, String maximum_stay, String start_date, String end_date, String during)
	{
		this.available_id = available_id;
		this.available_type = available_type;
		this.minimum_stay= minimum_stay;
		this.maximum_stay=maximum_stay;
		this.start_date=start_date;
		this.end_date=end_date;
		this.during=during;
	}

	// Trips Details
	public Makent_model(String reservation_id, String hostid, String room_id, String trip_status, String booking_status, String trip_date,
                        String room_name, String room_location, String host_user_name, String host_thumb_image, String specialofferstatus, String specialofferid)
	{
		this.reservation_id = reservation_id;
		this.hostid=hostid;
		this.room_id=room_id;
		this.trip_status=trip_status;
		this.booking_status=booking_status;
		this.trip_date=trip_date;
		this.room_name=room_name;
		this.room_location=room_location;
		this.host_user_name=host_user_name;
		this.host_thumb_image=host_thumb_image;
		this.specialofferstatus = specialofferstatus;
		this.specialofferid = specialofferid;
	}
//InBox Details
	public Makent_model(String reservation_id, String room_id, String message_status,
                        String room_name, String room_location, String host_user_name, String host_thumb_image
			, String message, String is_message_read, String total_cost, String check_in_time, String check_out_time,
                        String requestuserid, String host_fee, String service_fee)
	{
		this.reservation_id = reservation_id;
		this.room_id=room_id;
		this.trip_status=message_status;
		this.room_name=room_name;
		this.room_location=room_location;
		this.host_user_name=host_user_name;
		this.host_thumb_image=host_thumb_image;
		this.message=message;
		this.is_message_read=is_message_read;
		this.total_cost=total_cost;
		this.service_fee=service_fee;
		this.host_fee=host_fee;
		this.check_out_time=check_out_time;
		this.check_in_time=check_in_time;
		this.requestuserid=requestuserid;

	}
	 //Detailed list space

	public String getSpecialofferid() { return specialofferid; }

	public void setSpecialofferid(String specialofferid) { this.specialofferid = specialofferid; }

	public String getSpecialofferstatus() { return specialofferstatus; }

	public void setSpecialofferstatus(String specialofferstatus) { this.specialofferstatus = specialofferstatus; }


	public String getType() { return type; }

	public void setType(String type) { this.type = type; }


	public String getDays() { return days; }

	public void setDays(String days) { this.days = days; }

	public String getPercentage() { return percentage; }

	public void setPercentage(String percentage) { this.percentage = percentage; }

	public String getExplore_room_image() {	return Explore_room_image; }

	public void setExplore_room_image(String Explore_room_image) {this.Explore_room_image = Explore_room_image; }

	public String getRoomid() {	return roomid;	}

	public void setRoomid(String roomid) {this.roomid = roomid; }

	public String getRoomname() {return roomname;}

	public void setRoomname(String roomname) {this.roomname = roomname; }

	public String getRoomprice() {return roomprice;	}

	public void setRoomprice(String roomprice) {this.roomprice = roomprice; }

	public String getRoomrating() { return roomrating;	}

	public void setRoomrating(String roomrating) {this.roomrating = roomrating; }

	public String getRoomreview() { return roomreview;	}

	public void setRoomreview(String roomreview) {this.roomreview = roomreview; }

	public String getRoomiswishlist() { return roomiswishlist;	}

	public void setRoomiswishlist(String roomiswishlist) {this.roomiswishlist = roomiswishlist; }

	public String getRoomcountryname() { return roomcountryname;	}

	public void setRoomcountryname(String roomcountryname) {this.roomcountryname = roomcountryname; }

	public String getCurrencycode() { return currencycode;	}

	public void setCurrencycode(String currencycode) {this.currencycode = currencycode; }

	public String getCurrencysymbol() { return currencysymbol;	}

	public void setCurrencysymbol(String currencysymbol) {this.currencysymbol = currencysymbol; }

	public String getid() {
		return id;
	}

	public void setid(String id) {this.id = id;	}

	public boolean getAmenitiesSelected() {
		return amenitiesselected;
	}

	public void setAmenitiesSelected(boolean amenitiesselected) { this.amenitiesselected = amenitiesselected; }

	public char getAmenities_image() {
		return amenities_image;
	}

	public void setAmenities_image(char amenities_image) { this.amenities_image = amenities_image; }

	public String getAmenitiesId() {
		return amenitiesid;
	}

	public void setAmenitiesId(String amenitiesid) {	this.amenitiesid = amenitiesid; }

	public String getAmenities() {
		return amenities;
	}

	public void setAmenities(String amenities) {	this.amenities = amenities; }

	public String getName() {
		return name;
	}

	public void setName(String name) {		this.name = name;	}

	public String getCountryName() {
		return countryname;
	}

	public void setCountryName(String countryname) {	this.countryname = countryname;	}

	public String getCountryId() {
		return countryid;
	}

	public void setCountryId(String countryid) {	this.countryid = countryid;	}

	public String getGenderType() {
		return gendertype;
	}

	public void setGenderType(String gendertype) {	this.gendertype = gendertype;	}

	public String getShareName() {
		return sharename;
	}

	public void setSharename(String sharename) {	this.sharename = sharename;	}

	public Drawable getShareIcon() {
		return shareicon;
	}

	public void setShareIcon(Drawable shareicon) {	this.shareicon = shareicon;	}

	public ResolveInfo getShareItem() {
		return shareitem;
	}

	public void setShareItem(ResolveInfo shareitem) {	this.shareitem = shareitem;	}

	public String getWishlistImage() {	return wishlistImage;	}

	public void setWishlistImage(String wishlistImage) {	this.wishlistImage = wishlistImage;	}

	public String getWishlistId() {	return wishlistId;	}

	public void setWishlistId(String wishlistId) {	this.wishlistId = wishlistId;	}

	public String getWishlistPrivacy() {	return wishlistPrivacy;	}

	public void setWishlistPrivacy(String wishlistPrivacy) {	this.wishlistPrivacy = wishlistPrivacy;	}

	public String getWishlistName() {return wishlistName;	}

	public void setWishlistName(String wishlistName) {	this.wishlistName = wishlistName;	}

	public String getTripsType() {	return tripstype;	}

	public void setTripsType(String tripstype) {	this.tripstype = tripstype;	}

	public String getTripsTypeCount() {
		return tripstypecount;
	}

	public void setTripsTypeCount(String tripstypecount) {	this.tripstypecount = tripstypecount;	}

	public String getReviewUserName() {
		return review_user_name;
	}

	public void setReviewUserName(String review_user_name) {	this.review_user_name = review_user_name;	}

	public String getReviewUserImage() {
		return review_user_image;
	}

	public void setReviewUserImage(String review_user_image) {	this.review_user_image = review_user_image;	}

	public String getReviewDate() {	return review_date;	}

	public void setReviewDate(String review_date) {	this.review_date = review_date;	}

	public String getReviewMessage() {
		return review_message;
	}

	public void setReviewMessage(String review_message) {	this.review_message = review_message;	}

	public String getRoomtype() {
		return roomtype;
	}

	public void setRoomtype(String roomtype) {this.roomtype = roomtype;	}

	public String getRoomlat() {
		return roomlat;
	}

	public void setRoomlat(String roomlat) {this.roomlat = roomlat;	}

	public String getRoomlong() {
		return roomlong;
	}

	public void setRoomlong(String roomlong) {	this.roomlong = roomlong; }

	public String getInstantBook() {
		return instantbook;
	}

	public void setInstantBook(String instantbook) {	this.instantbook = instantbook; }

	public String getReservationId() {
		return reservation_id;
	}

	public void setReservationId(String reservation_id) {	this.reservation_id = reservation_id; }

	public String getRoomId() {
		return room_id;
	}

	public void setRoomId(String room_id) {	this.room_id = room_id; }

	public String getTripStatus() {
		return trip_status;
	}

	public void setTripStatus(String trip_status) {	this.trip_status = trip_status; }

	public String getBookingStatus() {
		return booking_status;
	}

	public void setBookingStatus(String booking_status) {	this.booking_status = booking_status; }

	public String getTripDate() {
		return trip_date;
	}

	public void setTripDate(String trip_date) {	this.trip_date = trip_date; }

	public String getRoomName() {
		return room_name;
	}

	public void setRoomName(String room_name) {	this.room_name = room_name; }

	public String getRoomLocation() {
		return room_location;
	}

	public void setRoomLocation(String room_location) {	this.room_location = room_location; }

	public String getHostId() {
		return hostid;
	}

	public void setHostId(String hostid) {	this.hostid = hostid; }


	public String getHostUserName() {
		return host_user_name;
	}

	public void setHostUserName(String host_user_name) {	this.host_user_name = host_user_name; }

	public String getHostThumbImage() {
		return host_thumb_image;
	}

	public void setHostThumbImage(String host_thumb_image) {	this.host_thumb_image = host_thumb_image; }

	public String getLastMessage() {
		return message;
	}

	public void setLastMessage(String message) {	this.message = message; }

	public String getIsMessageRead() {
		return is_message_read;
	}

	public void setIsMessageRead(String is_message_read) {	this.is_message_read = is_message_read; }

	public String getRequestUserId() {
		return requestuserid;
	}

	public void setRequestUserId(String requestuserid) {	this.requestuserid = requestuserid; }


	public String getTotalCost() {
		return total_cost;
	}

	public void setTotalCost(String total_cost) {	this.total_cost = total_cost; }

	public String getServiceFee() {
		return service_fee;
	}

	public void setServiceFee(String service_fee) {	this.service_fee = service_fee; }

	public String getHostFee() {
		return host_fee;
	}

	public void setHostFee(String host_fee) {	this.host_fee = host_fee; }


	public String getCheckInTime() {
		return check_in_time;
	}

	public void setCheckInTime(String check_in_time) {	this.check_in_time = check_in_time; }

	public String getCheckOutTime() {
		return check_out_time;
	}

	public void setCheckOutTime(String check_out_time) {	this.check_out_time = check_out_time; }



	public String getDiscountId() {
		return discount_id;
	}

	public void setDiscountId(String discount_id) {	this.discount_id = discount_id; }

	public String getDiscountType() {
		return discount_type;
	}

	public void setDiscountType(String discount_type) {	this.discount_type = discount_type; }

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {	this.period = period; }

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {	this.discount = discount; }


	public String getAvailableId() {
		return available_id;
	}

	public void setAvailableId(String available_id) {	this.available_id = available_id; }

	public String getAvailableType() {
		return available_type;
	}

	public void setAvailableType(String available_type) {	this.available_type = available_type; }

	public String getMinimumStay() {
		return minimum_stay;
	}

	public void setMinimumStay(String minimum_stay) {	this.minimum_stay = minimum_stay; }

	public String getMaximumStay() {
		return maximum_stay;
	}

	public void setMaximumStay(String maximum_stay) {	this.maximum_stay = maximum_stay; }

	public String getStartDate() {
		return start_date;
	}

	public void setStartDate(String start_date) {	this.start_date = start_date; }

	public String getEndDate() {
		return end_date;
	}

	public void setEndDate(String end_date) {	this.end_date = end_date; }

	public String getDuring() {
		return during;
	}

	public void setDuring(String during) {	this.during = during; }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getStart_date_formatted() {
		return start_date_formatted;
	}

	public void setStart_date_formatted(String start_date_formatted) {
		this.start_date_formatted = start_date_formatted;
	}

	public String getEnd_date_formatted() {
		return end_date_formatted;
	}

	public void setEnd_date_formatted(String end_date_formatted) {
		this.end_date_formatted = end_date_formatted;
	}
	public String getCoupon_amount() {
		return coupon_amount;
	}

	public void setCoupon_amount(String coupon_amount) {
		this.coupon_amount = coupon_amount;
	}

	public String getHost_penalty_amount() {
		return host_penalty_amount;
	}

	public void setHost_penalty_amount(String host_penalty_amount) {
		this.host_penalty_amount = host_penalty_amount;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
