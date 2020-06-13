package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MasterController {
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @GetMapping("/search")
    public String list(Model model) {
                model.addAttribute("profiles", profileRepository.findAll());
                
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("user", username);

        return "search";
    }
    
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
        
        Profile p = new Profile(name, username, profileString, passwordEncoder.encode(password));
        profileRepository.save(p);
        return "redirect:/search";
    }
}
