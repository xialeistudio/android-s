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
    private Date createdAt;
    private Date updatedAt;
    private UserModel user;
//    '_id' => 'ID',
//            'text' => '内容',
//            'images' => '图片',
//            'viewCount' => '点击数',
//            'praiseCount' => '顶数',
//            'unpraiseCount' => '踩数',
//            'commentCount' => '评论数',
//            'userId' => '用户ID',
//            'createdAt' => '发表时间',
//            'updatedAt' => '更新时间',

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
        if (jsonObject.has("commentCount")) {
            commentCount = jsonObject.getInt("commentCount");
        }
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
        if (jsonObject.has("text")) {
            text = jsonObject.getString("text");
        }
        if (jsonObject.has("userId")) {
            userId = jsonObject.getString("userId");
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
