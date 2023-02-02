package rest.api;

import my.spring.boot.annotations.Autowired;
import my.spring.boot.annotations.Component;
import rest.api.mapper.MyMapper;
import rest.api.model.MyUser;
import rest.api.model.Token;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Random;

@Component
public class UserService {
    @Autowired
    MyMapper myMapper;
    public Token login(String username, String password) {
        MyUser user = myMapper.getUserByUsername(username);
        if (user == null) {
            return null;
        }
        String saltedPass = null;// user.salt + password;
        if (!password.equals(user.password)) {
            throw new RuntimeException();
        }

        Token token = myMapper.getTokenByUserId(user.id);
        if (token == null) {
            token = new Token();
            token.userId = user.id;
            LocalDate date = LocalDate.now();
            token.createDate = Date.valueOf(date);
            token.expirationDate = Date.valueOf(date.plusMonths(1));
            Random rn = new Random();
            int i = rn.nextInt();

            //token.token = DigestUtils.sha1Hex(String.valueOf(i));
            myMapper.createToken(token);
        }
        return token;
    }

    public Token register(MyUser user) {
        String password = user.password;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        //var random = new SecureRandom();
        byte[] salt = new byte[16];
        //random.nextBytes(salt);
        md.update(salt);

        byte[] hashedPassword = md.digest(password.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashedPassword)
            sb.append(String.format("%02x", b));

        //user.password = sb.toString();
        user.salt = salt;
        myMapper.registerUser(user);
        return new Token();
    }

    public MyUser validateUser(String token) {
        if (token == null || !token.startsWith("Bearer "))
            return null;

        token = token.substring(7);
        Token tc = myMapper.getToken(token);

        return tc == null ? null : myMapper.getUserById(tc.userId);
    }
}
