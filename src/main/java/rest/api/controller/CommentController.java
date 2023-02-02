package rest.api.controller;

import my.spring.boot.annotations.Autowired;
import my.spring.boot.annotations.RestController;
import rest.api.controllerInterface.CommentControllerInterface;
import rest.api.mapper.MyMapper;
import rest.api.model.Comment;

@RestController
public class CommentController implements CommentControllerInterface {
    @Autowired
    private MyMapper myMapper;

    public Comment[] getPostComments() {
        return myMapper.getAllComments();
    }
}
