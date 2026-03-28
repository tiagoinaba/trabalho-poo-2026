create table if not exists regra_funcionamento (
    id integer primary key autoincrement,
    area_id integer not null,
    dia integer not null,
    permitido boolean not null check (permitido in (0, 1)),
    horario_inicio text,
    horario_limite text,
    created_at datetime default current_timestamp,

    unique(area_id, dia),
    foreign key (area_id) references area_comum(id) on delete cascade,
    
    check (
        (permitido = 0) or 
        (permitido = 1 and horario_inicio is not null and horario_limite is not null)
    )
);

create trigger if not exists trg_initialize_regras_funcionamento
after insert on area_comum
begin
    insert into regra_funcionamento (area_id, dia, permitido, horario_inicio, horario_limite)
    select 
        new.id, 
        dias.valor, 
        0,
        null,
        null
    from (
        select 1 as valor union all select 2 union all select 3 
        union all select 4 union all select 5 union all select 6 union all select 7
    ) as dias;
end;
