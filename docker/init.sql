create table if not exists image
(
    id           bigint auto_increment primary key,
    image_type   varchar(20)   not null,
    image_url    varchar(2000) not null,
    reference_id bigint        not null
);

create table if not exists member
(
    id               bigint auto_increment primary key,
    created_at       datetime(6)  not null,
    deleted_at       datetime(6)  null,
    updated_at       datetime(6)  not null,
    birth            varchar(255) null,
    email            varchar(254) not null,
    member_authority varchar(255) not null,
    nickname         varchar(30)  not null,
    constraint member_email_uk unique (email)
);

drop table if exists market_enrollment;

create table if not exists market_enrollment
(
    id                bigint auto_increment primary key,
    created_at        datetime(6)  not null,
    deleted_at        datetime(6)  null,
    updated_at        datetime(6)  not null,
    business_number   varchar(10)  not null,
    description       varchar(500) not null,
    end_time          time         not null,
    enrollment_status varchar(10)  not null,
    city              varchar(10)  not null,
    detail_address    varchar(80)  not null,
    district          varchar(10)  not null,
    market_name       varchar(20)  not null,
    open_date         date         not null,
    open_time         time         not null,
    owner_name        varchar(10)  not null,
    phone_number      varchar(20)  not null,
    member_id         bigint       not null,
    constraint business_number_uk unique (business_number),
    constraint market_enrollment_with_member_id_fk foreign key (member_id) references member (id)
);

create table if not exists market
(
    id                   bigint auto_increment primary key,
    created_at           datetime(6)  not null,
    deleted_at           datetime(6)  null,
    updated_at           datetime(6)  not null,
    description          varchar(500) not null,
    end_time             time         not null,
    city                 varchar(10)  not null,
    detail_address       varchar(80)  not null,
    district             varchar(10)  not null,
    open_time            time         not null,
    phone_number         varchar(20)  not null,
    market_enrollment_id bigint       not null,
    member_id            bigint       not null,
    constraint market_with_member_id_fk foreign key (member_id) references member (id),
    constraint market_with_market_enrollment_id_fk foreign key (market_enrollment_id) references market_enrollment (id)
);

create table if not exists orders
(
    id            bigint auto_increment primary key,
    created_at    datetime(6)  not null,
    deleted_at    datetime(6)  null,
    updated_at    datetime(6)  not null,
    bread_flavor  varchar(20)  not null,
    cake_category varchar(20)  not null,
    cake_height   varchar(10)  not null,
    cake_size     varchar(10)  not null,
    cream_flavor  varchar(20)  not null,
    requirements  varchar(500) null,
    hope_price    int          not null,
    member_id     bigint       not null,
    order_status  varchar(10)  not null,
    region        varchar(40)  not null,
    title         varchar(20)  not null,
    visit_date    datetime(6)  not null
);

create table if not exists offer
(
    id             bigint auto_increment primary key,
    created_at     datetime(6)  not null,
    deleted_at     datetime(6)  null,
    updated_at     datetime(6)  not null,
    content        varchar(500) not null,
    expected_price int          not null,
    market_id      bigint       not null,
    order_id       bigint       not null,
    constraint offer_with_order_id_fk foreign key (order_id) references orders (id)
);

create table if not exists comment
(
    id                bigint auto_increment primary key,
    created_at        datetime(6)  not null,
    deleted_at        datetime(6)  null,
    updated_at        datetime(6)  not null,
    content           varchar(500) not null,
    member_id         bigint       not null,
    offer_id          bigint       not null,
    parent_comment_id bigint       null,
    constraint comment_with_offer_id_fk foreign key (offer_id) references offer (id)
);

create table if not exists order_history
(
    id         bigint auto_increment primary key,
    created_at datetime(6) not null,
    deleted_at datetime(6) null,
    updated_at datetime(6) not null,
    market_id  bigint      not null,
    member_id  bigint      not null,
    order_id   bigint      not null,
    constraint order_history_with_order_id_fk foreign key (order_id) references orders (id)
);

create table if not exists token
(
    id            bigint auto_increment primary key,
    member_id     bigint       not null,
    refresh_token varchar(300) not null,
    constraint refresh_token_uk unique (refresh_token),
    constraint member_id_uk unique (member_id)
);

CREATE TABLE `follow` (
      id	BIGINT	NOT NULL auto_increment primary key,
      member_id	BIGINT	NOT NULL,
      market_id	BIGINT	NOT NULL,
      created_at	datetime(6)	NOT NULL,
      updated_at	datetime(6)	NOT NULL,
      deleted_at	datetime(6)	NULL
);

