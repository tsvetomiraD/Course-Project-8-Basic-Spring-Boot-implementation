package rest.api.mapper;

import org.apache.ibatis.annotations.*;
import rest.api.model.Comment;
import rest.api.model.MyUser;
import rest.api.model.Post;
import rest.api.model.Token;

@org.apache.ibatis.annotations.Mapper
public interface MyMapper {
    @Select("SELECT * FROM posts")
    public Post[] getAllPosts();

    @Select("SELECT * FROM posts WHERE id=#{id}")
    public Post getPostById(int id);

    @Delete("DELETE FROM posts WHERE id=#{id}")
    public int deletePost(int id);

    @Insert("INSERT INTO posts(userId, body, title) VALUES (#{userId},#{body},#{title}")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public int insertPost(Post post);

    @Update("UPDATE posts SET body = {body} WHERE id = #{id}")
    int updatePost(Post post, int id);

    @Select("SELECT * FROM comments")
    public Comment[] getAllComments();

    @Select("SELECT * FROM comments WHERE postId=#{id}")
    public Comment[] getCommentsByPostId(int id);

    @Select("SELECT * FROM users")
    public MyUser[] getUsers();

    @Select("SELECT * FROM users WHERE username=#{username}")
    public MyUser getUserByUsername(String username);

    @Select("SELECT * FROM users WHERE id=#{id}")
    public MyUser getUserById(int id);

    @Select("SELECT * FROM users WHERE username=#{name} AND password=#{password}")
    public boolean validUser(String name, String password);

    @Insert("INSERT INTO users(name, username, email, phone, password) VALUES (#{name},#{username},#{email},#{phone},#{password}")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public int registerUser(MyUser user);

    @Select("SELECT * FROM token WHERE userId=#{id}")
    public Token getTokenByUserId(int id);

    @Select("SELECT * FROM token WHERE token=#{token}")
    public Token getToken(String token);

    @Insert("INSERT INTO token(userId, token, createdDate, expirationDate) VALUES (#{userId},#{token},#{createdDate},#{expirationDate}")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public int createToken(Token token);

    @Insert("INSERT INTO users(username, password, role) VALUES (#{username},#{password},#{role}")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public int insertUser(MyUser user);

    @Delete("DELETE FROM users WHERE id=#{id}")
    public int deleteUser(int id);

    @Delete("DELETE FROM token WHERE userId=#{id}")
    public int deactivateUser(int id);

}
