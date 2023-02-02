package rest.api.controllerInterface;

import my.spring.boot.ResponseEntity;
import my.spring.boot.annotations.*;
import rest.api.model.Comment;
import rest.api.model.Post;

import java.util.Map;

@RequestMapping("/posts")
public interface PostControllerInterface {
    @GetMapping
    public Post[] getAllPosts();

    @PostMapping
    public Post createPost(@RequestBody Post post);

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Integer id);

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable int id, @RequestBody Post post);

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deletePost(@PathVariable int id);

    @GetMapping("/{id}/comments")
    public Comment[] getPostCommentsFromPostById(@PathVariable Integer id);
}
