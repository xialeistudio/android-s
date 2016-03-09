package com.ddhigh.joke.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * @project android-s
 * @package com.ddhigh.joke.model
 * @user xialeistudio
 * @date 2016/3/9 0009
 */
public class CommentModel extends BaseModel {
    private String userId;
    private String text;
    private String jokeId;

    private JokeModel joke;
    private UserModel user;

    @Override
    public void parse(JSONObject object) throws JSONException, ParseException {
        super.parse(object);
        if (object.has("userId")) {
            userId = object.getString("userId");
        }
        if (object.has("text")) {
            text = object.getString("text");
        }
        if (object.has("jokeId")) {
            jokeId = object.getString("jokeId");
        }

        if (object.has("joke")) {
            joke.parse(object.getJSONObject("joke"));
        }
        if (object.has("user")) {
            user.parse(object.getJSONObject("user"));
        }
    }

    public CommentModel() {
        user = new UserModel();
        joke = new JokeModel();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getJokeId() {
        return jokeId;
    }

    public void setJokeId(String jokeId) {
        this.jokeId = jokeId;
    }

    public JokeModel getJoke() {
        return joke;
    }

    public void setJoke(JokeModel joke) {
        this.joke = joke;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
