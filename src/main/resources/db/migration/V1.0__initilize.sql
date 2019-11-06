
    create table compra_divida (
       compra_divida_id varchar(255) not null,
        situacao varchar(255),
        transferencia_transferencia_id varchar(255) not null,
        primary key (compra_divida_id)
    );

    create table conta (
       conta_id varchar(255) not null,
        empresa_id varchar(255),
        limite decimal(19,2),
        saldo decimal(19,2),
        situacao varchar(255),
        solicitou_limite_emergencial varchar(255),
        primary key (conta_id)
    );

    create table empresa (
       empresa_id varchar(255) not null,
        cnpj varchar(255),
        quantidade_funcionarios integer,
        responsavel varchar(255),
        valor_mercado decimal(19,2),
        primary key (empresa_id)
    );

    create table emprestimo (
       emprestimo_id varchar(255) not null,
        situacao varchar(255),
        movimento_movimento_id varchar(255) not null,
        primary key (emprestimo_id)
    );

    create table movimento (
       movimento_id varchar(255) not null,
        origem varchar(255),
        situacao varchar(255),
        tipo varchar(255),
        valor decimal(19,2),
        conta_conta_id varchar(255),
        primary key (movimento_id)
    );

    create table transferencia (
       transferencia_id varchar(255) not null,
        situacao varchar(255),
        credito_movimento_id varchar(255),
        debito_movimento_id varchar(255),
        primary key (transferencia_id)
    );

    alter table compra_divida 
       add constraint UK_j42h2absmxy1g3ssrhphpg1rv unique (transferencia_transferencia_id);

    alter table emprestimo 
       add constraint UK_ausynpdiblghvm51hu0mlndrg unique (movimento_movimento_id);

    alter table compra_divida 
       add constraint FK7fremiwd38dtb7ejq1od7j2rs 
       foreign key (transferencia_transferencia_id) 
       references transferencia;

    alter table emprestimo 
       add constraint FKiivf045grlmawtjrk98b28i3l 
       foreign key (movimento_movimento_id) 
       references movimento;

    alter table movimento 
       add constraint FKmdohmgwsintfetosxhuv8obgq 
       foreign key (conta_conta_id) 
       references conta;

    alter table transferencia 
       add constraint FKlrsyef64jr2f527ke3e3d45ba 
       foreign key (credito_movimento_id) 
       references movimento;

    alter table transferencia 
       add constraint FKdrs7lx5dkathq2xj2v72x4vuu 
       foreign key (debito_movimento_id) 
       references movimento;
