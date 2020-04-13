create table t_stockin
(SERIALKEY number not null,
ADDDATE   date default sysdate,
ADDWHO    varchar2(20) default 'API',
EDITDATE  date,
EDITWHO   varchar2(20),
WHSEID VARCHAR2(30),
RECEIPTKEY VARCHAR2(30),
EXTERNRECEIPTKEY VARCHAR2(30),
STORERKEY VARCHAR2(30),
TYPE VARCHAR2(30),
SUPPLIERCODE VARCHAR2(30),
SUPPLIERNAME VARCHAR2(30),
SHIPFROMADDRESSLINE1 VARCHAR2(30),
UDF1 VARCHAR2(30),
C_SLOC VARCHAR2(30),
C_BZNO VARCHAR2(30),
C_SLOCNAME VARCHAR2(30),
RECEIPTLINENUMBER VARCHAR2(30),
SKU VARCHAR2(30),
MODEL VARCHAR2(30),
SUSR2 VARCHAR2(30),
SUSR4 VARCHAR2(30),
SUSR5 VARCHAR2(30),
SUSR3NAME VARCHAR2(30),
CATEGORY VARCHAR2(30),
QTYEXPECTED VARCHAR2(30),
LOTTABLE02 VARCHAR2(30),
WMSSTS  VARCHAR2(30)
)
;comment on column t_stockin.WHSEID
 is '仓库号';
comment on column t_stockin.RECEIPTKEY
 is 'WMS单号';
comment on column t_stockin.EXTERNRECEIPTKEY
 is '外部单号';
comment on column t_stockin.STORERKEY
 is '货主';
comment on column t_stockin.TYPE
 is '订单类型编码';
comment on column t_stockin.SUPPLIERCODE
 is '发货仓编码';
comment on column t_stockin.SUPPLIERNAME
 is '发货仓名称';
comment on column t_stockin.SHIPFROMADDRESSLINE1
 is '发货地址';
comment on column t_stockin.UDF1
 is '备注';
comment on column t_stockin.C_SLOC
 is '收货店仓编码';
comment on column t_stockin.C_BZNO
 is 'POS单号';
comment on column t_stockin.C_SLOCNAME
 is '收货店仓名称';
comment on column t_stockin.RECEIPTLINENUMBER
 is '行号';
comment on column t_stockin.SKU
 is 'SKU编码（款色码）';
comment on column t_stockin.MODEL
 is '款';
comment on column t_stockin.SUSR2
 is '品牌';
comment on column t_stockin.SUSR4
 is '年份';
comment on column t_stockin.SUSR5
 is '季节';
comment on column t_stockin.SUSR3NAME
 is '系列';
comment on column t_stockin.CATEGORY
 is '品类';
comment on column t_stockin.QTYEXPECTED
 is '应收数量';
comment on column t_stockin.LOTTABLE02
 is '批属性';
comment on column t_stockin.WMSSTS  
 is '错误标识';
comment on column t_stockin.ADDDATE 
 is '加入时间';
create sequence t_stockin_SEQ
minvalue 1
maxvalue 999999999999999999999999999999999999
start with 1
increment by 1
cache 20;

CREATE OR REPLACE TRIGGER t_stockin_TRIGGER BEFORE INSERT ON t_stockin FOR EACH ROW
DECLARE
  BEGIN
    IF :NEW.SERIALKEY IS NULL THEN
      SELECT t_stockin_SEQ.NEXTVAL
      INTO   :NEW.SerialKey
      FROM   dual;
        END IF;
  END;
  
  
  
  
  
  create table T_STOCKIN_RETURN
(SERIALKEY number not null,
ADDDATE   date default sysdate,
ADDWHO    varchar2(20) default 'API',
EDITDATE  date,
EDITWHO   varchar2(20),
WHSEID VARCHAR2(30),
RECEIPTKEY VARCHAR2(30),
EXTERNRECEIPTKEY VARCHAR2(30),
STORERKEY VARCHAR2(30),
TYPE VARCHAR2(30),
SUPPLIERCODE VARCHAR2(30),
SUPPLIERNAME VARCHAR2(30),
SHIPFROMADDRESSLINE1 VARCHAR2(30),
UDF1 VARCHAR2(30),
C_SLOC VARCHAR2(30),
C_BZNO VARCHAR2(30),
C_SLOCNAME VARCHAR2(30),
RECEIPTLINENUMBER VARCHAR2(30),
SKU VARCHAR2(30),
QTYEXPECTED VARCHAR2(30),
LOTTABLE02 VARCHAR2(30),
TOID VARCHAR2(30),
TOLOC VARCHAR2(30) 
)
;comment on column T_STOCKIN_RETURN.WHSEID
 is '仓库号';
comment on column T_STOCKIN_RETURN.RECEIPTKEY
 is 'WMS单号';
comment on column T_STOCKIN_RETURN.EXTERNRECEIPTKEY
 is '外部单号';
comment on column T_STOCKIN_RETURN.STORERKEY
 is '货主';
comment on column T_STOCKIN_RETURN.TYPE
 is '订单类型编码';
comment on column T_STOCKIN_RETURN.SUPPLIERCODE
 is '发货仓编码';
comment on column T_STOCKIN_RETURN.SUPPLIERNAME
 is '发货仓名称';
comment on column T_STOCKIN_RETURN.SHIPFROMADDRESSLINE1
 is '发货地址';
comment on column T_STOCKIN_RETURN.UDF1
 is '备注';
comment on column T_STOCKIN_RETURN.C_SLOC
 is '收货店仓编码';
comment on column T_STOCKIN_RETURN.C_BZNO
 is 'POS单号';
comment on column T_STOCKIN_RETURN.C_SLOCNAME
 is '收货店仓名称';
comment on column T_STOCKIN_RETURN.RECEIPTLINENUMBER
 is '行号';
comment on column T_STOCKIN_RETURN.SKU
 is 'SKU编码（款色码）';
comment on column T_STOCKIN_RETURN.QTYEXPECTED
 is '数量';
comment on column T_STOCKIN_RETURN.LOTTABLE02
 is '批属性';
comment on column T_STOCKIN_RETURN.TOID
 is '收货箱号';
comment on column T_STOCKIN_RETURN.TOLOC
 is '库位(SORTER)';
create sequence T_STOCKIN_RETURN_SEQ
minvalue 1
maxvalue 999999999999999999999999999999999999
start with 1
increment by 1
cache 20;

CREATE OR REPLACE TRIGGER T_STOCKIN_RETURN_TRIGGER BEFORE INSERT ON T_STOCKIN_RETURN FOR EACH ROW
DECLARE
  BEGIN
    IF :NEW.SERIALKEY IS NULL THEN
      SELECT T_STOCKIN_RETURN_SEQ.NEXTVAL
      INTO   :NEW.SerialKey
      FROM   dual;
        END IF;
  END;