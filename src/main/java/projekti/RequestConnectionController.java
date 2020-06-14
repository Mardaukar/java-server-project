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
public class RequestConnectionController {
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private RequestRepository requestRepository;
    
    @Autowired
    private ConnectionRepository connectionRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @PostMapping("/request")
    public String requestConnection(@RequestParam String targetUsername) {
        Profile pageProfile = profileRepository.findByUsername(targetUsername);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile userProfile = profileRepository.findByUsername(username);      
        requestRepository.save(new Request(userProfile, pageProfile));
        return "redirect:/profiles/" + pageProfile.getProfileString();
    }
    
    @PostMapping("/remove")
    public String removeConnection(@RequestParam String targetUsername) {
        Profile pageProfile = profileRepository.findByUsername(targetUsername);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile userProfile = profileRepository.findByUsername(username);
        
        List<Connection> connections = connectionRepository.findAll();
        for (Connection connection: connections) {
            if (connection.getProfiles().contains(userProfile)) {
                if (connection.getProfiles().contains(pageProfile)) {
                    connectionRepository.delete(connection);
                }
            }
        }
 
        return "redirect:/profiles/" + pageProfile.getProfileString();
    }
    
    @PostMapping("/cancel")
    public String cancelRequest(@RequestParam String targetUsername) {
        Profile pageProfile = profileRepository.findByUsername(targetUsername);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile userProfile = profileRepository.findByUsername(username);
        Request r = requestRepository.findByRequestorAndReceiver(userProfile, pageProfile);
        requestRepository.delete(r);
        return "redirect:/profiles/" + pageProfile.getProfileString();
    }
    
    @PostMapping("/decline")
    public String declineRequest(@RequestParam String requestorUsername) {
        Profile pageProfile = profileRepository.findByUsername(requestorUsername);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile userProfile = profileRepository.findByUsername(username);
        Request r = requestRepository.findByRequestorAndReceiver(pageProfile, userProfile);
        requestRepository.delete(r);
        return "redirect:/profiles/" + pageProfile.getProfileString();
    }
    
    @PostMapping("/accept")
    public String acceptRequest(@RequestParam String requestorUsername) {
        Profile pageProfile = profileRepository.findByUsername(requestorUsername);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile userProfile = profileRepository.findByUsername(username);
        Request r = requestRepository.findByRequestorAndReceiver(pageProfile, userProfile);
        requestRepository.delete(r);
        
        Connection connection = new Connection(new ArrayList<>());
        connection.getProfiles().add(userProfile);
        connection.getProfiles().add(pageProfile);
        connectionRepository.save(connection);  
        
        return "redirect:/profiles/" + pageProfile.getProfileString();
    }
}
