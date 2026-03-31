create table if not exists reserva (
    id integer primary key autoincrement,
    area_id integer not null,
    nome_morador text not null,
    ap text not null,
    data date not null,
    created_at datetime default current_timestamp,

    unique(area_id, data),
    foreign key (area_id) references area_comum(id) on delete cascade
);
