--
-- PostgreSQL database dump
--

-- Dumped from database version 9.0.6
-- Dumped by pg_dump version 9.1.2
-- Started on 2012-04-19 16:09:28

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 453 (class 2612 OID 11574)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 142 (class 1259 OID 41694)
-- Dependencies: 5
-- Name: inst_log; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE inst_log (
    id character varying(40) NOT NULL,
    rectime timestamp without time zone,
    "time" timestamp without time zone,
    cmpnm character varying(100),
    hwid character varying(40),
    "os" character varying(40),
    osact character varying(20),
    clntipv4 character varying(16),
    srvipv4 character varying(16),
    sbxver character varying(20),
    op character varying(20),
    rslt character varying(20)
);


ALTER TABLE public.inst_log OWNER TO postgres;

--
-- TOC entry 1810 (class 0 OID 0)
-- Dependencies: 142
-- Name: TABLE inst_log; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE inst_log IS 'installation log';

-- Table: installer_info

-- DROP TABLE installer_info;

CREATE TABLE installer_info
(
  ID serial NOT NULL,
  versionid character varying NOT NULL,
  svnVersion character varying,
  buildNumber character varying,
  "path" character varying,
  release_date timestamp without time zone,
  CONSTRAINT installer_info_pkey PRIMARY KEY (ID )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE installer_info
  OWNER TO postgres;



--
-- TOC entry 143 (class 1259 OID 41699)
-- Dependencies: 5
-- Name: opt_log; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE  opt_log (
    --id character varying(40) NOT NULL,
    id bigserial NOT NULL,
    rectime timestamp without time zone,
    "time" timestamp without time zone,
    cmpnm character varying(100),
    hwid character varying(40),
    "os" character varying(40),
    osact character varying(20),
    cssact character varying(80),
    clntipv4 character varying(16),
    srvipv4 character varying(16),
    sbxver character varying(20),
    op character varying(20),
    fpth character varying(600),
    fsz bigint,
    file_version character varying(128),
    isfolder character varying(128),
    syncId integer ,
    rslt character varying(20),
    inst_id character varying(40) NOT NULL,
    reqid character varying(256)
);


ALTER TABLE public.opt_log OWNER TO postgres;


--
--web_opt_log
--
CREATE TABLE  web_opt_log (
---    id character varying(40) NOT NULL,
    id serial NOT NULL,
    rectime timestamp without time zone,
    "time" timestamp without time zone,
    cmpnm character varying(100),
    hwid character varying(40),
    "os" character varying(40),
    osact character varying(20),
    cssact character varying(80),
    clntipv4 character varying(16),
    srvipv4 character varying(16),
    sbxver character varying(20),
    op character varying(20),
    fpth character varying(600),
    fsz bigint,
    file_version character varying(128),
    isfolder character varying(128),
    syncId integer ,
    rslt character varying(20),
    inst_id character varying(40) NOT NULL,
    reqid character varying(256)
);


ALTER TABLE public.web_opt_log OWNER TO postgres;


--
-- TOC entry 144 (class 1259 OID 41712)
-- Dependencies: 5
-- Name: regt_log; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE regt_log (
    id character varying(40) NOT NULL,
    rectime timestamp without time zone,
    "time" timestamp without time zone,
    cmpnm character varying(100),
    hwid character varying(40),
    "os" character varying(40),
    osact character varying(20),
    cssact character varying(80),
    clntipv4 character varying(16),
    srvipv4 character varying(16),
    sbxver character varying(20),
    op character varying(20),
    rslt character varying(20)
);


ALTER TABLE public.regt_log OWNER TO postgres;

--
-- TOC entry 1798 (class 2606 OID 41706)
-- Dependencies: 143 143
-- Name: opt_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY opt_log
    ADD CONSTRAINT opt_pk PRIMARY KEY (id);
ALTER TABLE ONLY web_opt_log
    ADD CONSTRAINT web_opt_pk PRIMARY KEY (id);


--
-- TOC entry 1791 (class 2606 OID 41698)
-- Dependencies: 142 142
-- Name: pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY inst_log
    ADD CONSTRAINT pk PRIMARY KEY (id);


--
-- TOC entry 1803 (class 2606 OID 41716)
-- Dependencies: 144 144
-- Name: regt_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY regt_log
    ADD CONSTRAINT regt_pk PRIMARY KEY (id);


--
-- TOC entry 1787 (class 1259 OID 41720)
-- Dependencies: 142
-- Name: inst_log_OS_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "inst_log_OS_idx" ON inst_log USING btree ("os");


--
-- TOC entry 1788 (class 1259 OID 41719)
-- Dependencies: 142
-- Name: inst_log_rectime_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX inst_log_rectime_idx ON inst_log USING btree (rectime);


--
-- TOC entry 1789 (class 1259 OID 41721)
-- Dependencies: 142
-- Name: inst_log_rslt_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX inst_log_rslt_idx ON inst_log USING btree (rslt);


--
-- TOC entry 1792 (class 1259 OID 41728)
-- Dependencies: 143
-- Name: opt_log_cssact_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX opt_log_cssact_idx ON opt_log USING btree (cssact);
CREATE INDEX web_opt_log_cssact_idx ON web_opt_log USING btree (cssact);


--
-- TOC entry 1793 (class 1259 OID 41724)
-- Dependencies: 143
-- Name: opt_log_fsz_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX opt_log_fsz_idx ON opt_log USING btree (fsz);
CREATE INDEX web_opt_log_fsz_idx ON web_opt_log USING btree (fsz);


--
-- TOC entry 1794 (class 1259 OID 41723)
-- Dependencies: 143
-- Name: opt_log_op_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX opt_log_op_idx ON opt_log USING btree (op);
CREATE INDEX web_opt_log_op_idx ON web_opt_log USING btree (op);


--
-- TOC entry 1795 (class 1259 OID 41722)
-- Dependencies: 143
-- Name: opt_log_rectime_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX opt_log_rectime_idx ON opt_log USING btree (rectime);
CREATE INDEX web_opt_log_rectime_idx ON web_opt_log USING btree (rectime);


--
-- TOC entry 1796 (class 1259 OID 41725)
-- Dependencies: 143
-- Name: opt_log_rslt_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX opt_log_rslt_idx ON opt_log USING btree (rslt);
CREATE INDEX web_opt_log_rslt_idx ON web_opt_log USING btree (rslt);

--
CREATE INDEX opt_log_avoid_is_null ON opt_log USING btree (COALESCE(syncid,-100));

--
--
-- TOC entry 1799 (class 1259 OID 41727)
-- Dependencies: 144
-- Name: regt_log_cssact_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX regt_log_cssact_idx ON regt_log USING btree (cssact);


--
-- TOC entry 1800 (class 1259 OID 41726)
-- Dependencies: 144
-- Name: regt_log_rectime_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX regt_log_rectime_idx ON regt_log USING btree (rectime);


--
-- TOC entry 1801 (class 1259 OID 41729)
-- Dependencies: 144
-- Name: regt_log_rslt_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX regt_log_rslt_idx ON regt_log USING btree (rslt);


--
-- TOC entry 1804 (class 2606 OID 41707)
-- Dependencies: 1790 142 143
-- Name: opt_ref_inst; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

--  ALTER TABLE ONLY opt_log
--  ADD CONSTRAINT opt_ref_inst FOREIGN KEY (inst_id) REFERENCES inst_log(id);


--
-- TOC entry 1809 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2012-04-19 16:09:28

--
-- PostgreSQL database dump complete
--



-- Table: lockfile

-- DROP TABLE lockfile;

CREATE TABLE lockfile
(
  cssact character(20),
  hwid character varying(50),
  fpath character varying(256) NOT NULL,
  status character varying(10),
  CONSTRAINT lockfile_pkey PRIMARY KEY (cssact )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE lockfile
  OWNER TO postgres;


-- Table: "syncInfo"

-- DROP TABLE "syncInfo";


CREATE TABLE syncInfo
(
  syncId serial NOT NULL,
  account character varying(128) NOT NULL,
  instanceKey character varying(128) NOT NULL,
  status character(128) NOT NULL, -- LOCK | SUCCESS |FAIL
  createTime timestamp without time zone NOT NULL,
  CONSTRAINT syncIdPK PRIMARY KEY (syncId )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE syncInfo
  OWNER TO postgres;
COMMENT ON COLUMN syncInfo.status IS 'LOCK | SUCCESS |FAIL ';
