package hu.loylos.loylbot.model;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
@Accessors(chain = true)
@Entity
public class Pin {

    @Id
    private Long id;
    private String url;
}
