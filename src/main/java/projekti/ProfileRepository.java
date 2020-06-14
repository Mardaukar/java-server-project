package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUsername(String username);
    List<Profile> findByNameContains(String name);
    Profile findByProfileString(String profileString);
}
