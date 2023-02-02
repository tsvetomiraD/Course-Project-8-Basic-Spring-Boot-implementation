package rest.api.controllerInterface;

import my.spring.boot.annotations.GetMapping;
import my.spring.boot.annotations.RequestMapping;
import rest.api.model.Comment;

@RequestMapping("/comments")
public interface CommentControllerInterface {
    @GetMapping("/comments")
    public Comment[] getPostComments();
}
