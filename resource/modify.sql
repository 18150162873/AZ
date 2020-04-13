alter table RECEIPTDETAIL add editflag varchar2(10) default 0;
alter table T_STOCKIN_RETURN add editflag varchar2(10) default 0;
alter table T_STOCKIN modify suppliername VARCHAR2(50);
alter table T_STOCKIN_RETURN modify suppliername VARCHAR2(50);
alter table T_STOCKIN_RETURN add notes VARCHAR2(1000);