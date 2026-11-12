create table member (
    id            bigint primary key,
    name          varchar(30)  not null,
    email         varchar(100) not null,
    status        varchar(10)  not null,      -- ACTIVE / DORMANT
    last_login_at timestamp    not null,
    dormant_at    timestamp
);
create index idx_member_status_login on member (status, last_login_at);

create table sales_order (
    id         bigint primary key,
    member_id  bigint      not null,
    amount     bigint      not null,
    status     varchar(10) not null,          -- PAID / CANCELED
    ordered_at timestamp   not null
);
create index idx_sales_order_ordered_at on sales_order (ordered_at);

create table daily_sales (
    sales_date   date   primary key,
    total_amount bigint not null,
    order_count  bigint not null
);

-- ShedLock 이 사용하는 락 테이블
create table shedlock (
    name       varchar(64)  not null primary key,
    lock_until timestamp(3) not null,
    locked_at  timestamp(3) not null,
    locked_by  varchar(255) not null
);
