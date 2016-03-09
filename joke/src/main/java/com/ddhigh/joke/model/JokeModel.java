package com.ddhigh.joke.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * @project android-s
 * @package com.ddhigh.joke.model
 * @user xialeistudio
 * @date 2016/3/7 0007
 */
public class JokeModel extends BaseModel {
    private List<String> images;
    private String text;
    private int viewCount;
    private int praiseCount;
    private int unpraiseCount;
    private int commentCount;
    private String userId;
    private UserModel user;

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getUnpraiseCount() {
        return unpraiseCount;
    }

    public void setUnpraiseCount(int unpraiseCount) {
        this.unpraiseCount = unpraiseCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void parse(JSONObject jsonObject) throws JSONException, ParseException {
        super.parse(jsonObject);
        if (jsonObject.has("images")) {
            JSONArray jsonArray = jsonObject.getJSONArray("images");
            for (int i = 0; i < jsonArray.length(); i++) {
                images.add(jsonArray.getString(i));
            }
        }
        if (jsonObject.has("praiseCount")) {
            praiseCount = jsonObject.getInt("praiseCount");
        }
        if (jsonObject.has("unpraiseCount")) {
            unpraiseCount = jsonObject.getInt("unpraiseCount");
        }
        if (jsonObject.has("commentCount")) {
            commentCount = jsonObject.getInt("commentCount");
        }
        if (jsonObject.has("viewCount")) {
            viewCount = jsonObject.getInt("viewCount");
        }
        if (jsonObject.has("text")) {
            text = jsonObject.getString("text");
        }
        if (jsonObject.has("userId")) {
            userId = jsonObject.getString("userId");
        }

        if (jsonObject.has("user")) {
            JSONObject userJson = jsonObject.getJSONObject("user");
            user.parse(userJson);
        }

    }

    public JokeModel() {
        images = new ArrayList<>();
        user = new UserModel();
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
