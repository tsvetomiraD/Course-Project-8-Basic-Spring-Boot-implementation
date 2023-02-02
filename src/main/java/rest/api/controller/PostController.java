package rest.api.controller;

import my.spring.boot.ResponseEntity;
import my.spring.boot.annotations.Autowired;
import my.spring.boot.annotations.RestController;
import rest.api.controllerInterface.PostControllerInterface;
import rest.api.mapper.MyMapper;
import rest.api.model.Comment;
import rest.api.model.Post;

import java.util.HashMap;
import java.util.Map;
@RestController
public class PostController implements PostControllerInterface {
    @Autowired
    private MyMapper myMapper;

    public Post[] getAllPosts() {
        return myMapper.getAllPosts();
    }

    public Post createPost(Post post)  {
        if (myMapper.getPostById(post.id)==null) {
            int id = myMapper.insertPost(post);
            return myMapper.getPostById(post.id);
        } else {
            throw new RuntimeException(); //todo exception
        }
    }

    public ResponseEntity<Post> getPostById(Integer id) {
        Post post = myMapper.getPostById(id);
        if(post==null) {
            throw new RuntimeException(); //todo
        }
        return ResponseEntity.ok(post);
    }

    public ResponseEntity<Post> updatePost(int id, Post post) {
        if (myMapper.updatePost(post, id) == 0) {
            throw new RuntimeException(); //todo
        }

        return ResponseEntity.ok(myMapper.getPostById(id));
    }

    public ResponseEntity<Map<String, Boolean>> deletePost( int id) {
        myMapper.deletePost(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    public Comment[] getPostCommentsFromPostById(Integer id) {
        return myMapper.getCommentsByPostId(id);
    }
}