package projekti;

import java.util.ArrayList;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Controller
public class MasterController {
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private RequestRepository requestRepository;
    
    @Autowired
    private ConnectionRepository connectionRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @GetMapping("/wall")
    public String wall(Model model) {
        List<Post> posts = postRepository.findAllByOrderByPostedDesc();
        List<Connection> connections = connectionRepository.findAll();
        List<Post> connectedPosts = new ArrayList<>();
        List<Profile> connectedProfiles = new ArrayList<>();
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile userProfile = profileRepository.findByUsername(username);
        
        for (Connection connection: connections) {
            if (connection.getProfiles().contains(userProfile)) {
                for (Profile profile : connection.getProfiles()) {
                    if (!profile.equals(userProfile)) {
                        connectedProfiles.add(profile);
                    }
                }
            }
        }

        for (Post post : posts) {
            if (connectedProfiles.contains(post.getPoster()) || post.getPoster().equals(userProfile)) {
                connectedPosts.add(post);
            }
        }
        
        if (connectedPosts.size() < 25) {
            model.addAttribute("posts", connectedPosts);
        } else {
            model.addAttribute("posts", connectedPosts.subList(0, 25));
        }
        
        return "wall";
    }
    
    @GetMapping("/my_profile")
    public String myProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile p = profileRepository.findByUsername(username);
        String ps = p.getProfileString();    
        return "redirect:/profiles/" + ps;
    }
    
    @GetMapping("/profile")
    public String profile(@RequestParam String targetProfileString) {   
        return "redirect:/profiles/" + targetProfileString;
    }
    
    @GetMapping("/profiles/{profileString}")
    public String getProfile(Model model, @PathVariable String profileString) {
        Profile pageProfile = profileRepository.findByProfileString(profileString);
        model.addAttribute("profile", pageProfile);
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile userProfile = profileRepository.findByUsername(username);
        
        if(pageProfile.getUsername().equals(username)) {
            model.addAttribute("status", "user");
            return "profile";
        }
        
        List<Connection> connections = connectionRepository.findAll();
        for (Connection connection: connections) {
            if (connection.getProfiles().contains(userProfile)) {
                if (connection.getProfiles().contains(pageProfile)) {
                    model.addAttribute("status", "connected");
                    return "profile";
                }
            }
        }
        
        List<Request> receivedRequests = requestRepository.findByReceiver(userProfile);
        for (Request request : receivedRequests) {
            if (request.getRequestor().equals(pageProfile)) {
                model.addAttribute("status", "request_received");
                return "profile";
            }
        }
        
        List<Request> sentRequests = requestRepository.findByRequestor(userProfile);
        for (Request request : sentRequests) {
            if (request.getReceiver().equals(pageProfile)) {
                model.addAttribute("status", "request_sent");
                return "profile";
            }
        }     
        
        model.addAttribute("status", "not_connected");
        return "profile";
    }
    
    @GetMapping("/connections")
    public String connections(Model model) {
        List<Profile> requestingProfiles = new ArrayList<>();
        List<Profile> requestedProfiles = new ArrayList<>();
        List<Profile> connectedProfiles = new ArrayList<>();
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile userProfile = profileRepository.findByUsername(username);
        
        List<Request> sentRequests = requestRepository.findByRequestor(userProfile);
        for (Request request : sentRequests) {
            requestedProfiles.add(request.getReceiver());
        }
        
        List<Request> receivedRequests = requestRepository.findByReceiver(userProfile);
        for (Request request : receivedRequests) {
            requestingProfiles.add(request.getRequestor());
        }
        
        List<Connection> connections = connectionRepository.findAll();
        for (Connection connection: connections) {
            if (connection.getProfiles().contains(userProfile)) {
                for (Profile profile : connection.getProfiles()) {
                    if (!profile.equals(userProfile)) {
                        connectedProfiles.add(profile);
                    }
                }
            }
        }
        
        model.addAttribute("requestingProfiles", requestingProfiles);
        model.addAttribute("requestedProfiles", requestedProfiles);
        model.addAttribute("connectedProfiles", connectedProfiles);
        return "connections";
    }
    
    @GetMapping("/search")
    public String search() {
        return "search";
    }
    
    @PostMapping("/search")
    public String list(@RequestParam String name, Model model) {
        
        List<Profile> profiles = profileRepository.findAll();
        List<Profile> searchProfiles = new ArrayList<>();
        
        for (Profile profile : profiles) {
            if (profile.getName().toLowerCase().contains(name.toLowerCase())) {
                searchProfiles.add(profile);
            }
        }
        
        model.addAttribute("profiles", searchProfiles);      
        return "search";
    }
    
    @PostMapping("/wall")
    public String post(@RequestParam String post) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile p = profileRepository.findByUsername(username);
        
        LocalDateTime time = LocalDateTime.now();
        time.truncatedTo(ChronoUnit.SECONDS);
        postRepository.save(new Post(p, post, time));    
        return "redirect:/wall";
    }
}
