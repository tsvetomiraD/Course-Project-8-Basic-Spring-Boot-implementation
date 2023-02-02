package rest.api.controllerInterface;

import my.spring.boot.ResponseEntity;
import my.spring.boot.annotations.*;
import rest.api.model.MyUser;
import rest.api.model.Token;

import java.util.Map;

@RequestMapping("/default")
public interface UserManagementInterface {
    @PostMapping("/login")
    public Token login(@RequestBody String info);

    @PostMapping("/register")
    public Token register(@RequestBody MyUser user);

    @PostMapping("/create")
    public MyUser createUser(@RequestBody MyUser user);

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@RequestBody int id);

    @GetMapping("/deactivate")
    public ResponseEntity<Map<String, Boolean>> deactivateUser(@RequestBody int id);
}
