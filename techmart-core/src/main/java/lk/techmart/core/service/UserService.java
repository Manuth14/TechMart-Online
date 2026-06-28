package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.DTO.UserDTO;

import java.util.List;

@Remote
public interface UserService {
    boolean validateUser(String email, String password);
}
