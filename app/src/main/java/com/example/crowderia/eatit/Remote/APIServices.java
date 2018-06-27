package com.example.crowderia.eatit.Remote;

import com.example.crowderia.eatit.Model.Response;
import com.example.crowderia.eatit.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by crowderia on 2/6/2018.
 */

public interface APIServices  {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAIUlLrn0:APA91bEprITdrXF3eeYCJkOs_rTXutgZZamEwjUjA_eqbqeqANqOAbwGcp5lzzGacpUbsAgJ6wxl0LICzLAgjFqhwT12l5ensjXqyRyZ3NeGGSPEXZjgjQLRTq1A5U-PjMslYK4M1C9z"
            }
    )
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
