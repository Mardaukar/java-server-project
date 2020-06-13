package projekti;

import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile extends AbstractPersistable<Long> {

    private String name;
    private String username;
    private String profileString;
    private String password;

}