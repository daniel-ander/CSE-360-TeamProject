package DisplayModels;

import java.util.List;

import Models.IPost;

public class PostWithPermissions {
    private final IPost post;
    private final List<String> permissions;

    public PostWithPermissions(IPost post, List<String> permissions) {
        this.post = post;
        this.permissions = permissions;
    }

    public IPost getPost() {
        return post;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}