
    create table if not exists Author (
        id bigint not null,
        consortium varchar(255),
        firstName varchar(35),
        lastName varchar(65),
        middleName varchar(35),
        suffix varchar(35),
        publication_id bigint not null,
        primary key (id)
    );

    create table if not exists BioProject (
        DTYPE varchar(31) not null,
        projectId bigint not null,
        accession varchar(20),
        archive integer,
        description clob,
        name varchar(255),
        title varchar(255),
        descriptionOther varchar(255),
        subType integer,
        organism_id bigint not null,
        primary key (projectId),
        unique (organism_id)
    );

    create table if not exists BioProject_locusTagPrefixes (
        BioProject_projectId bigint not null,
        locusTagPrefixes varchar(10)
    );

    create table if not exists Category (
        name varchar(64) not null,
        primary key (name)
    );

    create table if not exists Grant (
        id bigint not null,
        agencyAbbr varchar(20),
        agencyName varchar(35),
        grantId varchar(20),
        title varchar(255),
        bioProject_projectId bigint not null,
        primary key (id)
    );

    create table if not exists Link (
        DTYPE varchar(31) not null,
        id bigint not null,
        category varchar(255),
        label varchar(255),
        url varchar(1024),
        db varchar(32),
        dbId varchar(128),
        bioProject_projectId bigint not null,
        primary key (id)
    );

    create table if not exists Mapping (
        id bigint not null,
        linkName varchar(128),
        subjectType varchar(128),
        targetId varchar(64),
        url varchar(1024),
        bioProject_projectId bigint not null,
        category_name varchar(64),
        provider_id bigint not null,
        primary key (id)
    );

    create table if not exists Organism (
        id bigint not null,
        breed varchar(255),
        cultivar varchar(255),
        genomeSize bigint not null,
        genomeSizeUnits varchar(255),
        isolateName varchar(255),
        label varchar(255),
        organismName varchar(255),
        organization varchar(255),
        reproduction varchar(255),
        species integer,
        strain varchar(255),
        supergroup varchar(255),
        taxID integer,
        environment_id bigint,
        morphology_id bigint,
        phenotype_id bigint,
        sample_id bigint,
        primary key (id)
    );

    create table if not exists OrganismEnvironment (
        id bigint not null,
        habitat integer,
        optimumTemperature varchar(255),
        oxygenReq integer,
        salinity integer,
        temperatureRange integer,
        primary key (id)
    );

    create table if not exists OrganismMorphology (
        id bigint not null,
        endospores boolean,
        enveloped boolean,
        gram integer,
        motility boolean,
        primary key (id)
    );

    create table if not exists OrganismMorphology_shapes (
        OrganismMorphology_id bigint not null,
        shapes integer
    );

    create table if not exists OrganismPhenotype (
        id bigint not null,
        bioticRelationship integer,
        disease varchar(255),
        trophicLevel integer,
        primary key (id)
    );

    create table if not exists OrganismSample (
        id bigint not null,
        cultureSampleInfo integer,
        isolatedCell boolean,
        tissueSample boolean,
        primary key (id)
    );

    create table if not exists ProjectRelevance (
        id bigint not null,
        relevanceDescription varchar(255),
        relevantField integer,
        bioProject_projectId bigint not null,
        primary key (id)
    );

    create table if not exists Provider (
        id bigint not null,
        abbr varchar(32),
        name varchar(128),
        url varchar(1024),
        primary key (id)
    );

    create table if not exists Publication (
        id bigint not null,
        dbType integer,
        freeFormCitation clob,
        issue varchar(10),
        journalTitle varchar(255),
        pagesFrom varchar(10),
        pagesTo varchar(10),
        publicationDate timestamp,
        publicationId varchar(255),
        publicationStatus integer,
        title varchar(255),
        volume varchar(10),
        year varchar(10),
        bioProject_projectId bigint not null,
        primary key (id)
    );

    create table if not exists UserTerm (
        id bigint not null,
        category varchar(255),
        term varchar(255),
        units varchar(32),
        value varchar(255),
        bioProject_projectId bigint not null,
        primary key (id)
    );

    alter table Author
        add constraint if not exists FK75920DAB96FF7083
        foreign key (publication_id)
        references Publication;

    alter table BioProject
        add constraint if not exists FK266C0911320076D1
        foreign key (organism_id)
        references Organism;

    alter table BioProject_locusTagPrefixes
        add constraint if not exists FKD555592A2D65A5CE
        foreign key (BioProject_projectId)
        references BioProject;

    alter table Grant
        add constraint if not exists FK41DD0FC2D65A5CE
        foreign key (bioProject_projectId)
        references BioProject;

    alter table Link
        add constraint if not exists FK24241A2D65A5CE
        foreign key (bioProject_projectId)
        references BioProject;

    alter table Mapping
        add constraint if not exists FK9524B0AE2D65A5CE
        foreign key (bioProject_projectId)
        references BioProject;

    alter table Mapping
        add constraint if not exists FK9524B0AE60AE1C5C
        foreign key (category_name)
        references Category;

    alter table Mapping
        add constraint if not exists FK9524B0AE89B05A4C
        foreign key (provider_id)
        references Provider;

    alter table Organism
        add constraint if not exists FK5250E4F2614C89F1
        foreign key (environment_id)
        references OrganismEnvironment;

    alter table Organism
        add constraint if not exists FK5250E4F29871E843
        foreign key (morphology_id)
        references OrganismMorphology;

    alter table Organism
        add constraint if not exists FK5250E4F27903AF91
        foreign key (phenotype_id)
        references OrganismPhenotype;

    alter table Organism
        add constraint if not exists FK5250E4F2D901D203
        foreign key (sample_id)
        references OrganismSample;

    alter table OrganismMorphology_shapes
        add constraint if not exists FK61302D23267ACCB1
        foreign key (OrganismMorphology_id)
        references OrganismMorphology;

    alter table ProjectRelevance
        add constraint if not exists FKFF6DB4602D65A5CE
        foreign key (bioProject_projectId)
        references BioProject;

    alter table Publication
        add constraint if not exists FK23254A0C2D65A5CE
        foreign key (bioProject_projectId)
        references BioProject;

    alter table UserTerm
        add constraint if not exists FKF3F82AF72D65A5CE
        foreign key (bioProject_projectId)
        references BioProject;

    create sequence if not exists hibernate_sequence start with 1 increment by 1;
