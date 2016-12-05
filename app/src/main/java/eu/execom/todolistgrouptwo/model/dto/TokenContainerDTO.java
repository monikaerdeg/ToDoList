package eu.execom.todolistgrouptwo.model.dto;

import com.google.gson.annotations.SerializedName;


public class TokenContainerDTO {

    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return "TokenContainerDTO{" +
                "accessToken='" + accessToken + '\'' +
                '}';
    }
}
