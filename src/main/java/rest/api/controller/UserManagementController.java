package rest.api.controller;

import my.spring.boot.ResponseEntity;
import my.spring.boot.annotations.*;
import rest.api.Role;
import rest.api.UserService;
import rest.api.controllerInterface.UserManagementInterface;
import rest.api.mapper.MyMapper;
import rest.api.model.MyUser;
import rest.api.model.Token;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/manage")
public class UserManagementController implements UserManagementInterface {
    @Autowired
    UserService userService;

    @Autowired
    MyMapper mapper;

    public Token login(String info) {
        String[] res = info.split(", ");
        String username = res[0].split(":")[1];
        String pass = res[1].split(":")[1];
        return userService.login(username, pass);
    }

    public Token register(MyUser user) {
        return userService.register(user);
    }

    @Role(role = "admin")
    @PostMapping("/create")
    public MyUser createUser(MyUser user)  {
        MyUser user1 = mapper.getUserByUsername(user.username);
        if (user1 == null) {
            int id = mapper.insertUser(user);
            return user;
        } else {
            throw new RuntimeException(); //todo exception
        }
    }

    @Role(role = "admin")
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Boolean>> deleteUser(int id) {
        mapper.deleteUser(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    @Role(role = "admin")
    @GetMapping("/deactivate")
    public ResponseEntity<Map<String, Boolean>> deactivateUser(int id) {
        mapper.deactivateUser(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deactivated", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
