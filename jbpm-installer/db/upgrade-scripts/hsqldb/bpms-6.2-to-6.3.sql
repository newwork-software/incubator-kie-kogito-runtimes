create table TaskVariableImpl (
    id bigint generated by default as identity (start with 1),
    modificationDate timestamp,
    name varchar(255),
    processId varchar(255),
    processInstanceId bigint,
    taskId bigint,
    type integer,
    value varchar(5000),
    primary key (id)
);

