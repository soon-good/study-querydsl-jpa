package io.studyjpa.manytoone.oneway_mapping.company;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "DEPT", schema = "public")
public class Department {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPT_ID")
    private Long id;

    @Column(name = "DEPT_NAME")
    private String name;

//    @OneToMany(mappedBy = "dept")
//    private List<Employee> employees = new ArrayList<>();
}
