package io.studyjpa.manytoone.oneway_mapping.company;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "EMP", schema = "public")
public class Employee {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMP_ID")
    private Long id;

    @Column(name = "EMP_NAME")
    private String name;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID")
    private Department dept;
}
