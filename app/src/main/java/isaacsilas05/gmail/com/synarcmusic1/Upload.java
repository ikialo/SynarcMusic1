package isaacsilas05.gmail.com.synarcmusic1;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mKey;
    private String mUsername;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name, String imageUrl, String username) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mUsername = username;
        mName = name;
        mImageUrl = imageUrl;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
    @Exclude
    public String getKey(){
        return mKey;
    }

    @Exclude
    public void  setKey(String key){
        mKey = key;
    }
}
