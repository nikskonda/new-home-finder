package by.nikshkonda.newhomefinder.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Chat")
@Table(name = "chat", schema = "public")
public class Chat {

    @Id
    private Long id;

}
