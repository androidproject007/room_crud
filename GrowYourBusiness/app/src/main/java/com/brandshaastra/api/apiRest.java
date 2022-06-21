package com.brandshaastra.api;

import com.brandshaastra.interfaces.Consts;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface apiRest {


    @FormUrlEncoded
    @POST("categoryapi2")
    Call<ResponseBody> getAllCategory(@Field("user_id") String user_id, @Field(Consts.DEVICE_TOKEN) String device_id);

    @FormUrlEncoded
    @POST("categoryapi2_video")
    Call<ResponseBody> getAllCategoryVideo(@Field("user_id") String user_id, @Field(Consts.DEVICE_TOKEN) String device_id);

    @FormUrlEncoded
    @POST("videodata")
    Call<ResponseBody> getAllvideo(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_slider")
    Call<ResponseBody> getSlider(@Field("user_id") String user_id, @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("getBusinessDataById")
    Call<ResponseBody> getBusinessDataById(@Field("user_id") String user_id, @Field("bussiness_profile_id") String id);

    @FormUrlEncoded
    @POST("plan_detail")
    Call<ResponseBody> getAllPlan(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("add_plan_to_user")
    Call<ResponseBody> addPlan(@Field("user_id") String user_id, @Field("plan_id") String plan_id);

    @FormUrlEncoded
    @POST("getallsubcat")
    Call<ResponseBody> getAllSubCategory(@Field("user_id") String user_id, @Field("cat_id") String cat_id);

    @FormUrlEncoded
    @POST("getallsubcat_video")
    Call<ResponseBody> getAllSubCatVideo(@Field("user_id") String user_id, @Field("cat_id") String cat_id);

    @FormUrlEncoded
    @POST("getallsubcat_image")
    Call<ResponseBody> getAllSubCategoryImage(@Field("user_id") String user_id, @Field("subcatgory_id") String cat_id);

    @FormUrlEncoded
    @POST("getVideos")
    Call<ResponseBody> getAllSubCategoryVideo(@Field("user_id") String user_id, @Field("subcatgory_id") String cat_id);

    @FormUrlEncoded
    @POST("get_slider")
    Call<ResponseBody> get_slider(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_bussiness_data")
    Call<ResponseBody> getAllBusinessData(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("apply_coupon_code")
    Call<ResponseBody> applayCoupanCode(@Field("user_id") String user_id, @Field("coupon_code") String coupon_code, @Field("plan_id") String plan_id);

    @FormUrlEncoded
    @POST("send_otp")
    Call<ResponseBody> sendOtp2(@Field("email") String email);

    @Multipart
    @POST("edit_rofile")
    Call<ResponseBody> editProfile(@Part MultipartBody.Part filethree,
                                   @Part("user_id") RequestBody user_id,
                                   @Part("name") RequestBody model_name);

    @Multipart
    @POST("add_bussiness_profile")
    Call<ResponseBody> upload_business_data(@Part MultipartBody.Part filethree,
                                            @Part("user_id") RequestBody user_id,
                                            @Part("c_mobile") RequestBody model_name,
                                            @Part("c_company_name") RequestBody c_company_name,
                                            @Part("c_email") RequestBody c_email,
                                            @Part("c_address") RequestBody c_address,
                                            @Part("c_website") RequestBody c_website,
                                            @Part("c_description") RequestBody c_description,
                                            @Part("c_designation") RequestBody c_designation,
                                            @Part("social_media") RequestBody social_media,
                                            @Part("c_referral_code") RequestBody c_referalcode);

    @Multipart
    @POST("update_bussiness_profile")
    Call<ResponseBody> update_data(@Part MultipartBody.Part filethree,
                                   @Part("bussiness_profile_id") RequestBody business_id,
                                   @Part("user_id") RequestBody user_id,
                                   @Part("c_mobile") RequestBody model_name,
                                   @Part("c_company_name") RequestBody c_company_name,
                                   @Part("c_email") RequestBody c_email,
                                   @Part("c_address") RequestBody c_address,
                                   @Part("c_website") RequestBody c_website,
                                   @Part("c_description") RequestBody c_description,
                                   @Part("social_media") RequestBody social_media);

    @FormUrlEncoded
    @POST("checksignin")
    Call<ResponseBody> checksignin(@Field("email") String email, @Field(Consts.DEVICE_TOKEN) String device_id);

    @FormUrlEncoded
    @POST("regisration")
    Call<ResponseBody> getSignupData(@Field("name") String name, @Field("email") String email,
                                     @Field("profile_mobile") String profile_mobile, @Field("mobile") String mobile,
                                     @Field("referal") String referal, @Field("designation") String designation, @Field("c_company_name") String c_company_name,
                                     @Field("c_description") String c_description, @Field("c_mobile") String c_mobile,
                                     @Field("c_email") String c_email, @Field("c_website") String c_website, @Field("password") String pass,
                                     @Field(Consts.DEVICE_TOKEN) String device_id);

    @FormUrlEncoded
    @POST("getSubscriptionData")
    Call<ResponseBody> getSubscriptionData(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_profile_data")
    Call<ResponseBody> getProfileData(@Field("user_id") String user_id);

}
