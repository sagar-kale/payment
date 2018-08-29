package com.sgr.domain;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * Created by Sagar Kale
 */
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "job_id")
    private int id;
    @Column(name = "name")
    @NotEmpty(message = "*Please Provide Job name")
    private String name;
}
