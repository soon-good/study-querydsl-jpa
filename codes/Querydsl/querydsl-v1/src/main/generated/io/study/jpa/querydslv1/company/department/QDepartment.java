package io.study.jpa.querydslv1.company.department;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDepartment is a Querydsl query type for Department
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDepartment extends EntityPathBase<Department> {

    private static final long serialVersionUID = 125291295L;

    public static final QDepartment department = new QDepartment("department");

    public final StringPath deptName = createString("deptName");

    public final ListPath<io.study.jpa.querydslv1.company.employee.Employee, io.study.jpa.querydslv1.company.employee.QEmployee> employees = this.<io.study.jpa.querydslv1.company.employee.Employee, io.study.jpa.querydslv1.company.employee.QEmployee>createList("employees", io.study.jpa.querydslv1.company.employee.Employee.class, io.study.jpa.querydslv1.company.employee.QEmployee.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QDepartment(String variable) {
        super(Department.class, forVariable(variable));
    }

    public QDepartment(Path<? extends Department> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDepartment(PathMetadata metadata) {
        super(Department.class, metadata);
    }

}

