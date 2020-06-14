package projekti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }
    
    @GetMapping("/login")
    public String customLogin() {
        return "login";
    }

    @PostMapping("/register")
    public String createNew(@RequestParam String name, @RequestParam String username, @RequestParam String profileString, @RequestParam String password) {
        if (profileRepository.findByUsername(username) != null) {
            return "redirect:/register";
        }
        if (profileRepository.findByProfileString(profileString) != null) {
            return "redirect:/register";
        }
        
        Profile p = new Profile(name, username, profileString, passwordEncoder.encode(password), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "");
        profileRepository.save(p);
        return "redirect:/wall";
    }
}
